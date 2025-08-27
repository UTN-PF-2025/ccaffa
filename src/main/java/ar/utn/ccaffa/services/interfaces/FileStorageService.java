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
     * Carga un archivo como un recurso.
     *
     * @param filename El nombre del archivo a cargar.
     * @return El recurso correspondiente al archivo.
     */
    Resource loadAsResource(String filename);
}

