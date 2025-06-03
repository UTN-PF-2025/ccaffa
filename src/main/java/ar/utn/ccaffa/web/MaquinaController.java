package ar.utn.ccaffa.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        List<MaquinaDto> entidades = maquinaService.obtenerTodos();
        return new ResponseEntity<>(entidades, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaDto> obtenerPorId(@PathVariable Long id) {
        MaquinaDto entidad = maquinaService.obtenerPorId(id);
        return new ResponseEntity<>(entidad, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MaquinaDto> crear(@RequestBody MaquinaDto entidad) {
        MaquinaDto nuevaEntidad = maquinaService.guardar(entidad);
        return new ResponseEntity<>(nuevaEntidad, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        maquinaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}