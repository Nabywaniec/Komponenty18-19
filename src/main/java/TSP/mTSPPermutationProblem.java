package TSP;

import org.uma.jmetal.problem.Problem;

public interface mTSPPermutationProblem<S extends mTSPPermutationSolution<?>> extends Problem<S> {
    int getVertices();
    int getCars();
}
