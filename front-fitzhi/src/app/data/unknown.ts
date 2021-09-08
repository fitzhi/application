import { Collaborator } from './collaborator';

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
	 * Staff related if any to this pseudo.
	 */
	public staffRelated: Collaborator;

	/**
	 * The related staff has been saved. The ghost can not modified anymore.
	 */
	public staffRecorded: boolean;

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
	 * Staff's first name related to this pseudo.
	 */
	public firstname: string;

	/**
	 * Staff's first name related to this pseudo.
	 */
	public lastname: string;

	/**
	 * Staff status as active in the company.
	 */
	public active: boolean;

	/**
	 * Staff status as active in the company.
	 */
	public external: boolean;
	
	/**
	 * Date of the first commit.
	 */
	 public firstCommit?: Date;
 
	 /**
	  * Date of the latest commit.
	  */
	 public lastCommit?: Date;
	 
	 /**
	  * @return number of commit submitted by a developer inside the project.
	  */
	 public numberOfCommits?: number;
	 
	 /**
	  * @return number of files modifier by a developer inside the project.
	  */
	 public numberOfFiles?: number;
 
	/**
	 * Constructor.
	 */
	constructor() {
	}
}
