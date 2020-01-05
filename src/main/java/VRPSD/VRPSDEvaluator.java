package VRPSD;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.*;
import java.util.stream.Collectors;

public class VRPSDEvaluator {

    private int max_eval = 100000;
    private int max_steps = 500;

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

    public int evaluate(VRPSD vrpsdProblem, IntegerSolution vrpsdSolution, Graph graph, ArrayList<Double> customersDemand) {
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
        ArrayList<Double> customersCurrentDemand = new ArrayList<>(customersDemand);
        ArrayList<Boolean> isDispatchListLooped = new ArrayList<>(Collections.nCopies(vertexNum, false));
        int step = -1;
        int result = 0;
        while (!allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if (currentVehiclesLoad.get(carId) > 0.0) {
                    int currentPositionId = currentVehiclesPositions.get(carId);
                    int nextPositionId = dispatchLists.get(currentPositionId).get(dispatchListsPointers.get(currentPositionId));
                    currentVehiclesPositions.set(carId, nextPositionId);

                    if (customersCurrentDemand.get(nextPositionId) < currentVehiclesLoad.get(carId)) {
                        currentVehiclesLoad.set(carId, currentVehiclesLoad.get(carId) - customersCurrentDemand.get(nextPositionId));
                        customersCurrentDemand.set(nextPositionId, 0.0);
                    } else {
                        customersCurrentDemand.set(nextPositionId, customersCurrentDemand.get(nextPositionId) - currentVehiclesLoad.get(carId));
                        currentVehiclesLoad.set(carId, 0.0);
                    }
                    result += addEdgeCost(currentPositionId, nextPositionId, graphStructure);

                    if (dispatchListsPointers.get(currentPositionId).equals(dispatchListLength - 1)) {
                        isDispatchListLooped.set(currentPositionId, true);
                    }

                    for (int i = 0; i < customersCurrentDemand.size(); i++) {
                        if (customersCurrentDemand.get(i) == 0.0) {
                            dispatchLists = removeFromDispatchLists(dispatchLists, i, graph, currentPositionId, dispatchListsPointers,
                                    customersCurrentDemand, isDispatchListLooped);
                        }
                    }
                    dispatchListsPointers.set(currentPositionId, (dispatchListsPointers.get(currentPositionId) + 1) % dispatchListLength);
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        saveSolution(vrpsdSolution, dispatchLists);
        //System.out.println(result);
        return (step < 500) ? result : max_eval;

    }

    private ArrayList<ArrayList<Integer>> removeFromDispatchLists(ArrayList<ArrayList<Integer>> dispatchLists, int customerNumber, Graph graph,
                                                                  Integer currentPositionId, ArrayList<Integer> dispatchListsPointers,
                                                                  ArrayList<Double> customersCurrentDemand,
                                                                  ArrayList<Boolean> isDispatchListLooped) {
        ArrayList<Integer> dispatchList = dispatchLists.get(currentPositionId);
        int old_size = dispatchList.size();
        for (int i = 0; i < dispatchList.size(); i++) {
            if (dispatchList.get(i).equals(customerNumber) && dispatchListsPointers.get(currentPositionId) < i && isDispatchListLooped.get(currentPositionId) == false) {
                dispatchList.set(i, new Integer(-1));
            }
        }
        while (dispatchList.contains(-1)) {
            dispatchList.remove(new Integer(-1));
        }
        int actual_size = dispatchList.size();


        Map<Integer, Double> demandToDistance = new HashMap<>();
        Map<Integer, Map<Integer, Double>> nearestNeighbours = graph.getNearestNeighbours();
        Map<Integer, Double> nearestNeighboursForCurrentVertex = nearestNeighbours.get(currentPositionId);
        for (Integer i : nearestNeighboursForCurrentVertex.keySet()) {
                demandToDistance.put(i, customersCurrentDemand.get(i)/nearestNeighboursForCurrentVertex.get(i) );
        }

        Map<Integer, Double> demandToDistanceSorted =
                demandToDistance.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        for (int i = 0; i < old_size - actual_size; i++) {
            Map.Entry<Integer, Double> entry = demandToDistanceSorted.entrySet().iterator().next();
            Integer key = entry.getKey();

            dispatchList.add(key);
        }
        dispatchLists.set(currentPositionId, dispatchList);
        return dispatchLists;

    }

    private boolean allCustomersSupplied(List<Double> customersCurrentDemand) {
        for (Double demand : customersCurrentDemand) {
            if (demand > 0.0) {
                return false;
            }
        }
        return true;
    }

    private int addEdgeCost(int currentPositionId, int nextPositionId, Map<Vertex, List<Edge>> graphStructure) {
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

    private void saveSolution(IntegerSolution solution, ArrayList<ArrayList<Integer>> dispatchLists) {
        int index = 0;
        for (ArrayList<Integer> dispatchList : dispatchLists) {
            for (Integer value : dispatchList) {
                solution.setVariableValue(index, value);
                index += 1;
            }
        }
    }
}
