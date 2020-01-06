package VRPSD;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import org.uma.jmetal.solution.IntegerSolution;
import utils.EvaluatorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VRPSDSimpleEvaluator {

    private int max_eval = 100000;
    private int max_steps = 500;
    private EvaluatorUtils evaluatorUtils = new EvaluatorUtils();

    public int evaluate(VRPSD vrpsdProblem, IntegerSolution vrpsdSolution, Graph graph, ArrayList<Double> customersDemand) {
        int numOfVehicles = vrpsdProblem.getNumOfVehicles();
        double vehicleCapacity = vrpsdProblem.getCapacity();
        int dispatchListLength = vrpsdProblem.getDispatchListLength();
        int vertexNum = graph.getVertexNum();

        ArrayList<Integer> currentVehiclesPositions = new ArrayList<>(Collections.nCopies(numOfVehicles, 0));
        ArrayList<Double> currentVehiclesLoad = new ArrayList<>(Collections.nCopies(numOfVehicles, vehicleCapacity));
        ArrayList<ArrayList<Integer>> dispatchLists =
                evaluatorUtils.extractDispatchListsFromSolution(vrpsdSolution.getVariables(), dispatchListLength, vertexNum);
        ArrayList<Integer> dispatchListsPointers = new ArrayList<>(Collections.nCopies(vertexNum, 0));
        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();
        ArrayList<Double> customersCurrentDemand = new ArrayList<>(customersDemand);

        int step = -1;
        int result = 0;
        while (!evaluatorUtils.allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if(evaluatorUtils.allCustomersSupplied(customersCurrentDemand))
                    break;
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
                    result += evaluatorUtils.addEdgeCost(currentPositionId, nextPositionId, graphStructure);
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += evaluatorUtils.addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        return (step < 500) ? result : max_eval;

    }



    public int evaluateSimple(int numOfVehicles, double vehicleCapacity, Graph graph, ArrayList<Double> customersDemand) {
        ArrayList<Integer> currentVehiclesPositions = new ArrayList<>(Collections.nCopies(numOfVehicles, 0));
        ArrayList<Double> currentVehiclesLoad = new ArrayList<>(Collections.nCopies(numOfVehicles, vehicleCapacity));
        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();
        ArrayList<Double> customersCurrentDemand = new ArrayList<>(customersDemand);

        int step = -1;
        int result = 0;
        while (!evaluatorUtils.allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if(evaluatorUtils.allCustomersSupplied(customersCurrentDemand))
                    break;
                if (currentVehiclesLoad.get(carId) > 0.0) {
                    int currentPositionId = currentVehiclesPositions.get(carId);
                    int nextPositionId = evaluatorUtils.findClosestDemandingCustomer(currentPositionId, graphStructure, customersCurrentDemand);
                    currentVehiclesPositions.set(carId, nextPositionId);

                    if (customersCurrentDemand.get(nextPositionId) < currentVehiclesLoad.get(carId)) {
                        currentVehiclesLoad.set(carId, currentVehiclesLoad.get(carId) - customersCurrentDemand.get(nextPositionId));
                        customersCurrentDemand.set(nextPositionId, 0.0);
                    } else {
                        customersCurrentDemand.set(nextPositionId, customersCurrentDemand.get(nextPositionId) - currentVehiclesLoad.get(carId));
                        currentVehiclesLoad.set(carId, 0.0);
                    }
                    result += evaluatorUtils.addEdgeCost(currentPositionId, nextPositionId, graphStructure);
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += evaluatorUtils.addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        return (step < 500) ? result : max_eval;

    }

    public int simpleEvaluateStringDispatchList(String dispatchListRawString, Graph graph, ArrayList<Double> customersDemand,
                                                int numOfVehicles, double vehicleCapacity, int dispatchListLength) {
        String[] dispatchListRawStringSplit = dispatchListRawString.split(" ");
        ArrayList<Integer> dispatchListRaw = new ArrayList<>();
        for (String num: dispatchListRawStringSplit) {
            dispatchListRaw.add(Integer.parseInt(num));
        }

        int vertexNum = graph.getVertexNum();

        ArrayList<Integer> currentVehiclesPositions = new ArrayList<>(Collections.nCopies(numOfVehicles, 0));
        ArrayList<Double> currentVehiclesLoad = new ArrayList<>(Collections.nCopies(numOfVehicles, vehicleCapacity));
        ArrayList<ArrayList<Integer>> dispatchLists =
                evaluatorUtils.extractDispatchListsFromSolution(dispatchListRaw, dispatchListLength, vertexNum);
        ArrayList<Integer> dispatchListsPointers = new ArrayList<>(Collections.nCopies(vertexNum, 0));
        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();
        ArrayList<Double> customersCurrentDemand = new ArrayList<>(customersDemand);

        int step = -1;
        int result = 0;
        while (!evaluatorUtils.allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if(evaluatorUtils.allCustomersSupplied(customersCurrentDemand))
                    break;
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
                    result += evaluatorUtils.addEdgeCost(currentPositionId, nextPositionId, graphStructure);
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += evaluatorUtils.addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        return (step < 500) ? result : max_eval;

    }

}
