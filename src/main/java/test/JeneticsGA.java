package test;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.*;
import io.jenetics.util.Seq;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.AltererResult;
import java.util.random.RandomGenerator;
import io.jenetics.util.RandomRegistry;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java translation of ag7.py using Jenetics.
 *
 * Requirements:
 *  - Java 17+
 *  - Jenetics 7.x
 *
 * Maven:
 * <dependency>
 *   <groupId>io.jenetics</groupId>
 *   <artifactId>jenetics</artifactId>
 *   <version>7.1.3</version>
 * </dependency>
 *
 * Gradle:
 * implementation("io.jenetics:jenetics:7.1.3")
 */
public class JeneticsGA {

    // --- PARAMETERS ---
    static final int POPULATION_SIZE = 300;
    static final int GENERATIONS = 1000;
    static final double MUTATION_RATE = 0.7;
    static final double CROSSOVER_RATE = 0.7;

    static final double FITNESS_TOLERANCE = 1e-6; // Minimum fitness improvement considered significant
    static final int PATIENCE = 30;               // Steady generations limit

    static final int GENES_PER_SALE = 4; // sale, roll, m1, m2

    static final int grace_hours = 1; // Grace hours for machine usage
    static final LocalDateTime CURRENT_DATE = LocalDateTime.of(2025, 8, 22, 0, 5, 0);
    static final int processing_time_machine_hours = 2;

    static final int MIN_WIDTH = 100;
    static final int MIN_LENGTH = 300;

    // Example predefined IDs (replace with your real data)
    static final List<Integer> sales_ids = List.of(1, 2, 3, 4);
    static final List<Integer> roll_ids = List.of(10, 20, 30, 40, 50);
    static final List<Integer> cutters = List.of(0, 101, 102);
    static final List<Integer> lamintors = List.of(0, 201, 202);

    static final int NUM_SALES = sales_ids.size();

    // Data structures equivalent to Python classes
    static class MachineDateInUse {
        int machine;
        LocalDateTime start, end;
        MachineDateInUse(int machine, LocalDateTime start, LocalDateTime end) {
            this.machine = machine; this.start = start; this.end = end;
        }
    }

    static class Sale {
        int sale; LocalDateTime start, end; int roll_length, roll_width; String roll_type; double thickness;
        Sale(int sale, LocalDateTime start, LocalDateTime end, int roll_length, int roll_width, String roll_type, double thickness) {
            this.sale = sale; this.start = start; this.end = end; this.roll_length = roll_length; this.roll_width = roll_width; this.roll_type = roll_type; this.thickness = thickness;
        }
    }

    static class Roll {
        int roll; String type; int length, width; double thickness; LocalDateTime available_from;
        Roll(int roll, String type, int length, int width, double thickness) {
            this.roll = roll; this.type = type; this.length = length; this.width = width; this.thickness = thickness; this.available_from = CURRENT_DATE;
        }
    }

    static class JobOrderMachine {
        Sale sale; LocalDateTime start, end; Object usedRoll; int machine;
        JobOrderMachine(Sale sale, LocalDateTime start, LocalDateTime end, Object usedRoll, int machine) {
            this.sale = sale; this.start = start; this.end = end; this.usedRoll = usedRoll; this.machine = machine;
        }
    }

    static class ProcessedRoll { int roll; LocalDateTime end; ProcessedRoll(int roll, LocalDateTime end) { this.roll = roll; this.end = end; } }

    static class ChildRoll {
        int roll; int length, width; double thickness; String state; LocalDateTime available_from;
        ChildRoll(int roll, int length, int width, double thickness, String state) {
            this.roll = roll; this.length = length; this.width = width; this.thickness = thickness; this.state = state; }
    }

    // Seed machine reservations
    static final MachineDateInUse md1_1 = new MachineDateInUse(101, t(2025,8,22,10,30), t(2025,8,22,12,30));
    static final MachineDateInUse md1_2 = new MachineDateInUse(101, t(2025,8,22,12,30), t(2025,8,22,14,30));

    static final MachineDateInUse md2_1 = new MachineDateInUse(102, t(2025,8,22,16,30), t(2025,8,22,15,30));
    static final MachineDateInUse md2_2 = new MachineDateInUse(102, t(2025,8,23,16,30), t(2025,8,23,18,30));
    static final MachineDateInUse md2_3 = new MachineDateInUse(102, t(2025,8,23,7,0),  t(2025,8,23,1,0));

