import { Collaborator } from './collaborator';
import { OpenIdToken } from './OpenIdToken';

/**
 * This object transports the data produced during the registration of a user.
 */
export class OpenIdTokenStaff {
	constructor(
		public openIdToken: OpenIdToken,
		public staff: Collaborator) {
	}
}
