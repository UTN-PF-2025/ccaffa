package ar.utn.ccaffa.web;

import ar.utn.ccaffa.services.interfaces.OrdenDeTrabajoMaquinaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordenes-trabajo-maquina")
public class OrdenDeTrabajoMaquinaController {

    private OrdenDeTrabajoMaquinaService ordenDeTrabajoMaquinaService;

    @PostMapping(value = "/iniciar")
    public ResponseEntity<Void> iniciar(@RequestBody Long id) {
        this.ordenDeTrabajoMaquinaService.iniciarOrden(id);
        return ResponseEntity.ok().build();
    }
}
