export class TopicEvaluation {

	/**
	* @param idTopic the topic concerned
	* @param value the evaluation given
	* @param typeOfOperation the type of operation
	* - INPUT_BROADCAST = 1;
	* - CHANGE_BROADCAST = 2;
	*/
	constructor (
		public idTopic: number,
		public value: number,
		public typeOfOperation:  1 | 2) {}
}
