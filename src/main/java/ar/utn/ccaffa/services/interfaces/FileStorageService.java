package ar.utn.ccaffa.services.interfaces;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface FileStorageService {

    /**
     * Guarda un archivo en el sistema de archivos local.
     *
     * @param fileBytes El contenido del archivo como un array de bytes.
     * @param originalFilename El nombre original del archivo.
     * @return El nombre único del archivo guardado.
     * @throws IOException Si ocurre un error durante la escritura del archivo.
     */
    String save(byte[] fileBytes, String originalFilename) throws IOException;

    /**
     * Guarda un archivo en el sistema de archivos local bajo un subdirectorio de cámara.
     * Si {@code cameraId} es nulo o vacío, se usa el directorio raíz.
     *
     * @param fileBytes El contenido del archivo como un array de bytes.
     * @param originalFilename El nombre original del archivo.
     * @param cameraId Identificador de la cámara (ej. cam-1).
     * @return La ruta relativa (respecto del root) donde quedó guardado (por ejemplo, "cam-1/uuid_nombre.jpg").
     * @throws IOException Si ocurre un error durante la escritura del archivo.
     */
    String save(byte[] fileBytes, String originalFilename, String cameraId) throws IOException;

    /**
     * Carga un archivo como un recurso.
     *
     * @param filename El nombre del archivo a cargar.
     * @return El recurso correspondiente al archivo.
     */
    Resource loadAsResource(String filename);
}

