package Jmetal;

import org.uma.jmetal.problem.Problem;

public interface DispatchListsProblem extends Problem<Integer> {
    int getDispatchListsNum();
    int getDispatchListLength();
    int getVertexNum();
}
