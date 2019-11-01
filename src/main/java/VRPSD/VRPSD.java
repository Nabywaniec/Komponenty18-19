package VRPSD;

import Model.Graph;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VRPSD extends AbstractIntegerProblem {
    private Graph graph;
    private int dispatchListLength;
    private int numOfVehicles;
    private double alpha;
    private double gamma;
    private double capacity;
    private FileWriter fw;

    private long startTime;
    private ArrayList<Double> customerDemands = new ArrayList<>();

    public int getDispatchListLength() {
        return dispatchListLength;
    }

    public int getNumOfVehicles() {
        return numOfVehicles;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getGamma() {
        return gamma;
    }

    public double getCapacity() {
        return capacity;
    }

    public ArrayList<Double> getCustomerDemands() {
        return customerDemands;
    }

    public VRPSD(Graph graph, int dispatchListLength, int numOfVehicles,
                 double alpha, double gamma, double capacity, FileWriter fw){
        this.graph = graph;
        this.dispatchListLength = dispatchListLength;
        this.numOfVehicles = numOfVehicles;
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

        for (int i=1; i<=graph.getVertexNum(); i++) {
            customerDemands.add(demandFunction(this.alpha, this.gamma, this.capacity));
        }

        this.fw = fw;
        startTime = System.nanoTime();
    }

    private Double demandFunction(double alpha, double gamma, double capacity) {
        double delta = ThreadLocalRandom.current().nextDouble(0, 1);
        return Math.floor(alpha * capacity + delta * (gamma - alpha) * capacity);
    }

    @Override
    public void evaluate(IntegerSolution integerSolution) {
        int fitness = 0;

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
