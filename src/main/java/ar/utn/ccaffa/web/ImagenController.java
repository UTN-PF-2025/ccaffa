package ar.utn.ccaffa.web;

import ar.utn.ccaffa.services.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;

    @PostMapping
    public ResponseEntity<String> subirImagen(@RequestParam("imagen") MultipartFile archivo) {
        try {
            String id = imagenService.guardarImagen(archivo);
            return ResponseEntity.ok(id);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al subir la imagen: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerImagen(@PathVariable String id) {
        try {
            byte[] imagen = imagenService.obtenerImagen(id);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imagen);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable String id) {
        imagenService.eliminarImagen(id);
        return ResponseEntity.ok().build();
    }
}