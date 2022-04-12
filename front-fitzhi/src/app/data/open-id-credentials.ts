import { isConstructorDeclaration } from 'typescript';

/**
 * JWT Credentials.
 */
export class OpenIdCredentials {
	constructor(public serverId: string, public jwt: string) { }
}
