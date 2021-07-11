import { EvaluationDistribution } from "./EvalutionDistribution";

/**
 * This class is hosting all Fitzhi settings.
 */
export class FitzhiSettings {

	public evaluationDistributions: EvaluationDistribution[] = [ 
		new EvaluationDistribution(100, undefined, undefined),
		new EvaluationDistribution(undefined, undefined, 100),
		new EvaluationDistribution(undefined, 100, undefined),
		new EvaluationDistribution(70, 30, undefined),
		new EvaluationDistribution(20, undefined, 80),
		new EvaluationDistribution(20, 20, 60),
	]
}