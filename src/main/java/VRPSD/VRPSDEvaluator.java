package VRPSD;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.*;

public class VRPSDEvaluator {

    private int max_eval = 100000;

    public ArrayList<ArrayList<Integer>> extractDispatchListsFromSolution(List<Integer> dispatchListRaw,
                                                                                  int dispatchListVertexLength,
                                                                                  int vertexNum) {
        ArrayList<ArrayList<Integer>> dispatchLists = new ArrayList<>();
        for(int vertexId = 0; vertexId < vertexNum; vertexId++){
            ArrayList<Integer> dispatchList = new ArrayList<>();
            for(int dispatchListSlotId = 0; dispatchListSlotId < dispatchListVertexLength; dispatchListSlotId++){
                dispatchList.add(dispatchListRaw.get(vertexId*dispatchListVertexLength+dispatchListSlotId));
            }
            dispatchLists.add(vertexId, dispatchList);
        }
        return dispatchLists;
    }

    public int evaluate(VRPSD vrpsdProblem, IntegerSolution vrpsdSolution, Graph graph) {
        int numOfVehicles = vrpsdProblem.getNumOfVehicles();
        double vehicleCapacity = vrpsdProblem.getCapacity();
        int dispatchListLength = vrpsdProblem.getDispatchListLength();
        int vertexNum = graph.getVertexNum();

        ArrayList<Integer> currentVehiclesPositions = new ArrayList<>(Collections.nCopies(numOfVehicles, 0));
        ArrayList<Double> currentVehiclesLoad = new ArrayList<>(Collections.nCopies(numOfVehicles, vehicleCapacity));
        ArrayList<ArrayList<Integer>> dispatchLists =
                extractDispatchListsFromSolution(vrpsdSolution.getVariables(), dispatchListLength, vertexNum);
        ArrayList<Integer> dispatchListsPointers = new ArrayList<>(Collections.nCopies(vertexNum, 0));
        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();
        ArrayList<Double> customersCurrentDemand = vrpsdProblem.getCustomerDemands();


//        int step = -1;
//        int result = 0;
//        while (!isAllVisited() && step < 500) {
//            step += 1;
//            for (int carId = 0; carId < numOfVehicles; carId++) {
//                int currentPositionId = positions.get(carId);
//                int nextPositionId = redirections.get(currentPositionId).get(dispatchPointers.get(currentPositionId));
//                dispatchPointers.put(currentPositionId, (dispatchPointers.get(currentPositionId)+1) % dispatchListVertexLenght);
//                positions.set(carId, nextPositionId);
//
//                isVisited.set(nextPositionId, true);
//
//                Vertex currentVertex = null;
//                Vertex nextVertex = null;
//                for(Vertex vertex : graphStructure.keySet()){
//                    if(vertex.getId() == currentPositionId) {
//                        currentVertex = vertex;
//                    }
//                    if(vertex.getId() == nextPositionId) {
//                        nextVertex = vertex;
//                    }
//                }
//
//                List<Edge> egdes = graphStructure.get(currentVertex);
//                for (Edge edge : egdes) {
//                    if (edge.getFirstVertexId() == currentVertex.getId() && edge.getSecondVertexId() == nextVertex.getId()) {
//                        result += edge.getCost();
//                    }
//                }
//            }
//        }
//
//        return (step < 500) ? result : max_eval;

        return max_eval;
    }
}
