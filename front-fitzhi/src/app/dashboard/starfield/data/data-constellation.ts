
/**
 * This class contains the data sent by the backend server to generate a constellation in this UI.
 */
export class DataConstellation {

    constructor(
        public idSkill: number, 
        public starsNumber: number, 
        public starsNumberWithExternal: number) {}

}