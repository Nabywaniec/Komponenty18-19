package VRPSD;

import Model.Graph;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VRPSD extends AbstractIntegerProblem {
    private Graph graph;
    private ArrayList<Double> customerDemands;
    private int dispatchListLength;
    private int depotDispatchListLength;
    private int numOfVehicles;
    private double capacity;
    private FileWriter fw;

    private long startTime;

    public int getDispatchListLength() {
        return dispatchListLength;
    }

    public int getDepotDispatchListLength() {
        return depotDispatchListLength;
    }

    public int getNumOfVehicles() {
        return numOfVehicles;
    }

    public double getCapacity() {
        return capacity;
    }

    public VRPSD(Graph graph, ArrayList<Double> customerDemands, int dispatchListLength, int depotDispatchListLength,
                 int numOfVehicles, double capacity, FileWriter fw){
        this.graph = graph;
        this.customerDemands = customerDemands;
        this.dispatchListLength = dispatchListLength;
        this.depotDispatchListLength = depotDispatchListLength;
        this.numOfVehicles = numOfVehicles;
        this.capacity = capacity;

        this.setNumberOfVariables(this.depotDispatchListLength + (this.graph.getVertexNum()-1)*this.dispatchListLength);
        this.setNumberOfObjectives(1);
        this.setName("VRPSD");

        List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
        List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(1);
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

        VRPSDLuckyStarEvaluator evaluator = new VRPSDLuckyStarEvaluator();
        fitness = evaluator.evaluate(this, integerSolution, graph, customerDemands);

        try {
            fw.write((System.nanoTime() - startTime) + " " + fitness + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        integerSolution.setObjective(0, fitness);
    }
}
