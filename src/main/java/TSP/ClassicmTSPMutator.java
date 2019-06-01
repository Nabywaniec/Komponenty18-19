package TSP;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.Random;

public class ClassicmTSPMutator implements MutationOperator<mTSPPermutationSolution<Integer>> {

    private double mutationProbability;
    private RandomGenerator randomGenerator;
    private double distributionIndex = 1.0;
    private int numOfDrivers;
    private RepairDoubleSolution solutionRepair = new RepairDoubleSolutionAtBounds();

    public ClassicmTSPMutator(double mutationProbability, int numOfDrivers) {
        this.mutationProbability = mutationProbability;
        this.numOfDrivers = numOfDrivers;
        this.randomGenerator = () -> {
            return JMetalRandom.getInstance().nextDouble();
        };
    }

    @Override
    public mTSPPermutationSolution<Integer> execute(mTSPPermutationSolution<Integer> integermTSPPermutationSolution) {
        if (null == integermTSPPermutationSolution) {
            throw new JMetalException("Null parameter");
        } else {
            this.doMutation(this.mutationProbability, integermTSPPermutationSolution);
            return integermTSPPermutationSolution;
        }
    }

    private void doMutation(double mutationProbability, mTSPPermutationSolution<Integer> integermTSPPermutationSolution) {
        int valuesAmmount = integermTSPPermutationSolution.getNumberOfVariables() - numOfDrivers;
        Random rand = new Random();
        for (int i = 0; i < integermTSPPermutationSolution.getNumberOfVariables() - numOfDrivers; i++) {
            if ((Double) this.randomGenerator.getRandomValue() <= mutationProbability) {
                int value = rand.nextInt(valuesAmmount);
                Integer x1 = integermTSPPermutationSolution.getVariableValue(value);
                Integer x2 = integermTSPPermutationSolution.getVariableValue(i);
                integermTSPPermutationSolution.setVariableValue(i, x1);
                integermTSPPermutationSolution.setVariableValue(value,x2);
            }

        }
    }
}

