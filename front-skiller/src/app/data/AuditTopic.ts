export class AuditTopic {

	/**
	 * Public constuction.
	 * @param idTopic the topic identifier
	 * @param evaluation the evaluation given for this subject.
	 * @param weight the weight of this topic inside the global evaluation.
	 */
	constructor(
		public idTopic: number,
		public evaluation: number,
		public weight: number,
		public report: string = '') {}
}
