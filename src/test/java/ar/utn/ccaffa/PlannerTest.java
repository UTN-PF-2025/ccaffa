package ar.utn.ccaffa;
import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import ar.utn.ccaffa.enums.TipoMaterial;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.planner.Pair;
import ar.utn.ccaffa.planner.PlannerGA;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class PlannerTest {


        private static final Random RANDOM = new Random(7);

        @Test
        void planner() {
            // Generate Especificacion
            List<Especificacion> especificaciones = new ArrayList<>();
            for (long i = 1; i <= 10; i++) {
                Especificacion e = Especificacion.builder()
                        .id(i)
                        .ancho(10F + RANDOM.nextFloat() * 100) // 10–110
                        .espesor(1F + RANDOM.nextFloat() * 1) // 1–11
                        .cantidad(RANDOM.nextInt(1000)) // 0–999
                        .tipoMaterial(TipoMaterial.LAMINADO_EN_FRIO)
                        .pesoMaximoPorRollo(10F)
                        .tipoDeEmbalaje("TEST")
                        .toleranciaAncho(1F)
                        .toleranciaEspesor(1F)
                        .diametroInterno(1F)
                        .diametroExterno(2F)
                        .build();
                especificaciones.add(e);
            }

            // Generate OrdenVenta
            List<OrdenVenta> ordenesVenta = new ArrayList<>();
            for (long i = 1; i <= 10; i++) {
                Especificacion randomEspecificacion = especificaciones.get(RANDOM.nextInt(especificaciones.size()));
                OrdenVenta ov = OrdenVenta.builder()
                        .id(i)
                        .orderId(i)
                        .fechaCreacion(LocalDateTime.now().minusDays(RANDOM.nextInt(1)))
                        .fechaEntregaEstimada(LocalDateTime.now().plusDays(RANDOM.nextInt(30)))
                        .estado("Creada")
                        .observaciones("Generada automáticamente")
                        .cliente(Cliente.builder()
                                .id(1L)
                                .activo(true)
                                .email("test@example.com")
                                .name("Cliente " + i)
                                .build())
                        .especificacion(randomEspecificacion)
                        .build();
                ordenesVenta.add(ov);
            }

            // Generate Rollo
            List<Rollo> rollos = new ArrayList<>();
            for (long i = 1; i <= 7; i++) {
                Rollo r = Rollo.builder()
                        .id(i)
                        .proveedorId(1L)
                        .codigoProveedor("PROV" + i)
                        .pesoKG(1000000000F + RANDOM.nextFloat() * 9000) // 1–10 t
                        .anchoMM(200F + RANDOM.nextFloat() * 200)  // 200–800 mm
                        .espesorMM(1F + RANDOM.nextFloat() * 300)  // 1–21 mm
                        .tipoMaterial(TipoMaterial.LAMINADO_EN_FRIO)
                        .estado(EstadoRollo.DISPONIBLE)
                        .fechaIngreso(LocalDateTime.now().minusDays(RANDOM.nextInt(90)))
                        .rolloPadre(null)
                        .build();
                rollos.add(r);
            }

            // Generate Maquina
            List<Maquina> maquinas = new ArrayList<>();
            for (long i = 1; i <= 5; i++) {
                Maquina m = Maquina.builder()
                        .id(i)
                        .nombre((i % 2 == 0 ? "Laminadora " : "Cortadora ") + i)
                        .activo(true)
                        .tipo(i % 2 == 0 ? MaquinaTipoEnum.LAMINADORA : MaquinaTipoEnum.CORTADORA)
                        .espesorMaximoMilimetros(50F)
                        .espesorMinimoMilimetros(1F)
                        .anchoMinimoMilimetros(100F)
                        .anchoMaximoMilimetros(1000F)
                        .velocidadTrabajoMetrosPorMinuto(5F + RANDOM.nextFloat() * 20)
                        .build();
                maquinas.add(m);
            }

            // Generate OrdenDeTrabajoMaquina
            OrdenDeTrabajo ordenDeTrabajo = new OrdenDeTrabajo();
            List<OrdenDeTrabajoMaquina> ordenesTrabajoMaquina = new ArrayList<>();
            for (long i = 1; i <= 7; i++) {
                LocalDateTime start = LocalDateTime.now().minusDays(RANDOM.nextInt(20)).minusHours(RANDOM.nextInt(24));
                OrdenDeTrabajoMaquina otm = OrdenDeTrabajoMaquina.builder()
                        .id(i)
                        .ordenDeTrabajo(ordenDeTrabajo)
                        .maquina(maquinas.get(RANDOM.nextInt(maquinas.size())))
                        .estado("Programada")
                        .observaciones("Generada automáticamente")
                        .fechaInicio(start)
                        .fechaFin(start.plusHours(1 + RANDOM.nextInt(5)))
                        .build();
                ordenesTrabajoMaquina.add(otm);
            }

            // Now you can use:
            List<Long> ordenVentaIds = ordenesVenta.stream().map(OrdenVenta::getId).toList();
            List<Long> rolloIds = rollos.stream().map(Rollo::getId).toList();
            List<Long> maquinaIds = new ArrayList<>(maquinas.stream().map(Maquina::getId).toList());
            maquinaIds.add(0L);

            PlannerGA plannerGA = PlannerGA.builder()
                    .MULTIPLIER_OF_WASTE(1)
                    .MIN_LENGTH(20)
                    .MIN_WIDTH(10)
                    .maquinasIDs(maquinaIds)
                    .maquinas(maquinas)
                    .ordenesDeVenta(ordenesVenta)
                    .ordenesDeVentaIDs(ordenVentaIds)
                    .ordenesDeTrabajoMaquina(ordenesTrabajoMaquina)
                    .rollosIDs(rolloIds)
                    .rollos(rollos)
                    .grace_hours(2)
                    .horaDeFinLaboral(18)
                    .horaDeInicioLaboral(8)
                    .build();

            Pair<List<OrdenDeTrabajo>, List<Rollo>> result = plannerGA.execute();

            List<OrdenDeTrabajo> ordenDeTrabajoList = result.first;

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
