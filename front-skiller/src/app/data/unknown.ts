export class Unknown {

  /**
   * Pseudo of an unknown developer.
   */
  public pseudo: string;

  /**
   * Staff's idendifier related to this pseudo.
   */
  public idStaff: number;

  /**
   * Staff's full name related to this pseudo.
   */
  public fullName: string;

  /**
   * Developer's login elated to this pseudo.
   */
  public login: string;

  /**
   * Is this a pseudo for technical technical or a real end user ?
   */
  public technical: boolean;

  /**
   * Type of operation executed on the back-end.
   */
  public action: string;

  /**
   * Constructor.
   */
  constructor() {
  }
}
