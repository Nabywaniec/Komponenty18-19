package Jmetal;

import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;

import java.util.*;

public class ArrayIntegerSolution extends AbstractGenericSolution<Integer, IntegerProblem>{

    protected ArrayIntegerSolution(IntegerProblem problem) {
        super(problem);
    }

    public ArrayIntegerSolution(ArrayIntegerSolution solution) {
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
        return new ArrayIntegerSolution(this);
    }

    @Override
    public Map<Object, Object> getAttributes() {
        return super.attributes;
    }
}
