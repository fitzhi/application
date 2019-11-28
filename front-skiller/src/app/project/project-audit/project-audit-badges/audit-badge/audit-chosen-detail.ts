import { AuditDetail } from 'src/app/data/audit-detail';

/**
 * This class is used to propagate the detail panel chosen by the end-user
 * on the `AuditBadgeComponent`.
 */

export class AuditChosenDetail {
	constructor(
		public idTopic: number,
		public detail: AuditDetail) {}
}
