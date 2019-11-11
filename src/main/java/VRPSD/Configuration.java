package VRPSD;

import Model.Vertex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configuration {

    private String configurationFilename;
    private String name;
    private String comment;
    private String type;
    private int verticesNum;
    private String edgeWeightType;
    private int capacity;
    private List<Vertex> vertexesList = new ArrayList<>();
    private ArrayList<Double> customerDemands = new ArrayList<>();

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
                        List<String> splitted = Arrays.asList(line.split("\t| "));
                        int vertexId = Integer.parseInt(splitted.get(0))-1;
                        int x = Integer.parseInt(splitted.get(1));
                        int y = Integer.parseInt(splitted.get(2));
                        Vertex vertex = new Vertex(vertexId, x, y);
                        this.vertexesList.add(vertex);
                    }
                }
                if(line.contains("DEMAND_SECTION")){
                    for(int vertexNum=0; vertexNum < this.verticesNum; vertexNum++){
                        line = br.readLine();
                        List<String> splitted = Arrays.asList(line.split("\t| "));
                        int vertexId = Integer.parseInt(splitted.get(0))-1;
                        double demand = (double) Integer.parseInt(splitted.get(1));
                        this.customerDemands.add(demand);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public ArrayList<Double> getCustomerDemands() {
        return customerDemands;
    }
}
