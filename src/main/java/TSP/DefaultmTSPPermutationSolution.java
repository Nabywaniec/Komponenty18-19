package TSP;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;

import java.util.*;

public class DefaultmTSPPermutationSolution
        extends AbstractGenericSolution<Integer, mTSPPermutationProblem<?>>
        implements mTSPPermutationSolution<Integer> {

    public DefaultmTSPPermutationSolution(mTSPPermutationProblem<?> problem) {
        super(problem);
        List<Integer> randomSequence = new ArrayList(problem.getVertices());

        int i;
        for(i = 0; i < problem.getVertices(); ++i) {
            randomSequence.add(i);
        }

        Collections.shuffle(randomSequence);

        for(i = 0; i < problem.getVertices(); ++i) {
            this.setVariableValue(i, randomSequence.get(i));
        }

        int vals[] = getRandomArraySum(problem.getCars(), problem.getVertices());
        for(i = problem.getVertices(); i < problem.getVertices() + problem.getCars(); ++i) {
            this.setVariableValue(i, vals[i-problem.getVertices()]);
        }
    }

    public DefaultmTSPPermutationSolution(DefaultmTSPPermutationSolution solution) {
        super(solution.problem);

        int i;
        for(i = 0; i < ((mTSPPermutationProblem)this.problem).getNumberOfObjectives(); ++i) {
            this.setObjective(i, solution.getObjective(i));
        }

        for(i = 0; i < ((mTSPPermutationProblem)this.problem).getNumberOfVariables(); ++i) {
            this.setVariableValue(i, solution.getVariableValue(i));
        }

        this.attributes = new HashMap(solution.attributes);
    }

    private int[] getRandomArraySum(int arrayLength, int sum) {
        java.util.Random g = new java.util.Random();

        int vals[] = new int[arrayLength];
        sum -= arrayLength;

        for (int i = 0; i < arrayLength-1; ++i) {
            vals[i] = g.nextInt(sum);
        }
        vals[arrayLength-1] = sum;

        java.util.Arrays.sort(vals);
        for (int i = arrayLength-1; i > 0; --i) {
            vals[i] -= vals[i-1];
        }
        for (int i = 0; i < arrayLength; ++i) { ++vals[i]; }
        return vals;
    }

    @Override
    public String getVariableValueString(int i) {
        return ((Integer)this.getVariableValue(i)).toString();
    }

    @Override
    public Solution<Integer> copy() {
        return new DefaultmTSPPermutationSolution(this);
    }

    @Override
    public Map<Object, Object> getAttributes() {
        return this.attributes;
    }
}
