package Operators;

import Model.Vertex;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Mutator {

    public Mutator() {

    }

    public Map<Vertex, List<Integer>> mutate(Map<Vertex, List<Integer>> redirections) {
        for (Map.Entry<Vertex, List<Integer>> entry : redirections.entrySet()) {
            List<Integer> redirectionsForVertex = entry.getValue();
            Collections.shuffle(redirectionsForVertex);
            entry.setValue(redirectionsForVertex);
        }
        return redirections;
    }

}
