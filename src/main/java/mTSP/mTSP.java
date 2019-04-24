package mTSP;

import Model.Graph;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.ArrayList;
import java.util.List;

public class mTSP extends AbstractIntegerProblem {
    private int dispatchListLength;
    private Graph graph;
    private int numOfDrivers;

    public mTSP(Graph graph, int dispatchListLength, int numOfDrivers){
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
            upperLimit.add(this.graph.getVertexNum());
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(IntegerSolution integerSolution) {

    }
}
