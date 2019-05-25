package TSP;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class ClassicmTSPMutator implements MutationOperator<mTSPPermutationSolution<Integer>> {

    private double mutationProbability;
    private RandomGenerator randomGenerator;
    private double distributionIndex = 1.0;
    private RepairDoubleSolution solutionRepair = new RepairDoubleSolutionAtBounds();

    public ClassicmTSPMutator(double mutationProbability) {
        this.mutationProbability = mutationProbability;
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
        for(int i = 0; i < integermTSPPermutationSolution.getNumberOfVariables(); ++i) {
            if ((Double)this.randomGenerator.getRandomValue() <= mutationProbability) {
                double y = (double)(Integer)integermTSPPermutationSolution.getVariableValue(i);
                double yl = 0.0;
                double yu = 10.0;
                if (yl == yu) {
                    y = yl;
                } else {
                    Double delta1 = (y - yl) / (yu - yl);
                    Double delta2 = (yu - y) / (yu - yl);
                    Double rnd = (Double)this.randomGenerator.getRandomValue();
                    Double mutPow = 2.0D / (this.distributionIndex + 2.0D);
                    Double deltaq;
                    double val;
                    double xy;
                    if (rnd <= 0.5D) {
                        xy = 1.0D - delta1;
                        val = 2.0D * rnd + (1.0D - 2.0D * rnd) * Math.pow(xy, this.distributionIndex + 2.0D);
                        deltaq = Math.pow(val, mutPow) - 1.0D;
                    } else {
                        xy = 1.0D - delta2;
                        val = 2.0D * (1.0D - rnd) + 2.0D * (rnd - 0.5D) * Math.pow(xy, this.distributionIndex + 2.0D);
                        deltaq = 1.0D - Math.pow(val, mutPow);
                    }

                    y += deltaq * (yu - yl);
                    y = this.solutionRepair.repairSolutionVariableValue(y, yl, yu);
                }

                integermTSPPermutationSolution.setVariableValue(i, (int)y);
            }
        }

    }
    }

