export class Attribution {

  project_id: number;
  /**
   * Starting date of the collaborater's attribution
   */
  from_date: Date = new Date();
  /**
   * Ending date of the collaborater's attribution
   */
  to_date: Date = new Date();

  /**
   * Constructor
   */
  constructor(id: number) {
    this.project_id = id;
  }
}
