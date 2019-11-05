package VRPSD;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DemandFactory {

    public ArrayList<Double> createCustomersDemands(int customers, double alpha, double gamma, double capacity){
        ArrayList<Double> customerDemands = new ArrayList<>();
        for (int i=1; i<=customers; i++) {
            customerDemands.add(demandFunction(alpha, gamma, capacity));
        }
        return customerDemands;
    }

    public ArrayList<Double> readCustomersDemandsFromFile(String filename){
        return null;
    }

    private Double demandFunction(double alpha, double gamma, double capacity) {
        double delta = ThreadLocalRandom.current().nextDouble(0, 1);
        return Math.floor(alpha * capacity + delta * (gamma - alpha) * capacity);
    }

}
