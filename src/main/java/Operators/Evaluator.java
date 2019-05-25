package Operators;

import Model.Edge;
import Model.Graph;
import Model.Vertex;

import java.util.*;

public class Evaluator {

    private List<Boolean> isVisited;
    private int max_eval;

    public Evaluator() {
        isVisited = new ArrayList<>();
        max_eval = 1000;
    }

    public int evaluate(Graph graph, int dispatchListVertexLenght, List<Integer> positions, int M) {
        Map<Integer, List<Integer>> redirections = graph.getDispatchList();
        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();
        Map<Integer, Integer> dispatchPointers = new HashMap<Integer, Integer>();
        for(int vertexId = 0; vertexId < graph.getVertexNum(); vertexId++){
            dispatchPointers.put(vertexId, 0);
        }

        setIsVisited(graph.getVertexNum());

        for (int carId = 0; carId < M; carId++) {
            isVisited.set(positions.get(carId), true);
        }

        int step = -1;
        int result = 0;
        while (!isAllVisited() && step < 500) {
            step += 1;
            for (int carId = 0; carId < M; carId++) {
                int currentPositionId = positions.get(carId);
                int nextPositionId = redirections.get(currentPositionId).get(dispatchPointers.get(currentPositionId));
                dispatchPointers.put(currentPositionId, (dispatchPointers.get(currentPositionId)+1) % dispatchListVertexLenght);
                positions.set(carId, nextPositionId);

                isVisited.set(nextPositionId, true);

                Vertex currentVertex = null;
                Vertex nextVertex = null;
                for(Vertex vertex : graphStructure.keySet()){
                    if(vertex.getId() == currentPositionId) {
                        currentVertex = vertex;
                    }
                    if(vertex.getId() == nextPositionId) {
                        nextVertex = vertex;
                    }
                }

                List<Edge> egdes = graphStructure.get(currentVertex);
                for (Edge edge : egdes) {
                    if (edge.getFirstVertexId() == currentVertex.getId() || edge.getSecondVertexId() == nextVertex.getId()) {
                        result += edge.getCost();
                    }
                }
            }
        }
        return (step < 500) ? result : max_eval;
    }


    public void setIsVisited(int isVisitedSize) {
        this.isVisited = new ArrayList<>();
        for (int i = 0; i < isVisitedSize; i++) {
            isVisited.add(i, false);
        }
    }

    private boolean isAllVisited() {
        for (Boolean visited : isVisited) {
            if (!visited) return false;
        }
        return true;
    }
}
