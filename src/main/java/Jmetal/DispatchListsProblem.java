package Jmetal;

import org.uma.jmetal.problem.Problem;

public interface DispatchListsProblem<S extends DispatchListsSolution> extends Problem<S> {
    int getDispatchListsNum();
    int getDispatchListLength();
    int getVertexNum();
}
