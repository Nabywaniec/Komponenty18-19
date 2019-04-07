package Model;

import java.io.*;
import java.util.*;

public class Graph {

    private Map<Vertex, List<Integer>> structure = new HashMap<>();
    private Map<Vertex, List<Integer>> redirections = new HashMap<>();


    public void setStructure(String fileName) {
        File file = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> splitted = Arrays.asList(line.split(":"));
                int vertexId = Integer.parseInt(splitted.get(0));
                Vertex vertex = new Vertex(vertexId);
                List<String> neighbours = Arrays.asList(splitted.get(1).toString().split(" "));
                List<Integer> neighboursList = new ArrayList<>();
                for (String neighbour : neighbours) {
                    Integer vertedId = Integer.parseInt(neighbour);
                    neighboursList.add(vertedId);
                    structure.put(vertex, neighboursList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.setStructure("file.txt");
        System.out.println(graph.structure);
    }

}
