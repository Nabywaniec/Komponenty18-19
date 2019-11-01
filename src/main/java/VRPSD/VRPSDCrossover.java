package VRPSD;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.List;

public class VRPSDCrossover implements CrossoverOperator<IntegerSolution> {
    public VRPSDCrossover(double crossoverProbability, double crossoverDistributionIndex) {
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 0;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 0;
    }

    @Override
    public List<IntegerSolution> execute(List<IntegerSolution> integerSolutions) {
        return null;
    }
}
