package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.grpc.ImageClassifierGrpc;
import ar.utn.ccaffa.grpc.ImageRequest;
import ar.utn.ccaffa.grpc.ImageResponse;
import ar.utn.ccaffa.grpc.ImageResponses;
import ar.utn.ccaffa.model.dto.AnalysisResponse;
import ar.utn.ccaffa.model.entity.ControlDeCalidad;
import ar.utn.ccaffa.model.entity.Defecto;
import ar.utn.ccaffa.repository.interfaces.ControlDeCalidadRepository;
import ar.utn.ccaffa.services.interfaces.AnalysisService;
import ar.utn.ccaffa.services.interfaces.FileStorageService;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final SimpMessagingTemplate messagingTemplate;
    private final FileStorageService fileStorageService;
    private final ControlDeCalidadRepository controlDeCalidadRepository;

    @Override
    public void analyzeAndNotify(MultipartFile file, String id, String cameraId) throws IOException {
        byte[] fileBytes = file.getBytes();
        processAnalysis(fileBytes, file.getOriginalFilename(), file.getContentType(), id, cameraId);
    }

    @Async
    @Override
    public void processAnalysis(byte[] fileBytes, String originalFilename, String contentType, String id, String cameraId) {
        String effectiveCameraId = (cameraId == null || cameraId.isBlank()) ? "cam-1" : cameraId;
        log.info("Iniciando análisis asíncrono para el archivo: {} (cameraId={})", originalFilename, effectiveCameraId);
        ManagedChannel channel = null;
        try {
            // 1. Conectarse al servidor gRPC
            channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                    .usePlaintext()
                    .build();

            // 2. Crear el stub
            ImageClassifierGrpc.ImageClassifierBlockingStub stub = ImageClassifierGrpc.newBlockingStub(channel);

            // 3. Construir la petición
            ByteString imageBytes = ByteString.copyFrom(fileBytes);
            ImageRequest request = ImageRequest.newBuilder()
                    .setImage(imageBytes)
                    .build();

            // 4. Llamar al servicio gRPC
            log.info("Enviando imagen a análisis a través de gRPC (cameraId={})", effectiveCameraId);
            ImageResponses response = stub.classifyImage(request);

            // 5. Procesar la respuesta
            if (response != null && response.getFound()) {
                log.info("¡Defecto detectado! Detalles: {} (cameraId={})", response.getResponsesList(), effectiveCameraId);

                // Buscar el control de calidad y añadir el nuevo defecto
                ControlDeCalidad controlDeCalidad = controlDeCalidadRepository.findById(Long.valueOf(id))
                        .orElseThrow(() -> new RuntimeException("Control de Calidad no encontrado con ID: " + id));
                StringBuilder defectDescriptions = new StringBuilder();
                defectDescriptions.append("Defectos detectados:").append("\n");
                for (ImageResponse defDesc : response.getResponsesList()) {
                    if (defDesc.getProbability() > 0.8 && !defDesc.getClassName().equals("normal")) {
                        String desc = String.format("  Defecto: %s (Probabilidad: %.2f)", defDesc.getClassName(), defDesc.getProbability());
                        defectDescriptions.append(desc).append("\n");
                    }
                }

                // 6. Modificar la imagen para añadir la descripción y guardarla
                byte[] imageWithDescription = createImageWithDescription(fileBytes, defectDescriptions.toString());
                String filePath = fileStorageService.save(imageWithDescription, originalFilename, effectiveCameraId);
                log.info("Imagen con defecto y descripción guardada en: {}", filePath);


                Defecto defecto = new Defecto();
                defecto.setImagen(filePath.replace("\\", "/"));
                defecto.setFecha(LocalDate.now());
                defecto.setTipo("Defecto de Fleje");
                defecto.setDescripcion(defectDescriptions.toString());
                defecto.setEsRechazado(false);
                defecto.setControlDeCalidad(controlDeCalidad); // Establecer la relación

                controlDeCalidad.getDefectos().add(defecto);

                // 7. Construir y enviar notificación por WebSocket
                AnalysisResponse analysis = new AnalysisResponse();
                analysis.setDefect(true);
                analysis.setDetails("Defecto detectado en " + originalFilename);
                analysis.setImageId(filePath.replace("\\", "/"));
                analysis.setId(defecto.getId());
                analysis.setCameraId(effectiveCameraId);

                // Guardar la entidad actualizada
                controlDeCalidadRepository.save(controlDeCalidad);
                log.info("Defecto guardado para el control de calidad ID: {} (cameraId={})", id, effectiveCameraId);

                // Enviar notificaciones por WebSocket: general y por cámara
                messagingTemplate.convertAndSend("/topic/defects", analysis);
                messagingTemplate.convertAndSend("/topic/defects/" + effectiveCameraId, analysis);
                log.info("Notificaciones enviadas a /topic/defects y /topic/defects/{}", effectiveCameraId);
            }
        } catch (StatusRuntimeException e) {
            log.error("Error durante la llamada gRPC: {}", e.getStatus(), e);
        } catch (Exception e) {
            log.error("Error inesperado durante el análisis asíncrono", e);
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
    }

    private byte[] createImageWithDescription(byte[] originalImageBytes, String description) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(originalImageBytes)) {
            BufferedImage originalImage = ImageIO.read(bais);
            if (originalImage == null) {
                throw new IOException("No se pudo decodificar la imagen.");
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int sidebarWidth = 400; // Ancho del panel para el texto
            int newWidth = originalWidth + sidebarWidth;

            // Crear una nueva imagen más ancha
            BufferedImage newImage = new BufferedImage(newWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newImage.createGraphics();

            // Rellenar el fondo del panel de texto con blanco
            g2d.setColor(Color.WHITE);
            g2d.fillRect(originalWidth, 0, sidebarWidth, originalHeight);

            // Dibujar la imagen original en la nueva
            g2d.drawImage(originalImage, 0, 0, null);

            // Configurar fuente y color para el texto
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));

            // Dibujar el texto línea por línea
            int y = 30; // Posición Y inicial
            for (String line : description.split("\n")) {
                g2d.drawString(line, originalWidth + 20, y);
                y += 30; // Espacio entre líneas
            }

            g2d.dispose();

            // Convertir la nueva imagen a byte[]
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(newImage, "jpg", baos);
                return baos.toByteArray();
            }
        }
    }

    @Async
    @Override
    public void analyzeAndNotifyMock(MultipartFile file) {
        log.info("Iniciando análisis asíncrono para el archivo: {}", file.getOriginalFilename());
        try {
            // --- MOCK DE ANÁLISIS DE PYTHON ---
            // 1. Simular tiempo de procesamiento
            Thread.sleep(2000); // Pausa de 2 segundos

            // 2. Decidir aleatoriamente si se encontró un defecto (1 de cada 5 veces)
            boolean defectFound = new Random().nextInt(2) == 0;

            AnalysisResponse analysis = new AnalysisResponse();
            analysis.setDefect(defectFound);

            if (analysis.isDefect()) {
                analysis.setDetails("Falla simulada detectada en el frame: " + file.getOriginalFilename());
                log.info("¡Defecto simulado detectado! Detalles: {}", analysis.getDetails());

                // 3. Guardar la imagen en GridFS
                //Object fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
                //log.info("Imagen con defecto guardada en GridFS con ID: {}", fileId.toString());
                analysis.setImageId("mock-image-id");

                // 4. Enviar notificación por WebSocket
                messagingTemplate.convertAndSend("/topic/defects", analysis);
                log.info("Notificación de defecto enviada a /topic/defects");
            } else {
                log.info("No se encontraron defectos en el frame: {}", file.getOriginalFilename());
            }
            // --- FIN DEL MOCK ---

        } catch (InterruptedException e) {
            log.error("Error durante el análisis asíncrono simulado", e);
            Thread.currentThread().interrupt(); // Buena práctica al capturar InterruptedException
        } catch (Exception e) {
            log.error("Error inesperado durante el análisis asíncrono", e);
        }
    }
}
