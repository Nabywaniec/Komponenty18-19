package VRPSD;

import Model.Graph;
import Operators.Evaluator;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VRPSD extends AbstractIntegerProblem {
    private int dispatchListLength;
    private Graph graph;
    private int numOfDrivers;
    private double alpha;
    private double gamma;
    private double capacity;
    private FileWriter fw;

    private long startTime;

    public VRPSD(Graph graph, int dispatchListLength, int numOfDrivers,
                 double alpha, double gamma, double capacity, FileWriter fw){
        this.graph = graph;
        this.dispatchListLength = dispatchListLength;
        this.numOfDrivers = numOfDrivers;
        this.alpha = alpha;
        this.gamma = gamma;
        this.capacity = capacity;

        this.setNumberOfVariables(this.graph.getVertexNum()*this.dispatchListLength);
        this.setNumberOfObjectives(1);
        this.setName("VRPSD");

        List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
        List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0);
            upperLimit.add(this.graph.getVertexNum()-1);
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);

        this.fw = fw;
        startTime = System.nanoTime();
    }

    @Override
    public void evaluate(IntegerSolution integerSolution) {
        int fitness = 0;
        List<Integer> dispatchList = new ArrayList<>(integerSolution.getVariables());
        graph.setDispatchList(dispatchList, dispatchListLength);

        VRPSDEvaluator evaluator = new VRPSDEvaluator();
        fitness = evaluator.evaluate(this, integerSolution, graph);

        try {
            fw.write((System.nanoTime() - startTime) + " " + fitness + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        integerSolution.setObjective(0, fitness);
    }
}
