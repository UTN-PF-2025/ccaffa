package ar.utn.ccaffa;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import ar.utn.ccaffa.enums.TipoMaterial;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.planner.Pair;
import ar.utn.ccaffa.planner.PlannerGA;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import test.JeneticsGA;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@SpringBootTest
class CcaffaApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void planner() {
		Cliente cliente = Cliente.builder()
				.id(1l)
				.activo(true)
				.email("hola@email.com")
				.name("jose")
				.build();

		Especificacion especificacion1 = Especificacion.builder()
				.id(1l)
				.ancho(30F)
				.espesor(20F)
				.cantidad(50)
				.tipoMaterial(TipoMaterial.LAMINADO_EN_FRIO)
				.pesoMaximoPorRollo(10F)
				.tipoDeEmbalaje("TEST")
				.toleranciaAncho(1F)
				.toleranciaEspesor(1F)
				.diametroInterno(1F)
				.diametroExterno(2F)
				.build();

		OrdenVenta ordenVenta1 = OrdenVenta.builder()
				.id(1L)
				.orderId(1L)
				.fechaCreacion(LocalDateTime.of(2025, 8, 22, 9, 5, 0))
				.fechaEntregaEstimada(LocalDateTime.of(2025, 8, 22,18 , 0, 0))
				.estado("Creada")
				.observaciones("")
				.cliente(cliente)
				.especificacion(especificacion1)
				.build();

		OrdenVenta ordenVenta2 = OrdenVenta.builder()
				.id(2L)
				.orderId(1L)
				.fechaCreacion(LocalDateTime.of(2025, 8, 23, 11, 5, 0))
				.fechaEntregaEstimada(LocalDateTime.of(2025, 8, 24,15 , 0, 0))
				.estado("Creada")
				.observaciones("")
				.cliente(cliente)
				.especificacion(especificacion1)
				.build();

		Rollo rollo1 = Rollo.builder()
				.id(1l)
				.proveedorId(1l)
				.codigoProveedor("xxcccaaa")
				.pesoKG(5000f)
				.anchoMM(300f)
				.espesorMM(30f)
				.tipoMaterial(TipoMaterial.LAMINADO_EN_FRIO)
				.estado(EstadoRollo.DISPONIBLE)
				.fechaIngreso(LocalDateTime.of(2025, 6, 10, 11, 0, 0))
				.rolloPadre(null)
				.build();

		Maquina maquina1 = Maquina.builder()
				.id(1l)
				.nombre("Cortadora 1")
				.activo(true)
				.tipo(MaquinaTipoEnum.CORTADORA)
				.espesorMaximoMilimetros(50f)
				.espesorMinimoMilimetros(1f)
				.anchoMinimoMilimetros(1f)
				.anchoMaximoMilimetros(600f)
				.velocidadTrabajoMetrosPorMinuto(10f)
				.build();

		Maquina maquina2 = Maquina.builder()
				.id(2l)
				.nombre("Laminadora 1")
				.activo(true)
				.tipo(MaquinaTipoEnum.LAMINADORA)
				.espesorMaximoMilimetros(50f)
				.espesorMinimoMilimetros(1f)
				.anchoMinimoMilimetros(1f)
				.anchoMaximoMilimetros(600f)
				.velocidadTrabajoMetrosPorMinuto(10f)
				.build();

		OrdenDeTrabajo ordenDeTrabajo = new OrdenDeTrabajo();

		OrdenDeTrabajoMaquina ordenDeTrabajoMaquina1 = OrdenDeTrabajoMaquina.builder()
				.id(1l)
				.ordenDeTrabajo(ordenDeTrabajo)
				.maquina(maquina1)
				.estado("Progamada")
				.observaciones("")
				.fechaInicio(LocalDateTime.of(2025, 8, 22, 9, 5, 0))
				.fechaFin(LocalDateTime.of(2025, 8, 22, 11, 0, 0))
				.build();

		List<OrdenVenta> ordenVentaList = List.of(ordenVenta1,ordenVenta2);
		List<Rollo> rolloList = List.of(rollo1);
		List<Maquina> maquinaList = List.of(maquina1, maquina2);
		List<OrdenDeTrabajoMaquina> ordenDeTrabajoMaquinaList = List.of(ordenDeTrabajoMaquina1);

		List<Long> ordenVentaIds = ordenVentaList.stream().map(v -> v.getId()).toList();
		List<Long> rolloIds = rolloList.stream().map(r -> r.getId()).toList();
		List<Long> maquinaIds = new ArrayList<>(maquinaList.stream().map(m -> m.getId()).toList());
		maquinaIds.add(0L);

		PlannerGA plannerGA = PlannerGA.builder()
				.MULTIPLIER_OF_WASTE(1)
				.MIN_LENGTH(20)
				.MIN_WIDTH(10)
				.maquinasIDs(maquinaIds)
				.maquinas(maquinaList)
				.ordenesDeVenta(ordenVentaList)
				.ordenesDeVentaIDs(ordenVentaIds)
				.ordenesDeTrabajoMaquina(ordenDeTrabajoMaquinaList)
				.rollosIDs(rolloIds)
				.rollos(rolloList)
				.grace_hours(2)
				.horaDeFinLaboral(18)
				.horaDeInicioLaboral(8)
				.build();

		Pair result = plannerGA.execute();

		List<OrdenDeTrabajo> ordenDeTrabajoList = (List<OrdenDeTrabajo>) result.first;

		for (OrdenDeTrabajo job : ordenDeTrabajoList) {
			for (OrdenDeTrabajoMaquina ordenDeTrabajoMaquina : job.getOrdenDeTrabajoMaquinas()){
				System.out.printf("Sale %d on Roll %d using Machine %d from %s to %s%n",
						job.getOrdenDeVenta().getId(),
						job.getRollo().getId(),
						ordenDeTrabajoMaquina.getMaquina().getId(),
						ordenDeTrabajoMaquina.getFechaInicio(),
						ordenDeTrabajoMaquina.getFechaFin());
			}

		}



	}
}
