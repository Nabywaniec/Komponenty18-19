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
        ArrayList<ArrayList<Boolean>> isDispatchListSlotUsed = setupIsDispatchListSlotUsedList(dispatchLists, dispatchListLength);

        int step = -1;
        int result = 0;
        while (!EvaluatorUtils.allCustomersSupplied(customersCurrentDemand) && step < max_steps) {
            step += 1;
            for (int carId = 0; carId < numOfVehicles; carId++) {
                if(EvaluatorUtils.allCustomersSupplied(customersCurrentDemand))
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
                                dispatchLists.get(currentPositionId).add(findClosestDemandingCustomer(currentPositionId, graphStructure, customersCurrentDemand));

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
                    result += EvaluatorUtils.addEdgeCost(currentPositionId, nextPositionId, graphStructure);
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += EvaluatorUtils.addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        EvaluatorUtils.saveSolution(vrpsdSolution, dispatchLists);

        return (step < 500) ? result : max_eval;
    }

    private int findClosestDemandingCustomer(int currentPositionId, Map<Vertex, List<Edge>> graphStructure, ArrayList<Double> customersCurrentDemand) {
        int customerId = 0;
        ArrayList<Integer> customersFitness = new ArrayList<>();
        for(Double customerDemand : customersCurrentDemand){
            if(customerDemand == 0 || customerId == currentPositionId || customerId == 0){
                customersFitness.add(Integer.MAX_VALUE);
            } else {
                customersFitness.add(EvaluatorUtils.addEdgeCost(currentPositionId, customerId, graphStructure));
            }
            customerId++;
        }
        return customersFitness.indexOf(Collections.min(customersFitness));
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
}
