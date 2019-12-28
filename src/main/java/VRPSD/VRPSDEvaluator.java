package VRPSD;

import Model.Edge;
import Model.Graph;
import Model.Vertex;
import org.uma.jmetal.solution.IntegerSolution;
import utils.EvaluatorUtils;

import java.util.*;

public class VRPSDEvaluator {

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

                    for (int i = 0; i < customersCurrentDemand.size(); i++) {
                        if (customersCurrentDemand.get(i) == 0.0) {
                            dispatchLists = removeFromDispatchLists(dispatchLists, i, graph, currentPositionId, dispatchListsPointers,
                                    customersCurrentDemand);
                        }
                    }
                }
            }
        }
        for (int carId = 0; carId < numOfVehicles; carId++) {
            result += evaluatorUtils.addEdgeCost(currentVehiclesPositions.get(carId), 0, graphStructure);
        }
        evaluatorUtils.saveSolution(vrpsdSolution, dispatchLists);
        return (step < 500) ? result : max_eval;

    }

    private ArrayList<ArrayList<Integer>> removeFromDispatchLists(ArrayList<ArrayList<Integer>> dispatchLists, int customerNumber, Graph graph,
                                                                  Integer currentPositionId, ArrayList<Integer> dispatchListsPointers,
                                                                  ArrayList<Double> customersCurrentDemand) {
        ArrayList<Integer> dispatchList = dispatchLists.get(currentPositionId);
        int old_size = dispatchList.size();
        for(int i=0;i<dispatchList.size(); i++){
            if(dispatchList.get(i).equals(customerNumber) && dispatchListsPointers.get(currentPositionId)  < i){
                dispatchList.set(i, new Integer(-1));
            }
        }
        while(dispatchList.contains(-1)){
            dispatchList.remove(new Integer(-1));
        }
        int actual_size =  dispatchList.size();
        Random random = new Random();
        ArrayList<Double> customersCurrentDemandSorted = new ArrayList<>(customersCurrentDemand);
        Collections.sort(customersCurrentDemandSorted, Collections.reverseOrder());

        for(int i=0;i<old_size-actual_size;i++){
            int randomIndex = random.nextInt(10) % 3;
            dispatchList.add(customersCurrentDemand.indexOf(customersCurrentDemandSorted.get(randomIndex)));
            //  dispatchList.add(graph.getNearestNeighbours().get(currentPositionId).get(randomIndex));
        }
        dispatchLists.set(currentPositionId, dispatchList);
        return dispatchLists;

    }
}
