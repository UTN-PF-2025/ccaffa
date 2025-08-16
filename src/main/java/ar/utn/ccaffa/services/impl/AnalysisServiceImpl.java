package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.grpc.ImageClassifierGrpc;
import ar.utn.ccaffa.grpc.ImageRequest;
import ar.utn.ccaffa.grpc.ImageResponses;
import ar.utn.ccaffa.model.dto.AnalysisResponse;
import ar.utn.ccaffa.services.interfaces.AnalysisService;
import ar.utn.ccaffa.services.interfaces.FileStorageService;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final GridFsTemplate gridFsTemplate; // Se mantiene por si se usa en otro lado
    private final SimpMessagingTemplate messagingTemplate;
    private final FileStorageService fileStorageService;


    @Override
    public void analyzeAndNotify(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        processAnalysis(fileBytes, file.getOriginalFilename(), file.getContentType());
    }

    @Async
    @Override
    public void processAnalysis(byte[] fileBytes, String originalFilename, String contentType) {
        log.info("Iniciando análisis asíncrono para el archivo: {}", originalFilename);
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
            log.info("Enviando imagen a análisis a través de gRPC");
            ImageResponses response = stub.classifyImage(request);

            // 5. Procesar la respuesta
            if (response != null && response.getFound()) {
                log.info("¡Defecto detectado! Detalles: {}", response.getResponsesList());

                // 6. Guardar la imagen en el sistema de archivos
                String filePath = fileStorageService.save(fileBytes, originalFilename);
                log.info("Imagen con defecto guardada en: {}", filePath);

                // 7. Enviar notificación por WebSocket
                AnalysisResponse analysis = new AnalysisResponse();
                analysis.setDefect(true);
                analysis.setDetails("Defecto detectado en " + originalFilename);
                analysis.setImageId(filePath); // Usamos la ruta del archivo como ID
                messagingTemplate.convertAndSend("/topic/defects", analysis);
                log.info("Notificación de defecto enviada a /topic/defects");
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
