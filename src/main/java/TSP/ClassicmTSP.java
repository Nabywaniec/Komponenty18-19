package TSP;

import Model.Graph;

import java.io.FileWriter;

public class ClassicmTSP extends AbstractmTSPPermutationProblem {

    private Graph graph;
    private int numOfDrivers;
    private long startTime;
    private FileWriter fw;

    public ClassicmTSP(Graph graph, int numOfDrivers, FileWriter fw){
        this.graph = graph;
        this.numOfDrivers = numOfDrivers;

        this.setNumberOfVariables(this.graph.getVertexNum() + this.numOfDrivers);
        this.setNumberOfObjectives(1);
        this.setName("mTSP");

        this.fw = fw;
        this.startTime = System.nanoTime();
    }
    @Override
    public int getVertices() {
        return this.graph.getVertexNum();
    }

    @Override
    public int getCars() {
        return this.numOfDrivers;
    }

    @Override
    public void evaluate(mTSPPermutationSolution<Integer> integermTSPPermutationSolution) {

    }
}
