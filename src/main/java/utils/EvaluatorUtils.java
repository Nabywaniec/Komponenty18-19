package utils;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import VRPSD.VRPSD;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.*;

public class EvaluatorUtils {

    public static ArrayList<ArrayList<Integer>> extractDispatchListsFromSolution(List<Integer> dispatchListRaw,
                                                                          int dispatchListVertexLength,
                                                                          int vertexNum) {
        ArrayList<ArrayList<Integer>> dispatchLists = new ArrayList<>();
        for (int vertexId = 0; vertexId < vertexNum; vertexId++) {
            ArrayList<Integer> dispatchList = new ArrayList<>();
            for (int dispatchListSlotId = 0; dispatchListSlotId < dispatchListVertexLength; dispatchListSlotId++) {
                dispatchList.add(dispatchListRaw.get(vertexId * dispatchListVertexLength + dispatchListSlotId));
            }
            dispatchLists.add(vertexId, dispatchList);
        }
        return dispatchLists;
    }

    public static boolean allCustomersSupplied(List<Double> customersCurrentDemand) {
        for (Double demand : customersCurrentDemand) {
            if (demand > 0.0) {
                return false;
            }
        }
        return true;
    }

    public static int addEdgeCost(int currentPositionId, int nextPositionId, Map<Vertex, List<Edge>> graphStructure) {
        Vertex currentVertex = null;
        Vertex nextVertex = null;
        int result = 0;
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
        return result;
    }

    public static void saveSolution(IntegerSolution solution,ArrayList<ArrayList<Integer>> dispatchLists){
        int index = 0;
        for(ArrayList<Integer> dispatchList : dispatchLists){
            for(Integer value : dispatchList){
                solution.setVariableValue(index, value);
                index +=1;
            }
        }
    }
}
