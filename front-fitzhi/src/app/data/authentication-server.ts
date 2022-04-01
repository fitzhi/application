import { TypeAuthenticationServer } from "./type-authentication-server";

/**
 * OAUTH authentication server settings like Google or GitHub.
 * This setting is loaded from the backend of Fitzhì.
 */
export class AuthenticationServer {

    /**
     * Public simple construction.
     * @param typeAuthenticationServer category of the authentication server _(like "Google")_
     * @param url the URL to be used for the authentication
     * @param clientId the client ID for the application
     * @param secret the secret generated for the application
     */
    constructor(
        public typeAuthenticationServer: TypeAuthenticationServer,
        public url: string,
        public clientId: string,
        public secret: string,
    ) {}
}