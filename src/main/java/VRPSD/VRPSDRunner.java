package VRPSD;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import Operators.Evaluator;
import mTSP.mTSP;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VRPSDRunner extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws FileNotFoundException {
        Problem<IntegerSolution> problem;
        Algorithm<List<IntegerSolution>> algorithm;
        CrossoverOperator<IntegerSolution> crossover;
        MutationOperator<IntegerSolution> mutation;
        SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;
        String onlyGraphFolderName  ="src\\main\\resources\\VRPSD\\only_graph\\";
        String fullDataFolderName  ="src\\main\\resources\\VRPSD\\full\\";


        String filename = "";
        int dispatchListLength = 0;
        int numOfDrivers = 0;
        double alpha = 0;
        double gamma = 0;
        double capacity = 0;
        String referenceParetoFront = "";
        if (args.length == 6) {
            filename = args[0];
            dispatchListLength = Integer.parseInt(args[1]);
            numOfDrivers = Integer.parseInt(args[2]);
            alpha = Double.parseDouble(args[3]);
            gamma = Double.parseDouble(args[4]);

        } else {
            filename = "eil23.sd";
            dispatchListLength = 3;
            numOfDrivers = 4;
            alpha = 0.1;
            gamma = 0.3;
        }

        Configuration conf = new Configuration(fullDataFolderName + filename);

        capacity = conf.getCapacity();

        numOfDrivers = conf.getMinNumOfTrucks();

        Graph graph = new Graph();
//        graph.setFullGraphStructure(onlyGraphFolderName+filename);
//        graph.setStructure("src/main/resources/VRPSD/VRPSDtest.txt");
        graph.setFullGraphStructureWithVertexList(conf.getVertexesList());

        FileWriter fw = null;
        try {
            fw = new FileWriter(filename+"_"+numOfDrivers+"_"+dispatchListLength+".txt");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //DemandFactory demandFactory = new DemandFactory();
        //ArrayList<Double> customerDemands = demandFactory.createCustomersDemands(graph.getVertexNum(), alpha, gamma, capacity);
        //ArrayList<Double> customerDemands = demandFactory.readCustomersDemandsFromFile(fullDataFolderName+filename);
        //ArrayList<Double> customerDemands = new ArrayList<Double>(){{add(0.0); add(3.0); add(3.0); add(3.0); add(3.0);}};
        ArrayList<Double> customerDemands = conf.getCustomerDemands();

        VRPSDSimpleEvaluator evaluatorSimple = new VRPSDSimpleEvaluator();
        System.out.println(evaluatorSimple.evaluateSimple(numOfDrivers, capacity, graph, customerDemands));

        problem = new VRPSD(graph, customerDemands, dispatchListLength, numOfDrivers, capacity, fw);

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;
        crossover = new VRPSDCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        mutation = new VRPSDMutator(mutationProbability, mutationDistributionIndex);

        selection = new BinaryTournamentSelection<IntegerSolution>() ;

        int populationSize = 100;
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
