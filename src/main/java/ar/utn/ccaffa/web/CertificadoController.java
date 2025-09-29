package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.CertificadoDeCalidadDto;
import ar.utn.ccaffa.model.dto.CertificadoRequestDTO;
import ar.utn.ccaffa.services.interfaces.CertificadoCalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificados")
@RequiredArgsConstructor
public class CertificadoController {

    private final CertificadoCalidadService certificadoCalidadService;

    @PostMapping
    public ResponseEntity<String> generar(@RequestBody CertificadoRequestDTO certificadoRequest) {
        certificadoCalidadService.generarCertificado(certificadoRequest);
        return ResponseEntity.ok("Certificado generado");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CertificadoDeCalidadDto> getCertificado(@PathVariable Long id) {
        return ResponseEntity.ok(this.certificadoCalidadService.findById(id));
    }

}
