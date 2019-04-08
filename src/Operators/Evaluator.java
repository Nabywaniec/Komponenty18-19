package Operators;

import Model.Edge;
import Model.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Evaluator {

    private List<Boolean> isVisited;

    public Evaluator() {
    }

    public int evaluate(Map<Integer, List<Integer>> redirections, Map<Vertex, List<Edge>> graphStructure, List<Integer> positions, int M) {

        for (int i = 0; i < M; i++) {
            isVisited.set(i, true);
        }
        int step = -1;
        int result = 0;
        while (!isAllVisited()) {
            step += 1;
            for (int i = 0; i < M; i++) {
                List<Integer> redirectionList = redirections.get(positions.get(i));
                int newPositon = redirectionList.get(result % redirectionList.size());
                isVisited.set(newPositon, true);
                positions.set(i, redirectionList.get(result % redirectionList.size()));
                List<Edge> egdes = graphStructure.get(positions.get(i));
                for (Edge edge : egdes) {
                    if (edge.getFirstVertexId() == newPositon || edge.getSecondVertexId() == newPositon) {
                        result += edge.getCost();
                    }
                }
            }
        }
        return result;
    }


    public void setIsVisited(int isVisitedSize) {
        this.isVisited = new ArrayList<>(isVisitedSize);
        for (int i = 0; i < isVisitedSize; i++) {
            isVisited.set(i, false);
        }
    }

    private boolean isAllVisited() {
        for (Boolean visited : isVisited) {
            if (!visited) return false;
        }
        return true;
    }
}
