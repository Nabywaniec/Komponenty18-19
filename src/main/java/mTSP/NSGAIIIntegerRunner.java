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
import java.util.List;

/**
 * Class for configuring and running the NSGA-II algorithm (integer encoding)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class NSGAIIIntegerRunner extends AbstractAlgorithmRunner {
    /**
     * @param args Command line arguments.
     * @throws org.uma.jmetal.util.JMetalException
     * @throws java.io.IOException
     * @throws SecurityException
     * @throws ClassNotFoundException
     * Invoking command:
    java org.uma.jmetal.runner.multiobjective.NSGAIIIntegerRunner problemName [referenceFront]
     */
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
            filename = "";
            dispatchListLength = 5;
            numOfDrivers = 5;
            referenceParetoFront = "";
        }

        Graph graph = new Graph();
        graph.setFullGraphStructure(filename);
        problem = new mTSP(graph, dispatchListLength, numOfDrivers);

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;
        crossover = new IntegerSBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        mutation = new IntegerPolynomialMutation(mutationProbability, mutationDistributionIndex) ;

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
    }
}
