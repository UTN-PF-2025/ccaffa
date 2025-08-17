package ar.utn.ccaffa.web;

import ar.utn.ccaffa.model.dto.ClienteDto;
import ar.utn.ccaffa.model.entity.Cliente;
import ar.utn.ccaffa.services.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDto>> getAllClientes() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> getClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody ClienteDto clienteDto) {
        return new ResponseEntity<>(clienteService.save(clienteDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody ClienteDto clienteDto) {
        clienteDto.setId(id);
        return ResponseEntity.ok(clienteService.save(clienteDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}