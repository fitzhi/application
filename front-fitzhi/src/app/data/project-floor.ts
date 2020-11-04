export class ProjectFloor {

    /**
     * The project identifier.
     */
    public idProject: number;

    /**
     * The year of this floor.
     */
    private year: number;

    /**
     * The week of this floor.
     */
    private week: number;
    
    /**
     * Number of lines developed by ACTIVE developers.
     */
    public linesActiveDevelopers: number;

    /**
     * Number of lines developed by INACTIVE developers.
     */
    public linesInactiveDevelopers: number;

}
