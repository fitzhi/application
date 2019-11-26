export class TopicWeight {

	/**
	* @param idTopic the topic concerned
	* @param value the __weight__ given
	* @param typeOfOperation the type of operation
	* - INPUT_BROADCAST = 1;
	* - CHANGE_BROADCAST = 2;
	*/
	constructor (
		public idTopic: number,
		public value: number,
		public typeOfOperation:  1 | 2) {}
}
