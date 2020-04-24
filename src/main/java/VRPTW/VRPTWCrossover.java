package VRPTW;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class VRPTWCrossover implements CrossoverOperator<IntegerSolution> {
    private double crossoverProbability;
    private double distributionIndex;
    private RandomGenerator<Double> randomGenerator;

    public VRPTWCrossover(double crossoverProbability, double distributionIndex) {
        this.crossoverProbability = crossoverProbability;
        this.distributionIndex = distributionIndex;
        this.randomGenerator = () -> {
            return JMetalRandom.getInstance().nextDouble();};
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 1;
    }

    @Override
    public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
        if (null == solutions) {
            throw new JMetalException("Null parameter");
        } else if (solutions.size() != 2) {
            throw new JMetalException("There must be two parents instead of " + solutions.size());
        } else {
            return this.doCrossover(this.crossoverProbability, (IntegerSolution)solutions.get(0), (IntegerSolution)solutions.get(1));
        }
    }

    private void cross(int i, List<IntegerSolution> offspring, IntegerSolution parent1, IntegerSolution parent2){
        int valueX1 = parent1.getVariableValue(i);
        int valueX2 = parent2.getVariableValue(i);

        if (this.randomGenerator.getRandomValue() <= 0.5D) {
            if ((double)Math.abs(valueX1 - valueX2) > 1.0E-14D) {
                double y1;
                double y2;
                if (valueX1 < valueX2) {
                    y1 = valueX1;
                    y2 = valueX2;
                } else {
                    y1 = valueX2;
                    y2 = valueX1;
                }

                double yL = (double)parent1.getLowerBound(i);
                double yu = (double)parent1.getUpperBound(i);
                double rand = this.randomGenerator.getRandomValue();
                double beta = 1.0D + 2.0D * (y1 - yL) / (y2 - y1);
                double alpha = 2.0D - Math.pow(beta, -(this.distributionIndex + 1.0D));
                double betaq;
                if (rand <= 1.0D / alpha) {
                    betaq = Math.pow(rand * alpha, 1.0D / (this.distributionIndex + 1.0D));
                } else {
                    betaq = Math.pow(1.0D / (2.0D - rand * alpha), 1.0D / (this.distributionIndex + 1.0D));
                }

                double c1 = 0.5D * (y1 + y2 - betaq * (y2 - y1));
                beta = 1.0D + 2.0D * (yu - y2) / (y2 - y1);
                alpha = 2.0D - Math.pow(beta, -(this.distributionIndex + 1.0D));
                if (rand <= 1.0D / alpha) {
                    betaq = Math.pow(rand * alpha, 1.0D / (this.distributionIndex + 1.0D));
                } else {
                    betaq = Math.pow(1.0D / (2.0D - rand * alpha), 1.0D / (this.distributionIndex + 1.0D));
                }

                double c2 = 0.5D * (y1 + y2 + betaq * (y2 - y1));
                if (c1 < yL) {
                    c1 = yL;
                }

                if (c2 < yL) {
                    c2 = yL;
                }

                if (c1 > yu) {
                    c1 = yu;
                }

                if (c2 > yu) {
                    c2 = yu;
                }

                if (this.randomGenerator.getRandomValue() <= 0.4D) {
                    offspring.get(0).setVariableValue(i, (int)c2);
                    offspring.get(1).setVariableValue(i, (int)c1);
                } else {
                    offspring.get(0).setVariableValue(i, (int)c1);
                    offspring.get(1).setVariableValue(i, (int)c2);
                }
            } else {
                offspring.get(0).setVariableValue(i, valueX1);
                offspring.get(1).setVariableValue(i, valueX2);
            }
        }
    }

    public List<IntegerSolution> doCrossover(double probability, IntegerSolution parent1, IntegerSolution parent2) {
        List<IntegerSolution> offspring = new ArrayList(2);
        offspring.add((IntegerSolution)parent1.copy());
        offspring.add((IntegerSolution)parent2.copy());
        if (this.randomGenerator.getRandomValue() <= probability) {
            for(int i = 0; i < parent1.getNumberOfVariables()-1; i+=2) {
                if(this.randomGenerator.getRandomValue() < 0.3D){
                    cross(i,offspring,parent1,parent2);
                    cross(i+1,offspring,parent1,parent2);
                    break;
                }
                else if(this.randomGenerator.getRandomValue() <= 0.5){
                    if(i<parent1.getNumberOfVariables()-2) {
                        cross(i, offspring, parent1, parent2);
                        cross(i + 1, offspring,parent1,parent2);
                        cross(i+2,offspring,parent1,parent2);
                    }
                }
            }
        }

        return offspring;
    }
}
