package Operators;

import Model.Vertex;

import java.util.*;

public class Crossover {

    public Crossover() {

    }

    public Map<Vertex, List<Integer>> cross(Map<Vertex, List<Integer>> first,
                                            Map<Vertex, List<Integer>> second) {
        Map<Vertex, List<Integer>> result = new HashMap<>();
        for (Map.Entry<Vertex, List<Integer>> entry : first.entrySet()) {
            Vertex key = entry.getKey();
            List<Integer> neighboursFirst = entry.getValue();
            List<Integer> neighboursSecond = second.get(key);
            for (int i = 0; i < neighboursFirst.size(); i++) {
                Random r = new Random();
                if (r.nextBoolean()) {
                    neighboursFirst.set(i, neighboursSecond.get(i));
                }
            }
            result.put(key, neighboursFirst);
        }
        return result;
    }

}