    static final MachineDateInUse md3_1 = new MachineDateInUse(201, t(2025,8,22,1,30),  t(2025,8,22,3,30));
    static final MachineDateInUse md3_2 = new MachineDateInUse(201, t(2025,8,22,13,30), t(2025,8,22,15,30));
    static final MachineDateInUse md3_3 = new MachineDateInUse(201, t(2025,8,22,10,30), t(2025,8,22,12,30));

    static final MachineDateInUse md4_1 = new MachineDateInUse(202, t(2025,8,22,17,30), t(2025,8,22,18,30));
    static final MachineDateInUse md4_2 = new MachineDateInUse(202, t(2025,8,23,19,30), t(2025,8,23,20,30));
    static final MachineDateInUse md4_3 = new MachineDateInUse(202, t(2025,8,23,8,0),   t(2025,8,23,10,0));

    static final List<MachineDateInUse> mds = List.of(md1_1, md1_2, md2_1, md2_2, md2_3, md3_1, md3_2, md3_3, md4_1, md4_2, md4_3);

    static final Sale sale_1 = new Sale(1, t(2025,8,22,8,0),  t(2025,8,22,18,0), 1000, 200, "silver", 0.5);
    static final Sale sale_2 = new Sale(2, t(2025,8,22,9,0),  t(2025,8,22,17,0), 1500, 250, "silver", 0.7);
    static final Sale sale_3 = new Sale(3, t(2025,8,22,10,0), t(2025,8,23,16,0), 20000, 300, "gold", 0.8);
    static final Sale sale_4 = new Sale(4, t(2025,8,23,8,0),  t(2025,8,23,18,0), 1200, 220, "steel", 0.6);

    static final List<Sale> sales = List.of(sale_1, sale_2, sale_3, sale_4);

    static final Roll roll1 = new Roll(10, "silver", 20000, 300, 0.5);
    static final Roll roll2 = new Roll(20, "gold",   25000, 300, 1.0);
    static final Roll roll3 = new Roll(30, "steel",  10000, 200, 0.9);
    static final Roll roll4 = new Roll(40, "silver",  5000, 450, 0.9);
    static final Roll roll5 = new Roll(50, "steel",   3000, 300, 2.0);

    static final List<Roll> rolls = List.of(roll1, roll2, roll3, roll4, roll5);

    // Metrics
    static final List<Double> maxFitnessPerGen = new ArrayList<>();
    static final List<Double> avgFitnessPerGen = new ArrayList<>();

    // Utility: time builder
    static LocalDateTime t(int y, int M, int d, int h, int m) { return LocalDateTime.of(y, M, d, h, m); }

    public static void main(String[] args) {
        Engine<IntegerGene, Double> engine = Engine
                .builder(JeneticsGA::evaluate, genotypeFactory())
                .offspringFraction(1.0) // we'll control rates via alterers
                .selector(new TournamentSelector<>(2))
                .alterers(
                        new BlockCrossover(CROSSOVER_RATE),
                        new BlockMutation(MUTATION_RATE)
                )
                .populationSize(POPULATION_SIZE)
                .maximizing()
                .build();

        double bestFitness = Double.NEGATIVE_INFINITY;
        int stagnant = 0;

        Phenotype<IntegerGene, Double> best = null;
        int generation = 0;

        for (EvolutionResult<IntegerGene, Double> er : engine.stream().limit(GENERATIONS).toList()) {
            generation++;
            double maxFit = er.bestFitness();
            double avgFit = er.population().stream().mapToDouble(p -> p.fitness()).average().orElse(0);
            maxFitnessPerGen.add(maxFit);
            avgFitnessPerGen.add(avgFit);

            if (best == null || maxFit > bestFitness + FITNESS_TOLERANCE || (maxFit == bestFitness && bestFitness == 0.0)) {
                bestFitness = maxFit; stagnant = 0; best = er.bestPhenotype();
            } else {
                System.out.println("No significant improvement in generation " + (generation - 1) + ". Best fitness: " + bestFitness);
                if (++stagnant >= PATIENCE) {
                    System.out.println("Early stopping: No significant fitness improvement for " + PATIENCE + " generations.");
                    break;
                }
            }
        }

        if (best == null) best = engine.stream().limit(1).collect(EvolutionResult.toBestPhenotype());

        System.out.println("\nBest solution: " + toChromosomeList(best.genotype()));
        System.out.println("Best fitness: " + best.fitness());

        List<int[]> blocks = toBlocks(best.genotype());
        Pair<List<JobOrderMachine>, List<ChildRoll>> result = generateJobOrders(blocks);
        List<JobOrderMachine> jobs = result.first; List<ChildRoll> children = result.second;

        System.out.println("\nGenerated Job Orders:");
        for (JobOrderMachine job : jobs) {
            System.out.printf("Sale %d on Roll %s using Machine %d from %s to %s%n",
                    job.sale.sale,
                    (job.usedRoll instanceof Roll r) ? (""+r.roll) : ("child-of-"+((ChildRoll)job.usedRoll).roll),
                    job.machine,
                    job.start,
                    job.end);
        }

        System.out.println("\nGenerated Child Rolls:");
        for (ChildRoll c : children) {
            System.out.printf("Child Roll from Roll %d: %dx%dx%.3f, State: %s%s%n",
                    c.roll, c.length, c.width, c.thickness, c.state,
                    c.available_from != null ? ", Available from: " + c.available_from : "");
        }
    }

