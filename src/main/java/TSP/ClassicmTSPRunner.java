package TSP;

import Model.Graph;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.singleobjective.TSP;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassicmTSPRunner {

    public static void main(String[] args) throws Exception {
        mTSPPermutationProblem<mTSPPermutationSolution<Integer>> problem;
        CrossoverOperator<mTSPPermutationSolution<Integer>> crossover;
        MutationOperator<mTSPPermutationSolution<Integer>> mutation;
        SelectionOperator<List<mTSPPermutationSolution<Integer>>, mTSPPermutationSolution<Integer>> selection;
        Algorithm<mTSPPermutationSolution<Integer>> algorithm;

        for(int i=0; i< 5; i++){
            String filename = "src\\main\\resources\\input\\mtsp\\";
            String filenameEnd = "mtsp51";

            int numOfDrivers =7 ;
            Graph graph = new Graph();
            graph.setFullGraphStructure(filename+filenameEnd+".txt");
            FileWriter fw = null;
            try {
                fw = new FileWriter(filenameEnd+"_"+numOfDrivers+"_"+i+".txt");
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            problem = new ClassicmTSP(graph, numOfDrivers, fw);

            crossover = new ClassicmTSPCrossover(0.9D);

            double mutationProbability = 1.0D / (double)problem.getNumberOfVariables();
            mutation = new ClassicmTSPMutator(mutationProbability, numOfDrivers);

            selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());

            algorithm = (new GeneticAlgorithmBuilder(problem, crossover, mutation))
                    .setPopulationSize(100)
                    .setMaxEvaluations(250000)
                    .setSelectionOperator(selection).build();

            AlgorithmRunner algorithmRunner = (new AlgorithmRunner.Executor(algorithm))
                    .execute();

            mTSPPermutationSolution<Integer> solution = (mTSPPermutationSolution)algorithm.getResult();
            List<mTSPPermutationSolution<Integer>> population = new ArrayList(1);
            population.add(solution);

            long computingTime = algorithmRunner.getComputingTime();
            (new SolutionListOutput(population)).setSeparator("\t").setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv")).setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv")).print();
            JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
            JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
            JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
