package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.model.dto.ProveedorDto;
import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.model.entity.Proveedor;
import ar.utn.ccaffa.services.interfaces.ProveedorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@Slf4j
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<List<ProveedorDto>> getAllProveedores() {
        log.info("Obteniendo todos los proveedores");
        List<ProveedorDto> proveedores = proveedorService.findAll();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDto> getProveedorById(@PathVariable Long id) {
        log.info("Buscando proveedor con ID: {}", id);
        return ResponseEntity.ok(proveedorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Proveedor> createProveedor(@RequestBody ProveedorDto proveedor) {
        log.info("Creando nuevo proveedor: {}", proveedor);
        Proveedor savedProveedor = proveedorService.save(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> updateProveedor(@PathVariable Long id, @RequestBody ProveedorDto proveedor) {
        proveedor.setId(id);
        return ResponseEntity.ok(proveedorService.save(proveedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProveedor(@PathVariable Long id) {
        log.info("Eliminando proveedor con ID: {}", id);
        if (proveedorService.deleteById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}