    // --- Jenetics genotype factory mirroring Python's create_individual() ---
    static Factory<Genotype<IntegerGene>> genotypeFactory() {
        return () -> {
            RandomGenerator rnd = RandomRegistry.random();
            List<Integer> shuffledSales = new ArrayList<>(sales_ids);
            Collections.shuffle(shuffledSales, Random.from(rnd));

            int min = 0;
            int max = 9999;

            List<IntegerGene> genes = new ArrayList<>(NUM_SALES * GENES_PER_SALE);
            for (int sale : shuffledSales) {
                // sale gene (fixed to a specific id)
                genes.add(IntegerGene.of(sale, min, max));
                // roll gene
                int roll = roll_ids.get(rnd.nextInt(roll_ids.size()));
                genes.add(IntegerGene.of(roll, min, max));
                // m1 gene
                int m1 = pickRandom(rnd, concatLists(cutters, lamintors));
                genes.add(IntegerGene.of(m1, min, max));
                // m2 gene
                List<Integer> m2Candidates = compatibleMachine2(m1);
                int m2 = pickRandom( rnd, m2Candidates);
                genes.add(IntegerGene.of(m2, min, max));
            }
            return Genotype.of(IntegerChromosome.of(ISeq.of(genes)));
        };
    }

    static List<Integer> compatibleMachine2(int m1) {
        if (m1 == 0) return List.of(0);
        if (cutters.contains(m1)) return lamintors.stream().filter(m -> m != 0).collect(Collectors.toList());
        if (lamintors.contains(m1)) return List.of(0);
        return List.of(0);
    }

    static int pickRandom(RandomGenerator rnd, List<Integer> list) {
        return list.get(rnd.nextInt(list.size()));
    }

    // Fitness evaluation
    static Double evaluate(Genotype<IntegerGene> gt) {
        List<int[]> blocks = toBlocks(gt);
        double fitness = 0.0;
        boolean shouldGenerateJobOrders = true;

        if (checkRepeatedSales(blocks)) { fitness -= 1_000_000; System.out.println("Repeated sales detected"); shouldGenerateJobOrders = false; }
        if (checkRollTypeMismatch(blocks)) { fitness -= 1_000_000; System.out.println("Roll type mismatch detected"); shouldGenerateJobOrders = false; }
        if (checkRollThicknessMismatch(blocks)) { fitness -= 1_000_000; System.out.println("Roll thickness mismatch detected"); shouldGenerateJobOrders = false; }
        if (checkInvalidMachineCombination(blocks)) { fitness -= 1_000_000; System.out.println("Invalid machine combination detected"); shouldGenerateJobOrders = false; }
        if (checkRepeatedTypeMachinesInSale(blocks)) { fitness -= 1_000_000; System.out.println("Repeated type of machines in a sale detected"); shouldGenerateJobOrders = false; }

        if (!shouldGenerateJobOrders) return fitness;

        Pair<List<JobOrderMachine>, List<ChildRoll>> res = generateJobOrders(blocks);
        List<JobOrderMachine> jobOrders = res.first; List<ChildRoll> children = res.second;
        if (jobOrders.isEmpty()) { fitness -= 1_000_000; return fitness; }

        double penalty = calcPenaltyForWastedRolls(children);
        fitness -= penalty;
        double score = calcScoreForDiffDates(jobOrders);
        fitness += score;

        if (penalty > 0 || score > 0) {
            System.out.printf("Fitness score: %.3f, Penalty: %.3f, Score: %.3f%n", fitness, penalty, score);
        }
        return fitness;
    }

