package ar.utn.ccaffa.services.impl;

import ar.utn.ccaffa.model.dto.AnalysisResponse;
import ar.utn.ccaffa.services.interfaces.AnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final RestTemplate restTemplate;
    private final GridFsTemplate gridFsTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    @Async
    @Override
    public void analyzeAndNotify(MultipartFile file) {
        log.info("Iniciando análisis asíncrono para el archivo: {}", file.getOriginalFilename());
        try {
            // 1. Preparar la petición para el servicio de Python
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileAsResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 2. Llamar al servicio de Python
            log.info("Enviando a análisis a: {}", pythonServiceUrl);
            ResponseEntity<AnalysisResponse> response = restTemplate.postForEntity(pythonServiceUrl, requestEntity, AnalysisResponse.class);

            // 3. Procesar la respuesta
            AnalysisResponse analysis = response.getBody();
            if (analysis != null && analysis.isDefect()) {
                log.info("¡Defecto detectado! Detalles: {}", analysis.getDetails());

                // 4. Guardar la imagen en GridFS
                Object fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
                log.info("Imagen con defecto guardada en GridFS con ID: {}", fileId.toString());
                analysis.setImageId(fileId.toString());

                // 5. Enviar notificación por WebSocket
                messagingTemplate.convertAndSend("/topic/defects", analysis);
                log.info("Notificación de defecto enviada a /topic/defects");
            }

        } catch (IOException e) {
            log.error("Error de IO durante el análisis asíncrono", e);
        } catch (Exception e) {
            log.error("Error inesperado durante el análisis asíncrono", e);
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
