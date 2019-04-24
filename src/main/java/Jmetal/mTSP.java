package Jmetal;

import org.uma.jmetal.problem.impl.AbstractGenericProblem;

public class mTSP extends AbstractGenericProblem<DispatchListsSolution> implements DispatchListsProblem<DispatchListsSolution>{
    @Override
    public int getDispatchListsNum() {
        return 0;
    }

    @Override
    public int getDispatchListLength() {
        return 0;
    }

    @Override
    public int getVertexNum() {
        return 0;
    }

    @Override
    public void evaluate(DispatchListsSolution dispatchListsSolution) {

    }

    @Override
    public DispatchListsSolution createSolution() {
        return new DefaultDispatchListsSolution(this);
    }
}
