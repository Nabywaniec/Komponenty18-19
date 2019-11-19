package Model;

import java.io.*;
import java.util.*;

public class Graph {

    private Map<Vertex, List<Edge>> structure = new HashMap<>();
    private Map<Integer, List<Integer>> dispatchList = new HashMap<>();
    private int vertexNum = 0;
    private Map<Integer, List<Integer>>nearestNeighbours = new HashMap<>();

    public Map<Vertex, List<Edge>> getStructure() {
        return structure;
    }

    public void setDispatchList(List<Integer> dispatchListRaw, int dispatchListVertexLength) {
        for(int vertexId = 0; vertexId < vertexNum; vertexId++){
            List<Integer> vertexDispatchList = new ArrayList<>();
            for(int dispatchListVertexNum = 0; dispatchListVertexNum < dispatchListVertexLength; dispatchListVertexNum++){
                vertexDispatchList.add(dispatchListRaw.get(vertexId*dispatchListVertexLength+dispatchListVertexNum));
            }
            this.dispatchList.put(vertexId, vertexDispatchList);
        }
    }

    public Map<Integer, List<Integer>> getDispatchList() {
        return dispatchList;
    }

    public void setStructure(String fileName) {
        File file = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> splitted = Arrays.asList(line.split(":"));
                int vertexId = Integer.parseInt(splitted.get(0));
                Vertex vertex = new Vertex(vertexId);
                String[] neighbours = splitted.get(1).split(" ");
                String[] edgeCosts = splitted.get(2).split(" ");
                List<Edge> neighboursList = new ArrayList<>();
                int i = 0;
                for (String neighbour : neighbours) {
                    neighboursList.add(new Edge(Integer.parseInt(edgeCosts[i]), vertexId, Integer.parseInt(neighbour)));
                    i++;
                }
                structure.put(vertex, neighboursList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.vertexNum = structure.size();
    }

    public void setFullGraphStructure(String fileName){
        try{
            List<Vertex> vertexesList = getVertexesList(fileName);
            setStructure(getDistances(vertexesList));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setFullGraphStructureWithVertexList(List<Vertex> vertexesList){
        try{
            setStructure(getDistances(vertexesList));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Vertex> getVertexesList(String fileName) {
        File file = new File(fileName);
        List<Vertex> vertexList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> splitted = Arrays.asList(line.split("\t| "));
                int vertexId = Integer.parseInt(splitted.get(0))-1;
                int x = Integer.parseInt(splitted.get(1));
                int y = Integer.parseInt(splitted.get(2));
                Vertex vertex = new Vertex(vertexId, x, y);
                vertexList.add(vertex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vertexList;
    }

    public double[][] getDistances(List<Vertex> vertexes) {
        int vertexesNumber = vertexes.size();
        double[][] distances;
        distances = new double[vertexesNumber][vertexesNumber];
        for (int i = 0; i < vertexesNumber; i++) {
            for (int j = 0; j < vertexesNumber; j++) {
                Vertex v1 = vertexes.get(i);
                Vertex v2 = vertexes.get(j);
                distances[i][j] = Math.sqrt(Math.pow((v2.getX() - v1.getX()), 2) + Math.pow(v2.getY() - v1.getY(), 2));
            }
        }
        return distances;
    }

    public void setStructure(double[][] distances){
        Map<Vertex, List<Edge>> structure = new HashMap<>();
        for(int i=0;i<distances.length;i++){
            List<Edge> edges = new ArrayList<>();
            for(int j=0;j<distances.length;j++){
                edges.add(new Edge(distances[i][j], i, j));
            }
            structure.put(new Vertex(i), edges);
        }
        this.structure = structure;
        this.vertexNum = structure.size();
    }

    public void setNearestNeighboursMap(){
        List<Vertex> vertexList = new ArrayList<>();
        vertexList.addAll(structure.keySet());
        double[][] distances = getDistances(vertexList);
        for(int i=0;i<vertexNum;i++){
            ArrayList<Double> distancesForSingleVertex = new ArrayList<>();
            for(int j=0;j<vertexNum;j++){
                if(i != j){
                    distancesForSingleVertex.add(distances[i][j]);
                }
            }
            ArrayList<Double> distancesForSingleVertexSorted = new ArrayList<>(distancesForSingleVertex);
            Collections.sort(distancesForSingleVertex);
            List<Integer> nearestVertexesIndexes = new ArrayList<>();
            for(int k=0;k<4;k++){
                nearestVertexesIndexes.add(distancesForSingleVertex.indexOf(distancesForSingleVertexSorted.get(k)));
            }
            nearestNeighbours.put(i, nearestVertexesIndexes);
        }
    }

    public Map<Integer, List<Integer>> getNearestNeighbours(){
        return nearestNeighbours;
    }


    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.setStructure("src/main/resources/file.txt");
        System.out.println(graph.structure.entrySet());
    }

    public int getVertexNum() {
        return this.vertexNum;
    }
}
