import { AttachmentFile } from './AttachmentFile';

export class AuditTopic {

	/**
	 * Public constuction.
	 * @param idTopic the topic identifier
	 * @param evaluation the evaluation given for this subject.
	 * @param weight the weight of this topic inside the global evaluation.
	 * @param report the short text which summarized an opinion on this particular topic
	 * @param attachmentList the list of audit attachment files linked to this topic
	 */
	constructor(
		public idTopic: number,
		public evaluation: number,
		public weight: number,
		public report: string = '',
		public attachmentList: AttachmentFile[] = []) {}
}
