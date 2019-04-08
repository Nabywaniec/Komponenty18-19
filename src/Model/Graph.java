package Model;

import java.io.*;
import java.util.*;

public class Graph {

    private Map<Vertex,List<Edge>> structure = new HashMap<>();


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
                int i=0;
                for (String neighbour : neighbours) {
                    neighboursList.add(new Edge(Integer.parseInt(edgeCosts[i]),vertexId,Integer.parseInt(neighbour)));
                }
                structure.put(vertex, neighboursList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.setStructure("file.txt");
        System.out.println(graph.structure.entrySet());
    }

}
