import { AttachmentFile } from './AttachmentFile';

/**
 * A topic is a category of analysis.
 * 
 * It might be
 * - the architecture
 * - the design
 * - the performance
 * - _any other subject_
 * 
 * _This list is customized on the server, and downloaded by the application._
 */
	export class AuditTopic {

	/**
	 * Public constuction of a topic in the audit.
	 * @param idProject the projdct identifier
	 * @param idTopic the topic identifier in the audit scope
	 * @param evaluation the evaluation given for this subject.
	 * @param weight the weight of this topic inside the global evaluation.
	 * @param report the short text which summarized an opinion on this particular topic
	 * @param attachments an array of attachment files associated to this topic
	 */
	constructor(
		public idProject: number,
		public idTopic: number,
		public evaluation: number = 0,
		public weight: number = 5,
		public report: string = '',
		public attachmentList: AttachmentFile[] = []) {}
}
