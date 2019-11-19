package VRPSD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DemandFactory {

    public ArrayList<Double> createCustomersDemands(int customers, double alpha, double gamma, double capacity){
        ArrayList<Double> customerDemands = new ArrayList<>();
        for (int i=1; i<=customers; i++) {
            customerDemands.add(demandFunction(alpha, gamma, capacity));
        }
        return customerDemands;
    }

    public ArrayList<Double> readCustomersDemandsFromFile(String fileName){
        File file = new File(fileName);
        ArrayList<Double> customerDemands = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while(!br.readLine().contains("DEMAND_SECTION")) {}
            while ((line = br.readLine()) != null) {
                if(line.contains("DEPOT_SECTION"))
                    break;
                List<String> splitted = Arrays.asList(line.split("\t| "));
                int vertexId = Integer.parseInt(splitted.get(0))-1;
                double demand = (double) Integer.parseInt(splitted.get(1));
                customerDemands.add(demand);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customerDemands;
    }

    private Double demandFunction(double alpha, double gamma, double capacity) {
        double delta = ThreadLocalRandom.current().nextDouble(0, 1);
        return Math.floor(alpha * capacity + delta * (gamma - alpha) * capacity);
    }

}
