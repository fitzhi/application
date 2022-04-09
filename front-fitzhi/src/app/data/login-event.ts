import { LoginMode } from "./login-mode";

/**
 * This event is emitted after the authentication.
 */
export class LoginEvent {
    constructor(
        public idStaff: number,
        public loginMode: LoginMode) {}
}