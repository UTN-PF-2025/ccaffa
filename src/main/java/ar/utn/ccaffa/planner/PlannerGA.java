package ar.utn.ccaffa.planner;

import ar.utn.ccaffa.enums.EstadoRollo;
import ar.utn.ccaffa.enums.MaquinaTipoEnum;
import ar.utn.ccaffa.model.entity.*;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.*;
import io.jenetics.util.ISeq;

import java.time.Duration;
import java.util.random.RandomGenerator;
import io.jenetics.util.RandomRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerGA {
    // --- PARAMETERS ---
    static final double INVALIDATE_SCORE = Double.POSITIVE_INFINITY;
    static final int GENERATIONS = 10000;
    static final double MUTATION_RATE = 0.2;
    static final double CROSSOVER_RATE = 0.5;
    static final int PATIENCE = 50;               // Steady generations limit
    static final int GENES_PER_OV = 4; // sale, roll, m1, m2
    
    private List<Long> ordenesDeVentaIDs;
    private List<Long> maquinasIDs;
    private List<Long> rollosIDs;

    private List<OrdenVenta> ordenesDeVenta;
    private List<OrdenDeTrabajoMaquina> ordenesDeTrabajoMaquina;
    private List<Rollo> rollos;
    private List<Maquina> maquinas;

    @Builder.Default
    private LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Builder.Default
    private int  COUNT = 0;
    
    private int horaDeInicioLaboral;
    private int horaDeFinLaboral;
    private int grace_hours;

    @Builder.Default
    private int POPULATION_SIZE_PER_SALE = 60;
    @Builder.Default
    private int MIN_WIDTH = 100;
    @Builder.Default
    private int MIN_LENGTH = 300;
    @Builder.Default
    private int MULTIPLIER_OF_WASTE = 1;
    @Builder.Default
    private int TOP_DAYS_IN_ADVANCE = 3;

    public int num_sales(){return ordenesDeVentaIDs.size();}
    public Pair<List<OrdenDeTrabajo>, List<Rollo>> execute() {
        Engine<IntegerGene, Double> engine = Engine
                .builder(this::evaluate, genotypeFactory())
                .offspringFraction(0.6) // we'll control rates via alterers
                .selector(new LinearRankSelector<>(0.9))
                .offspringSelector(new TournamentSelector<>(3))
                .alterers(
                        new PlannerGA.BlockCrossover(CROSSOVER_RATE),
                        new PlannerGA.BlockMutation(MUTATION_RATE)
                )
                .populationSize(POPULATION_SIZE_PER_SALE*this.ordenesDeVenta.size())
                .maximizing()
                .build();



        EvolutionResult<IntegerGene, Double> finalResult = engine.stream()
                .limit(Limits.bySteadyFitness(PATIENCE*this.ordenesDeVenta.size()))
                .limit(GENERATIONS)
                .collect(EvolutionResult.toBestEvolutionResult());


        System.out.println("\nBest solution: " + toChromosomeList(finalResult.bestPhenotype().genotype()));
        System.out.println("Best fitness: " + finalResult.bestFitness());

        List<int[]> blocks = toBlocks(finalResult.bestPhenotype().genotype());

        return generateJobOrders(blocks);

    }


    // --- Jenetics genotype factory mirroring Python's create_individual() ---
    private Factory<Genotype<IntegerGene>> genotypeFactory() {
        return () -> {
            RandomGenerator rnd = RandomRegistry.random();
            List<Long> shuffledOVs = new ArrayList<>(ordenesDeVentaIDs);
            Collections.shuffle(shuffledOVs, Random.from(rnd));

            int min = 0;
            int max = 9999;

            List<IntegerGene> genes = new ArrayList<>(ordenesDeVentaIDs.size() * GENES_PER_OV);
            for (long ovId : shuffledOVs) {
                // sale gene (fixed to a specific id)
                genes.add(IntegerGene.of((int) ovId, min, max));
                // roll gene
                List<Long> compatibleRolls = getCompatibleRolls((int) ovId);
                long rolloID = pickRandom(rnd, compatibleRolls);
                genes.add(IntegerGene.of((int) rolloID, min, max));
                // m1 gene
                long m1 = pickRandom(rnd, maquinasIDs);
                genes.add(IntegerGene.of((int) m1, min, max));
                // m2 gene
                List<Long> m2Candidates = compatibleMachine2(m1);
                long m2 = pickRandom( rnd, m2Candidates);
                genes.add(IntegerGene.of((int) m2, min, max));
            }
            return Genotype.of(IntegerChromosome.of(ISeq.of(genes)));
        };
    }

    static long pickRandom(RandomGenerator rnd, List<Long> list) {
        return list.get(rnd.nextInt(list.size()));
    }

    static LocalDateTime maxDate(LocalDateTime a, LocalDateTime b, LocalDateTime c) { return Stream.of(a,b,c).max(LocalDateTime::compareTo).orElse(a); }

    static boolean equalsD(Float a, Float b) { return Math.abs(a-b) < 1e-9; }

    private List<Long> compatibleMachine2(long m1) {
        if (m1 == 0) return List.of(0L);

        Maquina maquina1 = getMaquinaByID(m1);

        if (maquina1.getTipo() == MaquinaTipoEnum.CORTADORA)
            return  getMaquinasIDsByType(MaquinaTipoEnum.LAMINADORA);

        if (maquina1.getTipo() == MaquinaTipoEnum.LAMINADORA)
            return  List.of(0L);

        return  List.of(0L);
    }

    public List<Long> getCompatibleRolls(int saleId) {
        OrdenVenta s = getOrdenVentaById(saleId);
        List<Long> comp = rollos.stream()
                .filter(r -> r.getTipoMaterial().equals(s.getEspecificacion().getTipoMaterial())
                        && r.getEspesorMM() >= s.getEspecificacion().getEspesor()
                        && r.getAnchoMM() >= s.getEspecificacion().getAncho()
                        && r.getLargo() >= s.getEspecificacion().neededLengthOfRoll(r))
                .map(Rollo::getId).collect(Collectors.toList());
        return comp.isEmpty() ? rollosIDs : comp;
    }

    private Maquina getMaquinaByID(Long id){
        return (maquinas.stream().filter(maquina -> Objects.equals(maquina.getId(), id))).toList().get(0);
    }
    private List<Long> getMaquinasIDsByType(MaquinaTipoEnum type){
        return (maquinas.stream().filter(maquina -> Objects.equals(maquina.getTipo(), type)))
                .map(Maquina::getId)
                .toList();
    }

    private OrdenVenta getOrdenVentaById(long id) { return ordenesDeVenta.stream().filter(s -> s.getId() == id).findFirst().orElse(null); }
    private Rollo getRolloById(long id) { return rollos.stream().filter(s -> s.getId() == id).findFirst().orElse(null); }

    static List<Integer> toChromosomeList(Genotype<IntegerGene> gt) {
        return gt.chromosome().stream().map(IntegerGene::intValue).collect(Collectors.toList());
    }
    
    static List<int[]> toBlocks(Genotype<IntegerGene> gt) {
        List<Integer> chrom = toChromosomeList(gt);
        List<int[]> blocks = new ArrayList<>();
        for (int i = 0; i < chrom.size(); i += GENES_PER_OV) {
            int[] b = new int[]{chrom.get(i), chrom.get(i+1), chrom.get(i+2), chrom.get(i+3)};
            blocks.add(b);
        }
        return blocks;
    }

    private boolean checkRepeatedOrdenesVentasIDs(List<int[]> blocks) {
        Set<Integer> set = new HashSet<>();
        for (int[] b : blocks) if (!set.add(b[0])) return true; return false;
    }

    private boolean checkRollTypeMismatch(List<int[]> blocks) {
        for (int[] b : blocks) {
            OrdenVenta s = getOrdenVentaById(b[0]);
            Rollo r = getRolloById(b[1]);
            if (s == null || r == null) return true;
            if (!Objects.equals(s.getEspecificacion().getTipoMaterial(), r.getTipoMaterial())) return true;
        }
        return false;
    }

    private boolean checkRollThicknessMismatch(List<int[]> blocks) {
        for (int[] b : blocks) {
            OrdenVenta s = getOrdenVentaById(b[0]);
            Rollo r = getRolloById(b[1]);
            if (s == null || r == null) return true;
            if (s.getEspecificacion().getEspesor() > r.getEspesorMM()) {
                System.out.printf("ESPE: %.3f | R: %.3f%n", s.getEspecificacion().getEspesor(), r.getEspesorMM() );
                return true;
            }
        }
        return false;
    }

    private boolean checkInvalidMachineCombination(List<int[]> blocks) {
        for (int[] b : blocks) { long m1 = b[2], m2 = b[3]; if (m1 == 0 && m2 != 0) return true; }
        return false;
    }

    private boolean checkRepeatedTypeMachinesInOV(List<int[]> blocks) {
        for (int[] b : blocks) {
            long m1 = b[2], m2 = b[3];
            if (m1 != 0 && m2 != 0) {
                Maquina maquina1 = getMaquinaByID(m1);
                Maquina maquina2 = getMaquinaByID(m2);
                if (maquina1.getTipo() == maquina2.getTipo()) {
                    return true;}
            }
        }
        return false;
    }

    private double calcPenaltyForWastedRolls(List<Rollo> rollosHijos) {
        double p = 0;
        for (Rollo c : rollosHijos) if (c.getEstado() == EstadoRollo.DESPERDICIO) p += (double) c.getPesoKG() * this.MULTIPLIER_OF_WASTE; return p;
    }

    private double calcPenaltyForAvailableChildrenRolls(List<Rollo> rollosHijos){
        double p = 0;
        for (Rollo c : rollosHijos) if (c.getEstado() == EstadoRollo.DISPONIBLE) p += (double) c.getPesoKG()/100000000; return p;
    }
    private double calcScoreForDiffDates(List<OrdenDeTrabajo> jobs) {
        double score = 0;
        for (OrdenDeTrabajo j : jobs) score += Duration.between(j.getFechaEstimadaDeInicio(), j.getFechaEstimadaDeFin()).toSeconds();
        score = Math.min(score, TOP_DAYS_IN_ADVANCE*24*60*60);
        return score;
    }

    static boolean checkRollCharacteristics(OrdenVenta s, Rollo rollo) {
        if (s.getEspecificacion().getAncho() > rollo.getAnchoMM()) return false;
        if (s.getEspecificacion().neededLengthOfRoll(rollo) > rollo.getLargo()) return false;
        return true;
    }


    public class BlockCrossover implements Alterer<IntegerGene, Double> {
        private final double probability;

        BlockCrossover(double p) {
            this.probability = p;
        }

        @Override
        public AltererResult<IntegerGene, Double> alter(
                Seq<Phenotype<IntegerGene, Double>> population,
                long generation
        ) {
            RandomGenerator rnd = RandomRegistry.random();
            // Create a mutable copy of the population
            MSeq<Phenotype<IntegerGene, Double>> pop = population.asISeq().copy();

            int alterations = 0;
            for (int i = 0; i + 1 < pop.length(); i += 2) {
                if (rnd.nextDouble() >= probability) continue;

                Phenotype<IntegerGene, Double> p1 = pop.get(i);
                Phenotype<IntegerGene, Double> p2 = pop.get(i + 1);

                List<Integer> a = toChromosomeList(p1.genotype());
                List<Integer> b = toChromosomeList(p2.genotype());

                int cut = 1 + rnd.nextInt(num_sales() - 1);
                int cp = cut * GENES_PER_OV;

                List<Integer> new1 = concat(a.subList(0, cp), b.subList(cp, b.size()));
                List<Integer> new2 = concat(b.subList(0, cp), a.subList(cp, a.size()));

                fixSales(new1, rnd);
                repair(new1,  rnd);
                fixSales(new2, rnd);
                repair(new2,  rnd);

                pop.set(i, Phenotype.of(Genotype.of(IntegerChromosome.of(ISeq.of(toGenes(new1)))), generation));
                pop.set(i + 1, Phenotype.of(Genotype.of(IntegerChromosome.of(ISeq.of(toGenes(new2)))), generation));
                alterations += 2;
            }

            return new AltererResult<>(pop.toISeq(), alterations);
        }

        private void fixSales(List<Integer> chrom, RandomGenerator rnd) {
            Set<Long> used = new HashSet<>();
            for (int block = 0; block < num_sales(); block++) {
                int idx = block * GENES_PER_OV;
                long sale = chrom.get(idx);
                if (used.contains(sale)) {
                    List<Long> available = ordenesDeVentaIDs.stream()
                            .filter(s -> !used.contains(s))
                            .toList();
                    chrom.set(idx, Math.toIntExact(available.get(rnd.nextInt(available.size()))));
                }
                used.add(Long.valueOf(chrom.get(idx)));
            }
        }
    }

    public void repair(List<Integer> ind, RandomGenerator rnd) {
        for (int block = 0; block < num_sales(); block++) {
            int start = block * GENES_PER_OV;
            int saleId = ind.get(start);
            List<Long> compatibleRolls = getCompatibleRolls(saleId);
            if (!compatibleRolls.contains(ind.get(start + 1))) ind.set(start + 1, (int) pickRandom( rnd, compatibleRolls));

            int m1 = ind.get(start + 2);

            List<Long> m2Candidates = compatibleMachine2(m1);

            if (!m2Candidates.contains(ind.get(start + 3))) {
                ind.set(start + 3, (int) pickRandom( rnd, m2Candidates));
            }
        }
    }


    public class BlockMutation implements Alterer<IntegerGene, Double> {
        private final double probability;

        BlockMutation(double p) {
            this.probability = p;
        }

        @Override
        public AltererResult<IntegerGene, Double> alter(
                Seq<Phenotype<IntegerGene, Double>> population,
                long generation
        ) {
            RandomGenerator rnd = RandomRegistry.random();
            MSeq<Phenotype<IntegerGene, Double>> pop = population.asISeq().copy();
            int alterations = 0;

            for (int i = 0; i < pop.length(); i++) {
                if (rnd.nextDouble() >= probability) continue;

                Phenotype<IntegerGene, Double> pt = pop.get(i);
                List<Integer> chrom = toChromosomeList(pt.genotype());

                for (int block = 0; block < num_sales(); block++) {
                    int idx = block * GENES_PER_OV;
                    int saleId = chrom.get(idx);

                    // Roll mutation
                    List<Long> compRolls = getCompatibleRolls(saleId);
                    chrom.set(idx + 1,  (int) pickRandom( rnd, compRolls));

                    // m1 mutation
                    int m1 = (int) pickRandom( rnd, maquinasIDs);
                    chrom.set(idx + 2, m1);

                    // m2 mutation
                    List<Long> m2c = compatibleMachine2(m1);
                    chrom.set(idx + 3, (int) pickRandom( rnd, m2c));
                }

                pop.set(i, Phenotype.of(
                        Genotype.of(IntegerChromosome.of(ISeq.of(toGenes(chrom)))),
                        generation
                ));
                alterations++;
            }

            return new AltererResult<>(pop.toISeq(), alterations);
        }
    }




    static <T> List<T> concat(List<T> a, List<T> b) { List<T> out = new ArrayList<>(a.size()+b.size()); out.addAll(a); out.addAll(b); return out; }

    static List<IntegerGene> toGenes(List<Integer> ints) {
        List<IntegerGene> genes = new ArrayList<>(ints.size());
        for (Integer v : ints) genes.add(IntegerGene.of(v, 0, 9999));
        return genes;
    }


    public Double evaluate(Genotype<IntegerGene> gt) {
        List<int[]> blocks = toBlocks(gt);
        double fitness = 0.0;
        boolean shouldGenerateJobOrders = true;

        if (checkRepeatedOrdenesVentasIDs(blocks)) { fitness -= INVALIDATE_SCORE; log.debug("Repeated sales detected"); shouldGenerateJobOrders = false; }
        if (checkRollTypeMismatch(blocks)) { fitness -= INVALIDATE_SCORE; log.debug("Roll type mismatch detected"); shouldGenerateJobOrders = false; }
        if (checkRollThicknessMismatch(blocks)) { fitness -= INVALIDATE_SCORE; log.debug("Roll thickness mismatch detected"); shouldGenerateJobOrders = false; }
        if (checkInvalidMachineCombination(blocks)) { fitness -= INVALIDATE_SCORE; log.debug("Invalid machine combination detected"); shouldGenerateJobOrders = false; }
        if (checkRepeatedTypeMachinesInOV(blocks)) { fitness -= INVALIDATE_SCORE; log.debug("Repeated type of machines in a sale detected"); shouldGenerateJobOrders = false; }

        if (!shouldGenerateJobOrders) return fitness;

        Pair<List<OrdenDeTrabajo>, List<Rollo>> res = generateJobOrders(blocks);
        List<OrdenDeTrabajo> jobOrders = res.first; List<Rollo> children = res.second;
        if (jobOrders.isEmpty()) { fitness -= INVALIDATE_SCORE; return fitness; }

        double penaltyForWaste = calcPenaltyForWastedRolls(children);
        fitness -= penaltyForWaste;

        double penaltyForChildren = calcPenaltyForAvailableChildrenRolls(children);
        fitness -= penaltyForChildren;

        double score = calcScoreForDiffDates(jobOrders);
        fitness += score;

        COUNT++;

        if (penaltyForChildren > 0 || penaltyForWaste > 0 || score > 0) {
            log.debug("{} - Fitness score: {}, Penalty Waste: {}, Penalty Child: {}, Score: {}",
                    COUNT,
                    String.format("%.3f", fitness),
                    String.format("%.3f", penaltyForWaste),
                    String.format("%.3f", penaltyForChildren),
                    String.format("%.3f", score));
        }
        return fitness;
    }


    public Pair<List<OrdenDeTrabajo>, List<Rollo>> generateJobOrders(List<int[]> blocks)  {
        List<OrdenDeTrabajo> jobs = new ArrayList<>();
        List<OrdenDeTrabajoMaquina> ordenesMaquina = new ArrayList<>(this.getOrdenesDeTrabajoMaquina());
        Set<Integer> processedRolls = new HashSet<>();
        List<Rollo> children = new ArrayList<>();
        Map<Integer, List<Rollo>> childrenMap =  new HashMap<>();


        for (int[] b : blocks) {
            int     saleId = b[0],
                    rollId = b[1],
                    m1 = b[2],
                    m2 = b[3];

            OrdenVenta sale = getOrdenVentaById(saleId);
            Rollo roll = (Rollo) getRolloById(rollId).clone();
            Rollo usingRoll = roll;

            // CREATE ORDEN DE TRABAJO
            OrdenDeTrabajo ordenDeTrabajo = new OrdenDeTrabajo();
            ordenDeTrabajo.setOrdenDeVenta(sale);
            ordenDeTrabajo.setRollo(usingRoll);
            ordenDeTrabajo.setOrdenDeTrabajoMaquinas(new ArrayList<>());

            // CHECK IF MAIN ROLL CAN BE USED FOR SALE
            if (!checkRollCharacteristics(sale, roll)) {
                log.debug("Main roll cannot be used in sale");
                return new Pair<>(List.of(), List.of());
            }

            // USE A CHILD IF NECESSARY
            if (processedRolls.contains(rollId)) { // should use a child because the roll was already processed
                List<Rollo> childrenCandidates = this.childrenCandidatesOfRoll(childrenMap, rollId, sale);
                if (childrenCandidates.isEmpty()) { // no available children, then return as failed
                    log.debug("No available children");
                    return new Pair<>(List.of(), List.of());
                }
                usingRoll = childrenCandidates.get(0); // grab the one with less area
                ordenDeTrabajo.setRollo(usingRoll);
            }

            // POSSIBLE START DATE OF JOB
            LocalDateTime possibleStart = maxDate(CURRENT_DATE, sale.getFechaCreacion(), usingRoll.getFechaIngreso());
            LocalDateTime possibleEnd = possibleStart;


            // No machines, then sale is and roll must match completely
            if (m1 == 0 && m2 == 0) {
                if (!ordenDeTrabajo.rolloIgualQueEspecificacion()){
                    log.debug("m1=0 | m2=0 | sale != rollo");
                    return new Pair<>(List.of(), List.of()); // THEN IT NEEDS A MACHINE, SO FAILED
                }
                ordenDeTrabajo.setFechaEstimadaDeInicio(possibleStart);
                ordenDeTrabajo.setFechaEstimadaDeFin(possibleStart.plusHours(grace_hours));
                ordenDeTrabajo.setEstado("En Proceso");
                jobs.add(ordenDeTrabajo);
                continue;
            }


            if (this.isThereAInvalidCombinationOfMachinesForRollAndSale(m1, m2, usingRoll, sale)) {
                log.debug("InvalidCombinationOfMachinesForRollAndSale");
                return new Pair<>(List.of(), List.of()); // BAD COMBINATION OF MACHINE FOR SALE AND ROLL, SO FAILED
            }

            if (m1 != 0) {
                Maquina machine1 = this.getMaquinaByID((long) m1);

                List<OrdenDeTrabajoMaquina> ordenesDeTrabajoConMaquina = ordenesMaquina.stream().filter(u -> u.getMaquina() == machine1).toList();

                long minutosDeProcesamiento = machine1.minutosParaProcesarEspecifiacion(sale.getEspecificacion(),usingRoll);
                possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);

                if (possibleEnd.getHour() >= this.horaDeFinLaboral){
                    possibleStart = possibleStart.plusDays(1).withHour(this.horaDeInicioLaboral).withMinute(0).withSecond(0);
                    possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                }

                // 'a,b' and 'x,y':
                // if (!((b < x) || (a > y)))
                for (OrdenDeTrabajoMaquina orden : ordenesDeTrabajoConMaquina){
                    if(!(possibleEnd.isBefore(orden.getFechaInicio())  || orden.getFechaFin().isBefore(possibleStart))) {
                        possibleStart = orden.getFechaFin();
                        possibleEnd= possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                        if (possibleEnd.getHour() >= this.horaDeFinLaboral){
                            possibleStart = possibleStart.plusDays(1).withHour(this.horaDeInicioLaboral).withMinute(0).withSecond(0);
                            possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                        }
                    }

                }


                possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                OrdenDeTrabajoMaquina ordenMaquina = new OrdenDeTrabajoMaquina();
                ordenMaquina.setMaquina(machine1);
                ordenMaquina.setEstado("Programada");
                ordenMaquina.setOrdenDeTrabajo(ordenDeTrabajo);
                ordenMaquina.setFechaInicio(possibleStart);
                ordenMaquina.setFechaFin(possibleEnd);

                ordenDeTrabajo.getOrdenDeTrabajoMaquinas().add(ordenMaquina);
                ordenesMaquina.add(ordenMaquina);

                possibleStart = ordenMaquina.getFechaFin();

                List<Rollo> childrenRolls = ordenDeTrabajo.procesarRollo();
                for (Rollo cr :childrenRolls){
                    if (cr.getAnchoMM() < this.MIN_WIDTH || cr.getLargo() < this.MIN_LENGTH){
                        cr.setEstado(EstadoRollo.DESPERDICIO);
                    }
                }

                children.addAll(childrenRolls);
                List<Rollo> existingRolls = childrenMap.putIfAbsent(rollId, childrenRolls);
                if (existingRolls != null){
                    existingRolls.addAll(childrenRolls);
                }
            }

            if (m2 != 0) {
                Maquina machine2 = this.getMaquinaByID((long) m2);

                List<OrdenDeTrabajoMaquina> ordenesDeTrabajoConMaquina = ordenesMaquina.stream().filter(u -> u.getMaquina() == machine2).toList();

                long minutosDeProcesamiento = machine2.minutosParaProcesarEspecifiacion(sale.getEspecificacion(),usingRoll);
                possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);

                if (possibleEnd.getHour() >= this.horaDeFinLaboral){
                    possibleStart = possibleStart.plusDays(1).withHour(this.horaDeInicioLaboral).withMinute(0).withSecond(0);
                    possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                }

                // 'a,b' and 'x,y':
                // if (!((b < x) || (a > y)))
                for (OrdenDeTrabajoMaquina orden : ordenesDeTrabajoConMaquina){
                    if(!(possibleEnd.isBefore(orden.getFechaInicio())  || orden.getFechaFin().isBefore(possibleStart))) {
                        possibleStart = orden.getFechaFin();
                        possibleEnd= possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                        if (possibleEnd.getHour() >= this.horaDeFinLaboral){
                            possibleStart = possibleStart.plusDays(1).withHour(this.horaDeInicioLaboral).withMinute(0).withSecond(0);
                            possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                        }
                    }

                }



                possibleEnd = possibleStart.plusMinutes(minutosDeProcesamiento).plusHours(grace_hours);
                OrdenDeTrabajoMaquina ordenMaquina = new OrdenDeTrabajoMaquina();
                ordenMaquina.setMaquina(machine2);
                ordenMaquina.setEstado("Programada");
                ordenMaquina.setOrdenDeTrabajo(ordenDeTrabajo);
                ordenMaquina.setFechaInicio(possibleStart);
                ordenMaquina.setFechaFin(possibleEnd);

                ordenDeTrabajo.getOrdenDeTrabajoMaquinas().add(ordenMaquina);
                ordenesMaquina.add(ordenMaquina);
            }

            // ALL OKAY, ADD ORDEN DE TRABAJO
            ordenDeTrabajo.setFechaEstimadaDeInicio(ordenDeTrabajo.fechaInicioPrimeraMaquina());
            ordenDeTrabajo.setFechaEstimadaDeFin(possibleEnd);
            ordenDeTrabajo.setEstado("Programada");
            jobs.add(ordenDeTrabajo);


            processedRolls.add(rollId);
        }


        return new Pair<>(jobs, children);
    }


    private List<Rollo> childrenCandidatesOfRoll(Map<Integer, List<Rollo>> childrenMap, int rollId, OrdenVenta sale){
        // returns the children of a roll in the map and orders them by volume
       return childrenMap.get(rollId).stream()
                .filter(c -> EstadoRollo.DISPONIBLE.equals(c.getEstado()) && checkRollCharacteristics(sale, c ))
                .sorted(Comparator.comparingDouble(c -> c.getAnchoMM() * c.getLargo()))
                .toList();

    }

    private boolean isThereAInvalidCombinationOfMachinesForRollAndSale(int m1, int m2, Rollo usingRoll, OrdenVenta sale){
        // No machine2, then roll one needs to be modified in one characteristic (thickness or width)
        if (m1 != 0 && m2 == 0) {
            Maquina machine1 = this.getMaquinaByID((long) m1);
            if (machine1.getTipo() == MaquinaTipoEnum.CORTADORA) {
                if (!equalsD(usingRoll.getEspesorMM(), sale.getEspecificacion().getEspesor())
                        || equalsD(usingRoll.getAnchoMM(), sale.getEspecificacion().getAncho()))
                    return true;
            }
            if (machine1.getTipo() == MaquinaTipoEnum.LAMINADORA) {
                if (!equalsD(usingRoll.getAnchoMM(),sale.getEspecificacion().getAncho())
                        || equalsD(usingRoll.getEspesorMM(), sale.getEspecificacion().getEspesor()))
                    return true;
            }
        }
        // If both machines, then rolls needs to be modified in all characteristics and machine1 cannot be a Laminadora
        if (m1 != 0 && m2 != 0) {
            Maquina machine1 = this.getMaquinaByID((long) m1);

            if (equalsD(usingRoll.getEspesorMM(), sale.getEspecificacion().getEspesor())
                    || equalsD(usingRoll.getAnchoMM(), sale.getEspecificacion().getAncho()))
                return true;

            if (machine1.getTipo() == MaquinaTipoEnum.LAMINADORA)
                return true;
        }

        if(m1 != 0){
            Maquina machine1 = this.getMaquinaByID((long) m1);

            if (sale.getEspecificacion().getAncho() > machine1.getAnchoMaximoMilimetros())
                    return true;
            if (sale.getEspecificacion().getEspesor() > machine1.getEspesorMaximoMilimetros())
                    return true;

            if(usingRoll.getAnchoMM() < machine1.getAnchoMinimoMilimetros())
                return true;

            if(usingRoll.getEspesorMM() < machine1.getEspesorMinimoMilimetros())
                return true;

        }

        if(m2 != 0){
            Maquina machine2 = this.getMaquinaByID((long) m2);

            if (sale.getEspecificacion().getAncho() > machine2.getAnchoMaximoMilimetros())
                return true;
            if (sale.getEspecificacion().getEspesor() > machine2.getEspesorMaximoMilimetros())
                return true;

            if(usingRoll.getAnchoMM() < machine2.getAnchoMinimoMilimetros())
                return true;

            if(usingRoll.getEspesorMM() < machine2.getEspesorMinimoMilimetros())
                return true;

        }

        return false;
    }

}
