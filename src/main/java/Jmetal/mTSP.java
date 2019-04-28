package Jmetal;

import Model.Graph;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;

public class mTSP extends AbstractGenericProblem<DispatchListsSolution> implements DispatchListsProblem<DispatchListsSolution>{
    private int dispatchListLength;
    private Graph graph;

    public mTSP(Graph graph, int dispatchListLength){
        this.graph = graph;
        this.dispatchListLength = dispatchListLength;
        this.setNumberOfVariables(this.graph.getVertexNum()*this.dispatchListLength);
        this.setNumberOfObjectives(1);
        this.setName("mTSP");
    }

    @Override
    public int getDispatchListLength() {
        return this.dispatchListLength;
    }

    @Override
    public int getVertexNum() {
        return this.graph.getVertexNum();
    }

    @Override
    public void evaluate(DispatchListsSolution dispatchListsSolution) {

    }

    @Override
    public DispatchListsSolution createSolution() {
        return new DefaultDispatchListsSolution(this);
    }
}
