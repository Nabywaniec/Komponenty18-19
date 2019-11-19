package TSP;

import Model.Graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        int fitness = 0;

        List<Integer> startPositions = new ArrayList<Integer>(Collections.nCopies(numOfDrivers, 0));
        ClassicmTSPEvaluator evaluator = new ClassicmTSPEvaluator();
        fitness = evaluator.evaluate(graph, numOfDrivers, integermTSPPermutationSolution.getVariables());

        try {
            fw.write(String.valueOf(System.nanoTime() - startTime) + " " + String.valueOf(fitness) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        integermTSPPermutationSolution.setObjective(0, fitness);
    }
}
