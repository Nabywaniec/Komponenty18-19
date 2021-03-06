package mTSP;

import Model.Graph;
import Operators.Evaluator;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class mTSP extends AbstractIntegerProblem {
    private int dispatchListLength;
    private Graph graph;
    private int numOfDrivers;
    private long startTime;
    private FileWriter fw;

    public mTSP(Graph graph, int dispatchListLength, int numOfDrivers, FileWriter fw){
        this.graph = graph;
        this.dispatchListLength = dispatchListLength;
        this.numOfDrivers = numOfDrivers;

        this.setNumberOfVariables(this.graph.getVertexNum()*this.dispatchListLength);
        this.setNumberOfObjectives(1);
        this.setName("mTSP");

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
        List<Integer> startPositions = new ArrayList<Integer>(Collections.nCopies(numOfDrivers, 0));

        Evaluator evaluator = new Evaluator();
        fitness = evaluator.evaluate(graph,dispatchListLength, startPositions, numOfDrivers);

        //System.out.println((System.nanoTime() - startTime) + " " + fitness);
        try {
            fw.write(String.valueOf(System.nanoTime() - startTime) + " " + String.valueOf(fitness) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        integerSolution.setObjective(0, fitness);
    }
}
