package TSP;

import Model.Edge;
import Model.Graph;
import Model.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassicmTSPEvaluator {

    private List<Boolean> isVisited;
    private int max_eval;

    public ClassicmTSPEvaluator() {
        this.isVisited = new ArrayList<>();
        this.max_eval = 1000;
    }

    public int evaluate(Graph graph, int numOfDrivers, List<Integer> variables) {

        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();

        List<List<Integer>> variablesForDriver = new ArrayList<>();
        int x1 = 0;
        int x2 = 0;
        for (int i = variables.size() - numOfDrivers; i < variables.size(); i++) {
            x1 = x2;
            x2 += variables.get(i);
            List<Integer> newList = new ArrayList<>();
            for (int j = x1; j < x2; j++) {
                newList.add(variables.get(j));
            }
            variablesForDriver.add(newList);
        }

        int result = 0;
        for (int carId = 0; carId < numOfDrivers; carId++) {
            for (int i = 1; i < variablesForDriver.get(carId).size(); i++) {
                int currentPositionId = variablesForDriver.get(carId).get(i - 1);
                int nextPositionId = variablesForDriver.get(carId).get(i);

                Vertex currentVertex = null;
                Vertex nextVertex = null;
                for (Vertex vertex : graphStructure.keySet()) {
                    if (vertex.getId() == currentPositionId) {
                        currentVertex = vertex;
                    }
                    if (vertex.getId() == nextPositionId) {
                        nextVertex = vertex;
                    }
                }

                List<Edge> egdes = graphStructure.get(currentVertex);
                for (Edge edge : egdes) {
                    if (edge.getFirstVertexId() == currentVertex.getId() && edge.getSecondVertexId() == nextVertex.getId()) {
                        result += edge.getCost();
                    }
                }
            }
        }
        for (int carId = 0; carId < numOfDrivers; carId++) {
            int currentPositionId = variablesForDriver.get(carId).get(variablesForDriver.get(carId).size()-1);
            int nextPositionId = variablesForDriver.get(carId).get(0);
            Vertex currentVertex = null;
            Vertex nextVertex = null;
            for (Vertex vertex : graphStructure.keySet()) {
                if (vertex.getId() == currentPositionId) {
                    currentVertex = vertex;
                }
                if (vertex.getId() == nextPositionId) {
                    nextVertex = vertex;
                }
            }

            List<Edge> egdes = graphStructure.get(currentVertex);
            for (Edge edge : egdes) {
                if (edge.getFirstVertexId() == currentVertex.getId() && edge.getSecondVertexId() == nextVertex.getId()) {
                    result += edge.getCost();
                }
            }
        }

        return result;

    }



}
