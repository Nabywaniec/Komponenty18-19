package mTSP;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class Crossover implements CrossoverOperator<IntegerSolution> {


    private static final double EPS = 1.0E-14D;
    private double distributionIndex;
    private double crossoverProbability;
    private RandomGenerator<Double> randomGenerator;

    public Crossover(double crossoverProbability, double distributionIndex) {
        this(crossoverProbability, distributionIndex, () -> {
            return JMetalRandom.getInstance().nextDouble();
        });
    }

    public Crossover(double crossoverProbability, double distributionIndex, RandomGenerator<Double> randomGenerator) {
        if (crossoverProbability < 0.0D) {
            throw new JMetalException("Crossover probability is negative: " + crossoverProbability);
        } else if (distributionIndex < 0.0D) {
            throw new JMetalException("Distribution index is negative: " + distributionIndex);
        } else {
            this.crossoverProbability = crossoverProbability;
            this.distributionIndex = distributionIndex;
            this.randomGenerator = randomGenerator;
        }
    }

    public double getCrossoverProbability() {
        return this.crossoverProbability;
    }

    public double getDistributionIndex() {
        return this.distributionIndex;
    }

    public void setDistributionIndex(double distributionIndex) {
        this.distributionIndex = distributionIndex;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

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
        int valueX1 = (Integer)parent1.getVariableValue(i);
        int valueX2 = (Integer)parent2.getVariableValue(i);

        if ((Double)this.randomGenerator.getRandomValue() <= 0.5D) {
            if ((double)Math.abs(valueX1 - valueX2) > 1.0E-14D) {
                double y1;
                double y2;
                if (valueX1 < valueX2) {
                    y1 = (double)valueX1;
                    y2 = (double)valueX2;
                } else {
                    y1 = (double)valueX2;
                    y2 = (double)valueX1;
                }

                double yL = (double)parent1.getLowerBound(i);
                double yu = (double)parent1.getUpperBound(i);
                double rand = (Double)this.randomGenerator.getRandomValue();
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

                if ((Double)this.randomGenerator.getRandomValue() <= 0.4D) {
                    ((IntegerSolution)offspring.get(0)).setVariableValue(i, (int)c2);
                    ((IntegerSolution)offspring.get(1)).setVariableValue(i, (int)c1);
                } else {
                    ((IntegerSolution)offspring.get(0)).setVariableValue(i, (int)c1);
                    ((IntegerSolution)offspring.get(1)).setVariableValue(i, (int)c2);
                }
            } else {
                ((IntegerSolution)offspring.get(0)).setVariableValue(i, valueX1);
                ((IntegerSolution)offspring.get(1)).setVariableValue(i, valueX2);
            }
        }
    }

    public List<IntegerSolution> doCrossover(double probability, IntegerSolution parent1, IntegerSolution parent2) {
        List<IntegerSolution> offspring = new ArrayList(2);
        offspring.add((IntegerSolution)parent1.copy());
        offspring.add((IntegerSolution)parent2.copy());
        if ((Double)this.randomGenerator.getRandomValue() <= probability) {
            for(int i = 0; i < parent1.getNumberOfVariables()-1; i+=2) {
                if((Double) this.randomGenerator.getRandomValue() < 0.3D){
                    cross(i,offspring,parent1,parent2);
                    cross(i+1,offspring,parent1,parent2);
                    break;
                }
                else if((Double) this.randomGenerator.getRandomValue() <= 0.5){
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

    public int getNumberOfRequiredParents() {
        return 2;
    }

    public int getNumberOfGeneratedChildren() {
        return 2;
    }
}
