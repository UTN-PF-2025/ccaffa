package ar.utn.ccaffa.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.utn.ccaffa.model.dto.ControlDeCalidadDto;
import ar.utn.ccaffa.model.dto.ControlDeCalidadRequestDto;
import ar.utn.ccaffa.services.interfaces.ControlCalidadService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/control-calidad")
public class ControlCalidadController {

    private final ControlCalidadService controlCalidadService;

    public ControlCalidadController(ControlCalidadService controlCalidadService) {
        this.controlCalidadService = controlCalidadService;
    }

    @PostMapping("/iniciar")
    public String iniciarCalidadProceso(@RequestBody ControlDeCalidadRequestDto controlCalidadRequestDto) {
        controlCalidadService.iniciar(controlCalidadRequestDto.getOrdenTrabajoId(), controlCalidadRequestDto.getMedidas());
        return "Control de calidad iniciado";
    }
}
