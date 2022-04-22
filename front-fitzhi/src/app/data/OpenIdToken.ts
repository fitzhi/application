
class Origin {
    constructor(
        public access_token: string,
        public token_type?: string,
        public scope?: string ) {}
}

export class OpenIdToken {
    constructor(
        public origin?: Origin) {}
}