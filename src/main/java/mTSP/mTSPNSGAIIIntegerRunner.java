package mTSP;

import Model.Graph;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class mTSPNSGAIIIntegerRunner extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws FileNotFoundException {
        Problem<IntegerSolution> problem;
        Algorithm<List<IntegerSolution>> algorithm;
        CrossoverOperator<IntegerSolution> crossover;
        MutationOperator<IntegerSolution> mutation;
        SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;


        String filename = "";
        int dispatchListLength = 0;
        int numOfDrivers = 0;
        String referenceParetoFront = "";
        if (args.length == 3) {
            filename = args[0];
            dispatchListLength = Integer.parseInt(args[1]);
            numOfDrivers = Integer.parseInt(args[2]);
        } else if (args.length == 4) {
            filename = args[0];
            dispatchListLength = Integer.parseInt(args[1]);
            numOfDrivers = Integer.parseInt(args[2]);
            referenceParetoFront = args[3] ;
        } else {
            filename = "src\\main\\resources\\input\\mtsp\\mtsp51.txt";
            dispatchListLength = 7;
            numOfDrivers = 7;
            referenceParetoFront = "";
        }
        for(int i=0; i< 5; i++){
            filename = "src\\main\\resources\\input\\mtsp\\";
            String filenameEnd = "mtsp51";

            Graph graph = new Graph();
            graph.setFullGraphStructure(filename+filenameEnd+".txt");
            FileWriter fw = null;
            try {
                fw = new FileWriter(filenameEnd+"_"+numOfDrivers+"_"+dispatchListLength+"_"+i+".txt");
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            problem = new mTSP(graph, dispatchListLength, numOfDrivers, fw);

            double crossoverProbability = 0.9 ;
            double crossoverDistributionIndex = 20.0 ;
            crossover = new Crossover(crossoverProbability, crossoverDistributionIndex) ;

            double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
            double mutationDistributionIndex = 20.0 ;
            mutation = new Mutator();

            selection = new BinaryTournamentSelection<IntegerSolution>() ;

            int populationSize = 100 ;
            algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation, populationSize)
                    .setSelectionOperator(selection)
                    .setMaxEvaluations(25000)
                    .build() ;

            AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                    .execute() ;

            List<IntegerSolution> population = algorithm.getResult() ;
            long computingTime = algorithmRunner.getComputingTime() ;

            JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

            printFinalSolutionSet(population);
            if (!referenceParetoFront.equals("")) {
                printQualityIndicators(population, referenceParetoFront) ;
            }
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
