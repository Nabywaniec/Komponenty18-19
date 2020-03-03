package VRPTW;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import VRPSD.VRPSD;
import org.uma.jmetal.solution.IntegerSolution;
import utils.EvaluatorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VRPTWLuckyStarEvaluator {

    private int max_eval = 100000;
    private int max_steps = 300;
    private EvaluatorUtils evaluatorUtils = new EvaluatorUtils();

    public int evaluate(VRPTW vrptwProblem, IntegerSolution vrpsdSolution, Graph graph, List<Double> customersDemand) {
        int numOfVehicles = vrptwProblem.getNumOfVehicles();
        double vehicleCapacity = vrptwProblem.getCapacity();
        int dispatchListLength = vrptwProblem.getDispatchListLength();
        int depotDispatchListLength = vrptwProblem.getDepotDispatchListLength();
        int vertexNum = graph.getVertexNum();
        List<Double> timeCounter = new ArrayList<Double>(Collections.nCopies(numOfVehicles, 0.0));
        List<Double> readyTimes = vrptwProblem.getReadyTimes();
        List<Double> dueTimes = vrptwProblem.getDueTimes();
        List<Double> serviceTimes = vrptwProblem.getServiceTimes();

        ArrayList<Integer> currentVehiclesPositions = new ArrayList<>(Collections.nCopies(numOfVehicles, 0));
        ArrayList<Double> currentVehiclesLoad = new ArrayList<>(Collections.nCopies(numOfVehicles, vehicleCapacity));
        ArrayList<ArrayList<Integer>> dispatchLists =
                evaluatorUtils.extractDispatchListsFromSolutionWithVariableDepot(vrpsdSolution.getVariables(), dispatchListLength, vertexNum, depotDispatchListLength);
        ArrayList<Integer> dispatchListsPointers = new ArrayList<>(Collections.nCopies(vertexNum, 0));
        Map<Vertex, List<Edge>> graphStructure = graph.getStructure();
        ArrayList<Double> customersCurrentDemand = new ArrayList<>(customersDemand);
        ArrayList<ArrayList<Boolean>> isDispatchListSlotUsed = setupIsDispatchListSlotUsedList(dispatchLists, dispatchListLength, depotDispatchListLength);

        int step = -1;
        int result = 0;
        while (!evaluatorUtils.allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if(evaluatorUtils.allCustomersSupplied(customersCurrentDemand))
                    break;
                if (currentVehiclesLoad.get(carId) > 0.0 && timeCounter.get(carId).equals((double) step)) {
                    int currentPositionId = currentVehiclesPositions.get(carId);

                    boolean isNextPositionProper = false;
                    boolean hasDemand = false;
                    boolean ableToDrive = true;
                    int nextPositionId = 0;
                    while(!isNextPositionProper) {
                        nextPositionId = dispatchLists.get(currentPositionId).get(dispatchListsPointers.get(currentPositionId));
                        int currentDistance = evaluatorUtils.addEdgeCost(currentPositionId, nextPositionId, graphStructure);
                        //jeśli klient nie jest zaopatrzony i jest w dobrym czasie to #G
                        if(customersCurrentDemand.get(nextPositionId) != 0.0
                                && (timeCounter.get(carId) + currentDistance >= readyTimes.get(nextPositionId)
                                && timeCounter.get(carId) + currentDistance <= dueTimes.get(nextPositionId))){
                            isNextPositionProper = true;
                            hasDemand = true;
                            timeCounter.set(carId, timeCounter.get(carId) + currentDistance);
                        } else { //klient ni mo już zapotrzebowania lub jest o złym czasie a slot był już używany
                            if(isDispatchListSlotUsed.get(currentPositionId).get(dispatchListsPointers.get(currentPositionId))){
                                isNextPositionProper = true;
                                timeCounter.set(carId, timeCounter.get(carId) + currentDistance);
                            } else { //(klient ni mo zapotrzebowania lub ma zły czas) i slot nie był używany
                                int potentiallyLegitPosition = evaluatorUtils.findClosestDemandingCustomerWithinTime(currentPositionId,
                                        graphStructure, customersCurrentDemand, step, readyTimes, dueTimes);
                                if(potentiallyLegitPosition != 0){
                                    dispatchLists.get(currentPositionId).remove((int)dispatchListsPointers.get(currentPositionId));
                                    dispatchLists.get(currentPositionId).add(potentiallyLegitPosition);
                                    isDispatchListSlotUsed.get(currentPositionId).remove((int)dispatchListsPointers.get(currentPositionId));
                                    isDispatchListSlotUsed.get(currentPositionId).add(false);
                                } else {
                                    //next pos is no pos
                                    isNextPositionProper = true;
                                    ableToDrive = false;
                                    timeCounter.set(carId, timeCounter.get(carId) + 1);
                                }
                            }
                        }
                    }

                    if(!ableToDrive)
                        continue;

                    //jeśli dojdzie do dostarczania, to dodaj czas dostarczania
                    if(hasDemand)
                        timeCounter.set(carId, timeCounter.get(carId) + serviceTimes.get(nextPositionId));

                    isDispatchListSlotUsed.get(currentPositionId).set(dispatchListsPointers.get(currentPositionId), true);

                    if(currentPositionId == 0){
                        dispatchListsPointers.set(currentPositionId, (dispatchListsPointers.get(currentPositionId) + 1) % depotDispatchListLength);
                    } else {
                        dispatchListsPointers.set(currentPositionId, (dispatchListsPointers.get(currentPositionId) + 1) % dispatchListLength);
                    }
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

        System.out.println((step < max_steps) ? result : max_eval);
        return (step < max_steps) ? result : max_eval;
    }

    private ArrayList<ArrayList<Boolean>> setupIsDispatchListSlotUsedList(ArrayList<ArrayList<Integer>> dispatchLists,
                                                                          int dispatchListVertexLength,
                                                                          int depotDispatchListLength) {
        ArrayList<ArrayList<Boolean>> isDispatchListSlotUsed = new ArrayList<>();
        isDispatchListSlotUsed.add(0, new ArrayList<>(Collections.nCopies(depotDispatchListLength, false)));

        for (int dispatchListNum = 1; dispatchListNum < dispatchLists.size(); dispatchListNum++) {
            isDispatchListSlotUsed.add(dispatchListNum, new ArrayList<>(Collections.nCopies(dispatchListVertexLength, false)));
        }
        return isDispatchListSlotUsed;
    }
}
