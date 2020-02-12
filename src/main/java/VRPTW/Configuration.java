package VRPTW;

import Model.Vertex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration {

    private String configurationFilename;
    private String name;
    private String comment;
    private String type;
    private int verticesNum;
    private String edgeWeightType;
    private int capacity;
    private List<Vertex> vertexesList = new ArrayList<>();
    private List<Double> readyTimes = new ArrayList<>();
    private List<Double> dueTimes = new ArrayList<>();
    private List<Double> customersDemand = new ArrayList<>();
    private int bestValue;
    private int minNumOfTrucks;

    public Configuration(String configurationFilename) {
        this.configurationFilename = configurationFilename;

        File file = new File(configurationFilename);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.contains("NAME")){
                    this.name = line.split(" : ")[1];
                }
                if(line.contains("COMMENT")){
                    this.comment = line.split(" : ")[1];
                }
                if(line.contains("TYPE")){
                    this.type = line.split(" : ")[1];
                }
                if(line.contains("DIMENSION")){
                    this.verticesNum = Integer.parseInt(line.split(" : ")[1]);
                }
                if(line.contains("EDGE_WEIGHT_TYPE")){
                    this.edgeWeightType = line.split(" : ")[1];
                }
                if(line.contains("CAPACITY")){
                    this.capacity = Integer.parseInt(line.split(" : ")[1]);
                }
                if(line.contains("NODE_COORD_SECTION")){
                    for(int vertexNum=0; vertexNum < this.verticesNum; vertexNum++){
                        line = br.readLine();
                        System.out.println(line);
                        List<String> splitted = Arrays.asList(line.split(" "));
                        int vertexId = Integer.parseInt(splitted.get(0))-1;
                        int x = (int) Double.parseDouble(splitted.get(1));
                        int y = (int) Double.parseDouble(splitted.get(2));
                        double readyTime = Double.parseDouble(splitted.get(4));
                        double dueTime = Double.parseDouble(splitted.get(5));
                        double customerDemand = Double.parseDouble(splitted.get(3));
                        readyTimes.add(readyTime);
                        dueTimes.add(dueTime);
                        customersDemand.add(customerDemand);
                        Vertex vertex = new Vertex(vertexId, x, y);
                        this.vertexesList.add(vertex);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //to tak na razie wpisane na sztywno
        this.minNumOfTrucks =20;
        this.bestValue = 600;

    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getType() {
        return type;
    }

    public int getVerticesNum() {
        return verticesNum;
    }

    public String getEdgeWeightType() {
        return edgeWeightType;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Vertex> getVertexesList() {
        return vertexesList;
    }

    public List<Double> getReadyTimes(){return readyTimes;}

    public List<Double> getCustomersDemand(){return customersDemand;}

    public List<Double> getDueTimes(){return dueTimes;}

    public int getBestValue() {
        return bestValue;
    }

    public int getMinNumOfTrucks() {
        return minNumOfTrucks;
    }
}
