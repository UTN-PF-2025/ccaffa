package ar.utn.ccaffa;

import ar.utn.ccaffa.enums.*;
import ar.utn.ccaffa.model.entity.*;
import ar.utn.ccaffa.planner.Plan;
import ar.utn.ccaffa.planner.PlannerGA;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class PlannerTest {


        private static final Random RANDOM = new Random(7);

        @Test
        void planner() {
            // Generate Especificacion
            List<Especificacion> especificaciones = new ArrayList<>();
            for (long i = 1; i <= 15; i++) {
                Especificacion e = Especificacion.builder()
                        .id(i)
                        .ancho(10F + RANDOM.nextFloat() * 100) // 10–110
                        .espesor(1F + RANDOM.nextFloat() * 1) // 1–11
                        .cantidad(RANDOM.nextFloat(1000)) // 0–999
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
            for (long i = 1; i <= 15; i++) {
                Especificacion randomEspecificacion = especificaciones.get(RANDOM.nextInt(especificaciones.size()));
                OrdenVenta ov = OrdenVenta.builder()
                        .id(i)
                        .fechaCreacion(LocalDateTime.now().minusDays(RANDOM.nextInt(1)))
                        .fechaEntregaEstimada(LocalDateTime.now().plusDays(RANDOM.nextInt(30)))
                        .estado(EstadoOrdenVentaEnum.A_PLANIFICAR)
                        .observaciones("Generada automáticamente")
                        .cliente(Cliente.builder()
                                .id(1L)
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
            for (long i = 1; i <= 10; i++) {
                LocalDateTime start = LocalDateTime.now().minusDays(RANDOM.nextInt(1)).minusHours(RANDOM.nextInt(24));
                OrdenDeTrabajoMaquina otm = OrdenDeTrabajoMaquina.builder()
                        .id(i)
                        .ordenDeTrabajo(ordenDeTrabajo)
                        .maquina(maquinas.get(RANDOM.nextInt(maquinas.size())))
                        .estado(EstadoOrdenTrabajoMaquinaEnum.PROGRAMADA)
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

            Plan<List<OrdenDeTrabajo>, List<Rollo>> result = plannerGA.execute();

            List<OrdenDeTrabajo> ordenDeTrabajoList = result.ordenesDeTrabajo;
            List<Rollo> rolloList = result.rollosHijos;

            for (OrdenDeTrabajo job : ordenDeTrabajoList) {
                for (OrdenDeTrabajoMaquina ordenDeTrabajoMaquina : job.getOrdenDeTrabajoMaquinas()){
                    int parentRollHash;
                    if (job.getRollo().getRolloPadre() == null){
                        parentRollHash = 0;
                    } else {
                        parentRollHash = job.getRollo().getRolloPadre().hashCode();
                    }
                    System.out.printf("Sale %d | Roll %d | Parent Roll %d  | Machine %d - %s | From: %s | %s%n",
                            job.getOrdenDeVenta().getId(),
                            job.getRollo().hashCode(),
                            parentRollHash,
                            ordenDeTrabajoMaquina.getMaquina().getId(),
                            ordenDeTrabajoMaquina.getMaquina().getTipo(),
                            ordenDeTrabajoMaquina.getFechaInicio(),
                            ordenDeTrabajoMaquina.getFechaFin());
                }

            }

            for (OrdenDeTrabajo job : ordenDeTrabajoList) {
                System.out.printf("Sale %d | Ended %s | Due date %s  | Days in advance %d %n",
                        job.getOrdenDeVenta().getId(),
                        job.getFechaEstimadaDeFin(),
                        job.getOrdenDeVenta().getFechaEntregaEstimada(),
                        ChronoUnit.DAYS.between(job.getFechaEstimadaDeFin(), job.getOrdenDeVenta().getFechaEntregaEstimada()));
            }

            int totalDesperdicio = 0;
            float pesoDesperdicio = 0;
            int totalDisponible = 0;
            float pesoDisponible = 0;
            int totalDividido = 0;
            float pesoDividido = 0;
            for (Rollo roll : rolloList){
                int parentRollHash;
                if (roll.getRolloPadre() == null){
                    parentRollHash = 0;
                } else {
                    parentRollHash = roll.getRolloPadre().hashCode();
                }
                System.out.printf("Child Roll %d | Parent Roll %d | State %s | Weight %f.2 %n",
                        roll.hashCode(),
                        parentRollHash,
                        roll.getEstado(),
                        roll.getPesoKG());
                if (roll.getTipoRollo() == TipoRollo.DESPERDICIO){
                    totalDesperdicio++;
                    pesoDesperdicio += roll.getPesoKG();
                }
                if (roll.getEstado() == EstadoRollo.PLANIFICADO){
                    totalDisponible++;
                    pesoDisponible += roll.getPesoKG();
                }
            }

            System.out.printf("DESPERDICIO - Total %d | Weight %f.2 %n",
                    totalDesperdicio, pesoDesperdicio);

            System.out.printf("DISPONIBLE - Total %d | Weight %f.2 %n",
                    totalDisponible, pesoDisponible);

            System.out.printf("DIVIDIDO - Total %d | Weight %f.2 %n",
                    totalDividido, pesoDividido);

        }


}
