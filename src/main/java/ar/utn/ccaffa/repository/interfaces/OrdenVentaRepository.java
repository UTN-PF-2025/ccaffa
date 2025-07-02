package ar.utn.ccaffa.repository.interfaces;

import ar.utn.ccaffa.model.entity.Cliente;
import ar.utn.ccaffa.model.entity.OrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Long> {

    List<OrdenVenta> findByFechaCreacion(LocalDate fechaCreacion);

    List<OrdenVenta> findByFechaCreacionAfter(LocalDate fecha);

    List<OrdenVenta> findByFechaCreacionBefore(LocalDate fecha);

    List<OrdenVenta> findByFechaCreacionBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<OrdenVenta> findByEstado(String estado);

    List<OrdenVenta> findByEstadoIn(List<String> estados);

    Optional<OrdenVenta> findByOrderId(Long orderId);

    List<OrdenVenta> findByCliente(Cliente cliente);

    List<OrdenVenta> findByClienteId(Long clienteId);

    List<OrdenVenta> findByObservacionesContainingIgnoreCase(String texto);

    List<OrdenVenta> findByClienteAndEstado(Cliente cliente, String estado);

    List<OrdenVenta> findByOrderIdAndEstado(Long orderId, String estado);

    List<OrdenVenta> findByClienteAndObservacionesContainingIgnoreCase(Cliente cliente, String texto);

}
