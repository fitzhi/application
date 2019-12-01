import { AuditDetail } from 'src/app/data/audit-detail';

/**
 * This class is used to propagate the detail panel chosen by the end-user
 * on the `AuditBadgeComponent`.
 */

export class AuditChosenDetail {
	constructor(
		public idTopic: number,
		public detail: AuditDetail) {}

	/**
	 * Returns `true` if this object is equal to the given auditDetail, `false` otherwiser.
	 * @param auditDetail the passed auditDetail
	 */
	public deepEqual(auditDetail: AuditChosenDetail): boolean {
		return (this.idTopic === auditDetail.idTopic) && (this.detail === auditDetail.detail);
	}
}
