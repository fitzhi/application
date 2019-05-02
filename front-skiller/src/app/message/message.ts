/**
 * Class of message
 */
export class Message {
    constructor(public severity: number, public message: string) {
    }

    trace() {
        return this.severity + ' ' + this.message;
    }
}