    // Convert genotype to list of int[4] blocks
    static List<int[]> toBlocks(Genotype<IntegerGene> gt) {
        List<Integer> chrom = toChromosomeList(gt);
        List<int[]> blocks = new ArrayList<>();
        for (int i = 0; i < chrom.size(); i += GENES_PER_SALE) {
            int[] b = new int[]{chrom.get(i), chrom.get(i+1), chrom.get(i+2), chrom.get(i+3)};
            blocks.add(b);
        }
        return blocks;
    }

    static List<Integer> toChromosomeList(Genotype<IntegerGene> gt) {
        return gt.chromosome().stream().map(IntegerGene::intValue).collect(Collectors.toList());
    }

    // --- Checks (ported from Python) ---
    static boolean checkRepeatedSales(List<int[]> blocks) {
        Set<Integer> set = new HashSet<>();
        for (int[] b : blocks) if (!set.add(b[0])) return true; return false;
    }

    static boolean checkRollTypeMismatch(List<int[]> blocks) {
        for (int[] b : blocks) {
            Sale s = saleById(b[0]); Roll r = rollById(b[1]);
            if (s == null || r == null) return true;
            if (!Objects.equals(s.roll_type, r.type)) return true;
        }
        return false;
    }

    static boolean checkRollThicknessMismatch(List<int[]> blocks) {
        for (int[] b : blocks) {
            Sale s = saleById(b[0]); Roll r = rollById(b[1]);
            if (s == null || r == null) return true;
            if (s.thickness > r.thickness) return true;
        }
        return false;
    }

    static boolean checkInvalidMachineCombination(List<int[]> blocks) {
        for (int[] b : blocks) { int m1 = b[2], m2 = b[3]; if (m1 == 0 && m2 != 0) return true; }
        return false;
    }

    static boolean checkRepeatedTypeMachinesInSale(List<int[]> blocks) {
        for (int[] b : blocks) {
            int m1 = b[2], m2 = b[3];
            if (m1 != 0 && m2 != 0) {
                if (cutters.contains(m1) && cutters.contains(m2)) return true;
                if (lamintors.contains(m1) && lamintors.contains(m2)) return true;
            }
        }
        return false;
    }

    static double calcPenaltyForWastedRolls(List<ChildRoll> children) {
        double p = 0;
        for (ChildRoll c : children) if ("wasted".equals(c.state)) p += (double)c.length * c.width * c.thickness; return p;
    }

    static double calcScoreForDiffDates(List<JobOrderMachine> jobs) {
        double score = 0;
        for (JobOrderMachine j : jobs) score += Duration.between(j.end, j.sale.end).toSeconds();
        return score;
    }

