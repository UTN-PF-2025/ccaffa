package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.entity.Maquina;
import ar.utn.ccaffa.model.entity.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ar.utn.ccaffa.model.dto.MaquinaDto;
import ar.utn.ccaffa.services.interfaces.MaquinaService;

import java.util.List;

@RestController
@RequestMapping("/api/maquinas")
public class MaquinaController {

    private final MaquinaService maquinaService;

    @Autowired
    public MaquinaController(MaquinaService maquinaService) {
        this.maquinaService = maquinaService;
    }

    @GetMapping
    public ResponseEntity<List<MaquinaDto>> obtenerTodos() {
        List<MaquinaDto> entidades = maquinaService.findAll();
        return new ResponseEntity<>(entidades, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaDto> obtenerPorId(@PathVariable Long id) {
        MaquinaDto entidad = maquinaService.findById(id);
        return new ResponseEntity<>(entidad, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Maquina> crear(@RequestBody MaquinaDto entidad) {
        Maquina nuevaEntidad = maquinaService.save(entidad);
        return new ResponseEntity<>(nuevaEntidad, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Maquina> updateMaquina(@PathVariable Long id, @RequestBody MaquinaDto maquina) {
        maquina.setId(id);
        return ResponseEntity.ok(maquinaService.save(maquina));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        maquinaService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}