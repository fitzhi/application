export class StarfieldMonth {
    constructor (public month: number, public year: number) {}

    /**
     * @returns the string representation of this object.
     */
    toString() {
        return this.month + '/' + this.year;
    }

    /**
     * @returns the first day of month.
     */
    firstDateOfMonth(): Date {
        return new Date(this.year, this.month, 1);
    }
}