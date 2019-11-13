package VRPSD;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.*;

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

        int step = -1;
        int result = 0;
        while (!allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if (currentVehiclesLoad.get(carId) > 0.0) {
                    int currentPositionId = currentVehiclesPositions.get(carId);
                    int nextPositionId = dispatchLists.get(currentPositionId).get(dispatchListsPointers.get(currentPositionId));
                    dispatchListsPointers.set(currentPositionId, (dispatchListsPointers.get(currentPositionId) + 1) % dispatchListLength);
                    currentVehiclesPositions.set(carId, nextPositionId);

                    if (customersCurrentDemand.get(nextPositionId) < currentVehiclesLoad.get(carId)) {
                        currentVehiclesLoad.set(carId, currentVehiclesLoad.get(carId) - customersCurrentDemand.get(nextPositionId));
                        customersCurrentDemand.set(nextPositionId, 0.0);
                    } else {
                        customersCurrentDemand.set(nextPositionId, customersCurrentDemand.get(nextPositionId) - currentVehiclesLoad.get(carId));
                        currentVehiclesLoad.set(carId, 0.0);
                    }
                    result += addEdgeCost(currentPositionId, nextPositionId, graphStructure);

                    for (int i = 0; i < customersCurrentDemand.size(); i++) {
                        if (customersCurrentDemand.get(i) == 0.0) {
                            dispatchLists = removeFromDispatchLists(dispatchLists, i, graph, vrpsdSolution);
                        }
                    }
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        return (step < 500) ? result : max_eval;

    }

    private ArrayList<ArrayList<Integer>> removeFromDispatchLists(ArrayList<ArrayList<Integer>> dispatchLists, int customerNumber, Graph graph,
                                                                  IntegerSolution vrpsdSolution) {
        int index_number = 0;
        for (ArrayList<Integer> dispatchList : dispatchLists) {
            int old_size = dispatchList.size();
            boolean b = dispatchList.remove(new Integer(customerNumber));
            int actual_size = dispatchList.size();
            Random random = new Random();
            for (int i = 0; i < old_size - actual_size; i++) {
                Integer rand = random.nextInt(graph.getVertexNum());
                dispatchList.add(rand);
                //vrpsdSolution.setVariableValue(index_number+dispatchList.size()-1,rand);

            }
            index_number += dispatchList.size();
        }
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

    public int evaluateSimple(int numOfVehicles, double vehicleCapacity, Graph graph, ArrayList<Double> customersDemand) {
        ArrayList<Integer> currentVehiclesPositions = new ArrayList<>(Collections.nCopies(numOfVehicles, 0));
        ArrayList<Double> currentVehiclesLoad = new ArrayList<>(Collections.nCopies(numOfVehicles, vehicleCapacity));
        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();
        ArrayList<Double> customersCurrentDemand = new ArrayList<>(customersDemand);

        int step = -1;
        int result = 0;
        while (!allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if (currentVehiclesLoad.get(carId) > 0.0) {
                    int currentPositionId = currentVehiclesPositions.get(carId);
                    int nextPositionId = findClosestDemandingCustomer(currentPositionId, graphStructure, customersCurrentDemand);
                    currentVehiclesPositions.set(carId, nextPositionId);

                    if (customersCurrentDemand.get(nextPositionId) < currentVehiclesLoad.get(carId)) {
                        currentVehiclesLoad.set(carId, currentVehiclesLoad.get(carId) - customersCurrentDemand.get(nextPositionId));
                        customersCurrentDemand.set(nextPositionId, 0.0);
                    } else {
                        customersCurrentDemand.set(nextPositionId, customersCurrentDemand.get(nextPositionId) - currentVehiclesLoad.get(carId));
                        currentVehiclesLoad.set(carId, 0.0);
                    }
                    result += addEdgeCost(currentPositionId, nextPositionId, graphStructure);
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        return (step < 500) ? result : max_eval;

    }

    private int findClosestDemandingCustomer(int currentPositionId, Map<Vertex, List<Edge>> graphStructure, ArrayList<Double> customersCurrentDemand) {
        int customerId = 0;
        ArrayList<Integer> customersFitness = new ArrayList<>();
        for (Double customerDemand : customersCurrentDemand) {
            if (customerDemand != 0 && customerId != currentPositionId) {
                customersFitness.add(addEdgeCost(currentPositionId, customerId, graphStructure));
            } else {
                customersFitness.add(Integer.MAX_VALUE);
            }
            customerId++;
        }
        return customersFitness.indexOf(Collections.min(customersFitness));
    }
}