    static Pair<List<JobOrderMachine>, List<ChildRoll>> generateJobOrders(List<int[]> blocks) {
        List<JobOrderMachine> jobs = new ArrayList<>();
        List<MachineDateInUse> mdsGenerated = new ArrayList<>(mds);
        Set<Integer> processedRolls = new HashSet<>();
        List<ChildRoll> children = new ArrayList<>();

        for (int[] b : blocks) {
            int saleId = b[0], rollId = b[1], m1 = b[2], m2 = b[3];
            Sale sale = saleById(saleId); Roll roll = rollById(rollId);
            Object usingRoll = roll;

            if (processedRolls.contains(rollId)) {
                // try existing child rolls
                List<ChildRoll> candidates = children.stream()
                        .filter(c -> c.roll == rollId && "available".equals(c.state) && checkRollCharacteristics(sale, c.length, c.width, c.thickness))
                        .sorted(Comparator.comparingInt(c -> c.width * c.length))
                        .collect(Collectors.toList());
                if (!candidates.isEmpty()) {
                    ChildRoll child = candidates.get(0);
                    child.state = "used";
                    usingRoll = child;
                } else {
                    return new Pair<>(List.of(), List.of());
                }
            } else {
                if (!checkRollCharacteristics(sale, roll.length, roll.width, roll.thickness)) return new Pair<>(List.of(), List.of());
            }

            LocalDateTime possibleStart = maxDate(CURRENT_DATE, sale.start, getAvailableFrom(usingRoll));

            // Validate machine/roll/sale relations
            if (m1 == 0 && m2 == 0) {
                if (!(equalsD(thicknessOf(usingRoll), sale.thickness) && widthOf(usingRoll) == sale.roll_width)) return new Pair<>(List.of(), List.of());
            }
            if (m1 != 0 && m2 == 0) {
                if (cutters.contains(m1)) {
                    if (!equalsD(thicknessOf(usingRoll), sale.thickness) || widthOf(usingRoll) == sale.roll_width) return new Pair<>(List.of(), List.of());
                }
                if (lamintors.contains(m1)) {
                    if (widthOf(usingRoll) != sale.roll_width || equalsD(thicknessOf(usingRoll), sale.thickness)) return new Pair<>(List.of(), List.of());
                }
            }
            if (m1 != 0 && m2 != 0) {
                if (equalsD(thicknessOf(usingRoll), sale.thickness) || widthOf(usingRoll) == sale.roll_width) return new Pair<>(List.of(), List.of());
                if (lamintors.contains(m1)) return new Pair<>(List.of(), List.of());
            }

            if (m1 != 0) {
                List<MachineDateInUse> uses = mdsGenerated.stream().filter(u -> u.machine == m1).collect(Collectors.toList());
                for (MachineDateInUse u : uses) if (!u.end.isBefore(possibleStart) && !u.start.isAfter(possibleStart)) possibleStart = u.end.plusHours(grace_hours);
                JobOrderMachine job1 = new JobOrderMachine(sale, possibleStart, possibleStart.plusHours(processing_time_machine_hours), usingRoll, m1);
                jobs.add(job1);
                mdsGenerated.add(new MachineDateInUse(job1.machine, job1.start, job1.end));
                processedRolls.add(rollId);
                possibleStart = job1.end.plusHours(grace_hours);

                if (cutters.contains(m1)) generateChildRollsByWidth(sale, usingRoll, children, possibleStart);
                generateChildRollsByLength(sale, usingRoll, children, possibleStart);
            }

            if (m2 != 0) {
                List<MachineDateInUse> uses2 = mdsGenerated.stream().filter(u -> u.machine == m1).collect(Collectors.toList());
                for (MachineDateInUse u : uses2) if (!u.end.isBefore(possibleStart) && !u.start.isAfter(possibleStart)) possibleStart = u.end.plusHours(grace_hours);
                JobOrderMachine job2 = new JobOrderMachine(sale, possibleStart, possibleStart.plusHours(processing_time_machine_hours), usingRoll, m2);
                jobs.add(job2);
                mdsGenerated.add(new MachineDateInUse(job2.machine, job2.start, job2.end));
            }
        }

        return new Pair<>(jobs, children);
    }

    static LocalDateTime getAvailableFrom(Object usingRoll) {
        if (usingRoll instanceof Roll r) return r.available_from;
        if (usingRoll instanceof ChildRoll c) return c.available_from != null ? c.available_from : CURRENT_DATE;
        return CURRENT_DATE;
    }

    static boolean checkRollCharacteristics(Sale s, int length, int width, double thickness) {
        if (s.roll_width > width) return false; if (s.roll_length > length) return false; return true;
    }

    static void generateChildRollsByWidth(Sale s, Object usingRoll, List<ChildRoll> out, LocalDateTime availableFrom) {
        int width = widthOf(usingRoll); int length = lengthOf(usingRoll); double thick = thicknessOf(usingRoll); int saleWidth = s.roll_width;
        if (saleWidth < width) {
            ChildRoll c = new ChildRoll(baseRollId(usingRoll), s.roll_length, width - saleWidth, thick, "available");
            c.available_from = availableFrom; c.state = (c.width >= MIN_WIDTH) ? "available" : "wasted"; out.add(c);
        }
    }

    static void generateChildRollsByLength(Sale s, Object usingRoll, List<ChildRoll> out, LocalDateTime availableFrom) {
        int width = widthOf(usingRoll); int length = lengthOf(usingRoll); double thick = thicknessOf(usingRoll);
        if (s.roll_length < length) {
            int remaining = length - s.roll_length; ChildRoll c = new ChildRoll(baseRollId(usingRoll), remaining, width, thick, "available");
            c.available_from = availableFrom; c.state = (c.length >= MIN_LENGTH) ? "available" : "wasted"; out.add(c);
        }
    }

