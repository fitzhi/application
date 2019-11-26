export class AuditTopic {

	/**
	 * Public constuction.
	 * @param id the topic identifier
	 * @param evaluation the evaluation given for this subject.
	 * @param weight the weight of this topic inside the global evaluation.
	 */
	constructor(
		public id: number,
		public evaluation: number,
		public weight: number) {}
}
