package utils;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.*;

public class EvaluatorUtils {

    public ArrayList<ArrayList<Integer>> extractDispatchListsFromSolution(List<Integer> dispatchListRaw,
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

    public ArrayList<ArrayList<Integer>> extractDispatchListsFromSolutionWithVariableDepot(List<Integer> dispatchListRaw,
                                                                                            int dispatchListVertexLength,
                                                                                            int vertexNum,
                                                                                            int depotDispatchListLength) {
        ArrayList<ArrayList<Integer>> dispatchLists = new ArrayList<>();

        ArrayList<Integer> dispatchList = new ArrayList<>();
        for (int dispatchListSlotId = 0; dispatchListSlotId < depotDispatchListLength; dispatchListSlotId++) {
            dispatchList.add(dispatchListRaw.get(dispatchListSlotId));
        }
        dispatchLists.add(0, dispatchList);

        for (int vertexId = 1; vertexId < vertexNum; vertexId++) {
            dispatchList = new ArrayList<>();
            for (int dispatchListSlotId = 0; dispatchListSlotId < dispatchListVertexLength; dispatchListSlotId++) {
                dispatchList.add(dispatchListRaw.get(depotDispatchListLength + (vertexId-1) * dispatchListVertexLength + dispatchListSlotId));
            }
            dispatchLists.add(vertexId, dispatchList);
        }
        return dispatchLists;
    }

    public boolean allCustomersSupplied(List<Double> customersCurrentDemand) {
        for (Double demand : customersCurrentDemand) {
            if (demand > 0.0) {
                return false;
            }
        }
        return true;
    }

    public int addEdgeCost(int currentPositionId, int nextPositionId, Map<Vertex, List<Edge>> graphStructure) {
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

    public void saveSolution(IntegerSolution solution,ArrayList<ArrayList<Integer>> dispatchLists){
        int index = 0;
        for(ArrayList<Integer> dispatchList : dispatchLists){
            for(Integer value : dispatchList){
                solution.setVariableValue(index, value);
                index +=1;
            }
        }
    }

    public int findClosestDemandingCustomer(int currentPositionId, Map<Vertex, List<Edge>> graphStructure, ArrayList<Double> customersCurrentDemand) {
        int customerId = 0;
        ArrayList<Integer> customersFitness = new ArrayList<>();
        for(Double customerDemand : customersCurrentDemand){
            if(customerDemand == 0 || customerId == currentPositionId || customerId == 0){
                customersFitness.add(Integer.MAX_VALUE);
            } else {
                customersFitness.add(addEdgeCost(currentPositionId, customerId, graphStructure));
            }
            customerId++;
        }
        return customersFitness.indexOf(Collections.min(customersFitness));
    }

    public int findBestDemandPerDistanceCustomer(int currentPositionId, Map<Vertex, List<Edge>> graphStructure, ArrayList<Double> customersCurrentDemand) {
        int customerId = 0;
        ArrayList<Double> customersFitness = new ArrayList<>();
        for(Double customerDemand : customersCurrentDemand){
            if(customerDemand == 0 || customerId == currentPositionId || customerId == 0){
                customersFitness.add(0.0);
            } else {
                customersFitness.add(customerDemand / addEdgeCost(currentPositionId, customerId, graphStructure));
            }
            customerId++;
        }
        return customersFitness.indexOf(Collections.max(customersFitness));
    }

    public int findClosestDemandingNeighbour(int currentPositionId, Graph graph, ArrayList<Double> customersCurrentDemand) {
        Map<Integer, Double> neighbourMap = graph.getNearestNeighbours().get(currentPositionId);
        int bestNeighbourIndex = 0;
        double lowestDistance = Double.MAX_VALUE;
        for(Map.Entry<Integer, Double> neighbour : neighbourMap.entrySet()) {
            if(neighbour.getValue() < lowestDistance && customersCurrentDemand.get(neighbour.getKey()) != 0) {
                lowestDistance = neighbour.getValue();
                bestNeighbourIndex = neighbour.getKey();
            }
        }
        //returning 0 means every neighbour has no demand
        return bestNeighbourIndex;
    }

    public int findBestDemandPerDistanceNeighbour(int currentPositionId, Graph graph, ArrayList<Double> customersCurrentDemand) {
        Map<Integer, Double> neighbourMap = graph.getNearestNeighbours().get(currentPositionId);
        int bestNeighbourIndex = 0;
        double bestFitness = 0;
        for(Map.Entry<Integer, Double> neighbour : neighbourMap.entrySet()) {
            if((customersCurrentDemand.get(bestNeighbourIndex) / neighbour.getValue()) > bestFitness
                    && customersCurrentDemand.get(neighbour.getKey()) != 0) {
                bestNeighbourIndex = neighbour.getKey();
                bestFitness = customersCurrentDemand.get(bestNeighbourIndex) / neighbour.getValue();
            }
        }
        //returning 0 means every neighbour has no demand
        return bestNeighbourIndex;
    }

    public Integer findClosestDemandingCustomerWithinTime(int currentPositionId, Map<Vertex, List<Edge>> graphStructure,
                                                          ArrayList<Double> customersCurrentDemand, int step,
                                                          List<Double> readyTimes, List<Double> dueTimes) {
        int customerId = 0;
        ArrayList<Double> customersFitness = new ArrayList<>();
        for(Double customerDemand : customersCurrentDemand){
            if(customerDemand == 0 || customerId == currentPositionId || customerId == 0
                    || step + addEdgeCost(currentPositionId, customerId, graphStructure) > dueTimes.get(customerId)
                    || step + addEdgeCost(currentPositionId, customerId, graphStructure) < readyTimes.get(customerId)){
                customersFitness.add(Double.MAX_VALUE);
            } else {
                customersFitness.add(readyTimes.get(customerId));
            }
            customerId++;
        }
        return customersFitness.indexOf(Collections.min(customersFitness));
    }
}
