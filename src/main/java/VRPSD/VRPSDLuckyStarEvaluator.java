package VRPSD;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import org.uma.jmetal.solution.IntegerSolution;
import utils.EvaluatorUtils;

import java.util.*;

public class VRPSDLuckyStarEvaluator {

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
        ArrayList<ArrayList<Boolean>> isDispatchListSlotUsed = setupIsDispatchListSlotUsedList(dispatchLists, dispatchListLength);

        int step = -1;
        int result = 0;
        while (!evaluatorUtils.allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if(evaluatorUtils.allCustomersSupplied(customersCurrentDemand))
                    break;
                if (currentVehiclesLoad.get(carId) > 0.0) {
                    int currentPositionId = currentVehiclesPositions.get(carId);

                    boolean isNextPositionProper = false;
                    int nextPositionId = 0;
                    while(!isNextPositionProper) {
                        nextPositionId = dispatchLists.get(currentPositionId).get(dispatchListsPointers.get(currentPositionId));
                        //System.out.println(carId+" "+currentPositionId+" "+nextPositionId);
                        if(customersCurrentDemand.get(nextPositionId) != 0.0){
                            isNextPositionProper = true;
                        } else { //klient ni mo już zapotrzebowania
                            if(isDispatchListSlotUsed.get(currentPositionId).get(dispatchListsPointers.get(currentPositionId))){
                                isNextPositionProper = true;
                            } else { //klient ni mo zapotrzebowania i slot nie był używany
                                dispatchLists.get(currentPositionId).remove((int)dispatchListsPointers.get(currentPositionId));
                                dispatchLists.get(currentPositionId).add(evaluatorUtils.findClosestDemandingCustomer(currentPositionId, graphStructure, customersCurrentDemand));

                                isDispatchListSlotUsed.get(currentPositionId).remove((int)dispatchListsPointers.get(currentPositionId));
                                isDispatchListSlotUsed.get(currentPositionId).add(false);
                            }
                        }
                    }

                    isDispatchListSlotUsed.get(currentPositionId).set(dispatchListsPointers.get(currentPositionId), true);

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
        evaluatorUtils.saveSolution(vrpsdSolution, dispatchLists);

        return (step < 500) ? result : max_eval;
    }

    private ArrayList<ArrayList<Boolean>> setupIsDispatchListSlotUsedList(ArrayList<ArrayList<Integer>> dispatchLists, int dispatchListVertexLength) {
        ArrayList<ArrayList<Boolean>> isDispatchListSlotUsed = new ArrayList<>();
        for (int dispatchListNum = 0; dispatchListNum < dispatchLists.size(); dispatchListNum++) {
            ArrayList<Boolean> isDispatchListSlotUsedSingle = new ArrayList<>();
            for (int dispatchListSlotId = 0; dispatchListSlotId < dispatchListVertexLength; dispatchListSlotId++) {
                isDispatchListSlotUsedSingle.add(false);
            }
            isDispatchListSlotUsed.add(dispatchListNum, isDispatchListSlotUsedSingle);
        }
        return isDispatchListSlotUsed;
    }
}
