package TSP;

import org.uma.jmetal.problem.impl.AbstractGenericProblem;

public abstract class AbstractmTSPPermutationProblem
        extends AbstractGenericProblem<mTSPPermutationSolution<Integer>>
        implements mTSPPermutationProblem<mTSPPermutationSolution<Integer>> {
    public AbstractmTSPPermutationProblem(){
    }

    public mTSPPermutationSolution<Integer> createSolution() {
        return new DefaultmTSPPermutationSolution(this);
    }
}