    static int baseRollId(Object r) { return (r instanceof Roll R) ? R.roll : ((ChildRoll)r).roll; }
    static int widthOf(Object r) { return (r instanceof Roll R) ? R.width : ((ChildRoll)r).width; }
    static int lengthOf(Object r) { return (r instanceof Roll R) ? R.length : ((ChildRoll)r).length; }
    static double thicknessOf(Object r) { return (r instanceof Roll R) ? R.thickness : ((ChildRoll)r).thickness; }

    static boolean equalsD(double a, double b) { return Math.abs(a-b) < 1e-9; }

    static Sale saleById(int id) { return sales.stream().filter(s -> s.sale == id).findFirst().orElse(null); }
    static Roll rollById(int id) { return rolls.stream().filter(r -> r.roll == id).findFirst().orElse(null); }

    static LocalDateTime maxDate(LocalDateTime a, LocalDateTime b, LocalDateTime c) { return Stream.of(a,b,c).max(LocalDateTime::compareTo).orElse(a); }

    // --- Custom Alterers ---
    static class BlockCrossover implements Alterer<IntegerGene, Double> {
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

                int cut = 1 + rnd.nextInt(NUM_SALES - 1);
                int cp = cut * GENES_PER_SALE;

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
            Set<Integer> used = new HashSet<>();
            for (int block = 0; block < NUM_SALES; block++) {
                int idx = block * GENES_PER_SALE;
                int sale = chrom.get(idx);
                if (used.contains(sale)) {
                    List<Integer> available = sales_ids.stream()
                            .filter(s -> !used.contains(s))
                            .toList();
                    chrom.set(idx, available.get(rnd.nextInt(available.size())));
                }
                used.add(chrom.get(idx));
            }
        }
    }
    static class BlockMutation implements Alterer<IntegerGene, Double> {
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

                for (int block = 0; block < NUM_SALES; block++) {
                    int idx = block * GENES_PER_SALE;
                    int saleId = chrom.get(idx);

                    // Roll mutation
                    List<Integer> compRolls = getCompatibleRolls(saleId);
                    chrom.set(idx + 1, compRolls.get(rnd.nextInt(compRolls.size())));

                    // m1 mutation
                    int m1 = pickRandom( rnd, concatLists(cutters, lamintors));
                    chrom.set(idx + 2, m1);

                    // m2 mutation
                    List<Integer> m2c = compatibleMachine2(m1);
                    chrom.set(idx + 3, m2c.get(rnd.nextInt(m2c.size())));
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


    // Helper: repair roll/machine compatibility after crossover
    static void repair(List<Integer> ind, RandomGenerator rnd) {
        for (int block = 0; block < NUM_SALES; block++) {
            int start = block * GENES_PER_SALE;
            int saleId = ind.get(start);
            List<Integer> compatibleRolls = getCompatibleRolls(saleId);
            if (!compatibleRolls.contains(ind.get(start + 1))) ind.set(start + 1, compatibleRolls.get(rnd.nextInt(compatibleRolls.size())));

            int m1 = ind.get(start + 2);
            List<Integer> compM2;
            if (m1 == 0) compM2 = List.of(0);
            else if (cutters.contains(m1)) compM2 = lamintors.stream().filter(x -> x != 0).collect(Collectors.toList());
            else if (lamintors.contains(m1)) compM2 = List.of(0);
            else compM2 = List.of(0);

            if (!compM2.contains(ind.get(start + 3))) ind.set(start + 3, compM2.get(rnd.nextInt(compM2.size())));
        }
    }

    static List<IntegerGene> toGenes(List<Integer> ints) {
        List<IntegerGene> genes = new ArrayList<>(ints.size());
        for (Integer v : ints) genes.add(IntegerGene.of(v, 0, 9999));
        return genes;
    }

    static List<Integer> getCompatibleRolls(int saleId) {
        Sale s = saleById(saleId);
        List<Integer> comp = rolls.stream()
                .filter(r -> r.type.equals(s.roll_type) && r.thickness >= s.thickness && r.width >= s.roll_width && r.length >= s.roll_length)
                .map(r -> r.roll).collect(Collectors.toList());
        return comp.isEmpty() ? roll_ids : comp;
    }

    // Small utilities
    static <T> List<T> concat(List<T> a, List<T> b) { List<T> out = new ArrayList<>(a.size()+b.size()); out.addAll(a); out.addAll(b); return out; }
    static List<Integer> concatLists(List<Integer> a, List<Integer> b) { return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList()); }

    // Simple Pair record
    static class Pair<A,B> { final A first; final B second; Pair(A a, B b){first=a;second=b;} }
}
