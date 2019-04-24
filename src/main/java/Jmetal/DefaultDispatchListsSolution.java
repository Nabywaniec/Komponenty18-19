package Jmetal;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;

import java.util.*;

public class DefaultDispatchListsSolution extends AbstractGenericSolution<Integer, DispatchListsProblem<?>> implements DispatchListsSolution{

    protected DefaultDispatchListsSolution(DispatchListsProblem problem) {
        super(problem);
        List<Integer> randomSequence = new ArrayList(problem.getVertexNum());

        int i;
        for(i = 0; i < problem.getVertexNum(); ++i) {
            randomSequence.add(i);
        }

        Collections.shuffle(randomSequence);

        for(i = 0; i < this.getNumberOfVariables(); ++i) {
            this.setVariableValue(i, randomSequence.get(i));
        }
    }

    public DefaultDispatchListsSolution(DefaultDispatchListsSolution solution) {
        this(solution.problem);

        int i;
        for(i = 0; i < this.problem.getNumberOfVariables(); ++i) {
            this.setVariableValue(i, solution.getVariableValue(i).intValue());
        }

        for(i = 0; i < this.problem.getNumberOfObjectives(); ++i) {
            this.setObjective(i, solution.getObjective(i));
        }

        this.attributes = new HashMap(solution.attributes);
    }

    @Override
    public String getVariableValueString(int i) {
        return this.getVariableValue(i).toString();
    }

    @Override
    public Solution<Integer> copy() {
        return new DefaultDispatchListsSolution(this);
    }

    @Override
    public Map<Object, Object> getAttributes() {
        return super.attributes;
    }
}
