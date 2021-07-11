/**
 * Distribution of the project evaluations to calculate the mean evaluation of the project
 */
export class EvaluationDistribution {

	constructor(
		public staffEvaluationPercentage: number,
		public sonarEvaluationPercentage: number,
		public auditEvaluationPercentage: number) {}
}