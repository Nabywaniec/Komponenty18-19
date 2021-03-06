package mTSP;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class Mutator implements MutationOperator<IntegerSolution> {
    private static final double DEFAULT_PROBABILITY = 0.01D;
    private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0D;
    private double distributionIndex;
    private double mutationProbability;
    private RepairDoubleSolution solutionRepair;
    private RandomGenerator<Double> randomGenerator;

    public Mutator() {
        this(0.01D, 20.0D);
    }

    public Mutator(IntegerProblem problem, double distributionIndex) {
        this(1.0D / (double)problem.getNumberOfVariables(), distributionIndex);
    }

    public Mutator(double mutationProbability, double distributionIndex) {
        this(mutationProbability, distributionIndex, new RepairDoubleSolutionAtBounds());
    }

    public Mutator(double mutationProbability, double distributionIndex, RepairDoubleSolution solutionRepair) {
        this(mutationProbability, distributionIndex, solutionRepair, () -> {
            return JMetalRandom.getInstance().nextDouble();
        });
    }

    public Mutator(double mutationProbability, double distributionIndex, RepairDoubleSolution solutionRepair, RandomGenerator<Double> randomGenerator) {
        if (mutationProbability < 0.0D) {
            throw new JMetalException("Mutation probability is negative: " + mutationProbability);
        } else if (distributionIndex < 0.0D) {
            throw new JMetalException("Distribution index is negative: " + distributionIndex);
        } else {
            this.mutationProbability = mutationProbability;
            this.distributionIndex = distributionIndex;
            this.solutionRepair = solutionRepair;
            this.randomGenerator = randomGenerator;
        }
    }

    public double getMutationProbability() {
        return this.mutationProbability;
    }

    public double getDistributionIndex() {
        return this.distributionIndex;
    }

    public void setDistributionIndex(double distributionIndex) {
        this.distributionIndex = distributionIndex;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public IntegerSolution execute(IntegerSolution solution) throws JMetalException {
        if (null == solution) {
            throw new JMetalException("Null parameter");
        } else {
            this.doMutation(this.mutationProbability, solution);
            return solution;
        }
    }

    private void doMutation(double probability, IntegerSolution solution) {
        for(int i = 0; i < solution.getNumberOfVariables(); ++i) {
            if ((Double)this.randomGenerator.getRandomValue() <= probability) {
                double y = (double)(Integer)solution.getVariableValue(i);
                double yl = (double)solution.getLowerBound(i);
                double yu = (double)solution.getUpperBound(i);
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

                solution.setVariableValue(i, (int)y);
            }
        }

    }
}

