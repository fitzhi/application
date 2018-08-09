import {Injectable} from '@angular/core';
import {Collaborator} from './data/collaborator';
import {Skill} from './data/skill';
import {Project} from './data/project';
import {Constants} from './constants';
import {MOCK_COLLABORATORS} from './mock/mock-collaborators';
import {Subject, Observable, of} from 'rxjs';
import {catchError, map, tap, filter} from 'rxjs/operators';

import {CollaboratorService} from './collaborator.service';
import {SkillService} from './skill.service';
import {ProjectService} from './project.service';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  /**
   * List of collaborators corresponding to the search criteria.
   */
  private static theStaff: Collaborator[] = [];

  /**
   * List of skills corresponding to the search criteria.
   */
  private static theSkills: Skill[] = [];

  /**
   * List of projects corresponding to the search criteria.
   */
  private static theProjects: Project[] = [];

  /**
   * Current collaborator's identifier previewed on the fom.
   */
  private emitActualCollaboratorDisplay = new Subject<number>();

  /**
   * Observable associated with the current collaborator.
   */
  newCollaboratorDisplayEmitted$ = this.emitActualCollaboratorDisplay.asObservable();

  /**
   * Construction.
   */
  constructor(
    private collaboratorService: CollaboratorService,
    private skillService: SkillService,
    private projectService: ProjectService) {
  }

  /**
	* Reload the collaborators for the passed criteria.
	*/
  reloadCollaborators(myCriteria: string) {

    function testCriteria(collab, index, array) {
      const firstname = (typeof collab.firstName !== 'undefined') ? collab.firstName : '';
      const lastname = (typeof collab.firstName !== 'undefined') ? collab.firstName : '';
      return ((firstname.toLowerCase().indexOf(myCriteria) > -1)
        || (lastname.toLowerCase().indexOf(myCriteria) > -1));
    }

    this.cleanUpCollaborators();
    this.collaboratorService.getAll().
      subscribe((staff: Collaborator[]) =>
        DataService.theStaff.push(...staff.filter(testCriteria)),
      error => console.log(error),
      () => {
        if (Constants.DEBUG) {
          console.log('the staff collection is containing now ' + DataService.theStaff.length + ' records');
        }
      }
      );
  }

  /**
   * Cleanup the list of collaborators involved in our service center.
   */
  cleanUpCollaborators() {
    if (Constants.DEBUG) {
      if (DataService.theStaff == null) {
        console.log('INTERNAL ERROR : collection theStaff SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the staff collection containing ' + DataService.theStaff.length + ' records');
      }
    }
    DataService.theStaff.length = 0;
  }

  /**
   * Return the collaborator associated with this id.
   */
  getCollaborator(id: number): Observable<Collaborator> {

    let foundCollab: Collaborator = null;
    foundCollab = DataService.theStaff.find(collab => collab.id === id);

    if (typeof foundCollab !== 'undefined') {
      this.emitActualCollaboratorDisplay.next(id);
      // We create an observable for an element of the cache in order to be consistent with the direct reading.
      return of(foundCollab);
    } else {
      // The collaborator's id is not, or no more, available in the cache
      // We try a direct access
      if (Constants.DEBUG) {
        console.log('Direct access for : ' + id);
      }
      return this.collaboratorService.get(id).pipe(tap(
        (collab: Collaborator) => {
          if (Constants.DEBUG) {
            console.log('Direct access for : ' + id);
            if (typeof collab !== 'undefined') {
              console.log('Collaborator found : ' + collab.firstName + ' ' + collab.lastName);
            } else {
              console.log('No staff found for id ' + id);
            }
          }
        }));
    }
  }

  /**
   * Return the NEXT collaborator's id associated with this id in the staff list.
   */
  nextCollaboratorId(id: number): number {
    const index = DataService.theStaff.findIndex(collab => collab.id === id);
    if (Constants.DEBUG) {
      console.log('Current index : ' + index);
      console.log('Staff size : ' + DataService.theStaff.length);
    }
    if (index < DataService.theStaff.length - 1) {
      return DataService.theStaff[index + 1].id;
    } else {
      return undefined;
    }
  }

  /**
   * Return the PREVIOUS collaborator's id associated with this id in the staff list.
   */
  previousCollaboratorId(id: number): number {
    const index = DataService.theStaff.findIndex(collab => collab.id === id);
    if (index > 0) {
      return DataService.theStaff[index - 1].id;
    } else {
      return undefined;
    }
  }

  /**
   * Return the list of staff membersÒ.
   */
  getStaff(): Collaborator[] {
    return DataService.theStaff;
  }

  /**
   * Saving a new or an updated collaborator
   */
  saveCollaborator(collaborator: Collaborator): Observable<Collaborator> {
    return this.collaboratorService.save(collaborator);
  }

  /**
   * Saving a new or an updated skill
   */
  saveSkill(skill: Skill) {
    this.skillService.save(skill);
  }

  /**
   * Return the list of staff membersÒ.
   */
  getSkills(): Skill[] {
    return DataService.theSkills;
  }

  /**
  * Reload the collaborators for the passed criteria.
  */
  reloadSkills(myCriteria: string) {

    function testCriteria(skill, index, array) {
      return (skill.title.toLowerCase().indexOf(myCriteria) > -1);
    }

    this.cleanUpSkills();
    this.skillService.getAll().
      subscribe((skills: Skill[]) =>
        DataService.theSkills.push(...skills.filter(testCriteria)),
      // console.log (skills[0].title.toLowerCase().indexOf(myCriteria) ),
      // DataService.theSkills.push(...skills.filter(testCriteria)),
      error => console.log(error),
      () => {
        if (Constants.DEBUG) {
          console.log('the skills collection is containing now ' + DataService.theSkills.length + ' records');
        }
      }
      );
  }

  /**
   * Cleanup the list of skills involved in our service center.
   */
  cleanUpSkills() {
    if (Constants.DEBUG) {
      if (DataService.theSkills == null) {
        console.log('INTERNAL ERROR : collection theSkill SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the skill collection containing ' + DataService.theSkills.length + ' records');
      }
    }
    DataService.theSkills.length = 0;
  }

  /**
   * Return the skill associated with this id.
   */
  getSkill(id: number): Observable<Skill> {

    let foundSkill: Skill = null;
    foundSkill = DataService.theSkills.find(skill => skill.id === id);

    if (typeof foundSkill !== 'undefined') {
      //TODO this.emitActualCollaboratorDisplay.next(id);
      // We create an observable for an element of the cache in order to be consistent with the direct reading.
      return of(foundSkill);
    } else {
      // The collaborator's id is not, or no more, available in the cache
      // We try a direct access
      if (Constants.DEBUG) {
        console.log('Direct access for : ' + id);
      }
      return this.skillService.get(id).pipe(tap(
        (skill: Skill) => {
          if (Constants.DEBUG) {
            console.log('Direct access for : ' + id);
            if (typeof skill !== 'undefined') {
              console.log('Skill found : ' + skill.title);
            } else {
              console.log('No skill found for id ' + id);
            }
          }
        }));
    }
  }


  /**
  * Reload the projects for the passed criteria.
  */
  reloadProjects(myCriteria: string) {

    function testCriteria(project, index, array) {
      return (myCriteria == null) ?
        true : (project.name.toLowerCase().indexOf(myCriteria.toLowerCase()) > -1);
    }

    this.cleanUpProjects();
    this.projectService.getAll().
      subscribe((projects: Project[]) =>
        DataService.theProjects.push(...projects.filter(testCriteria)),
        error => console.log(error),
      () => {
        if (Constants.DEBUG) {
          console.log('the projects collection is containing now ' + DataService.theProjects.length + ' records');
        }
      }
      );
  }

  /**
   * Cleanup the list of projects formerly loaded on the browser.
   */
  cleanUpProjects() {
    if (Constants.DEBUG) {
      if (DataService.theSkills == null) {
        console.log('INTERNAL ERROR : collection theProjects SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the projects collection containing ' + DataService.theProjects.length + ' records');
      }
    }
    DataService.theProjects.length = 0;
  }


  /**
  * Return the list of projects.
  */
  getProjects(): Project[] {
    return DataService.theProjects;
  }

  /**
   * Saving a new or an updated project.
   */
  saveProject(project: Project): Observable<Project> {
    return this.projectService.save (project);
  }

  /**
   * Return the project associated with this id.
   */
  getProject(id: number): Observable<Project> {

    let foundProject: Project = null;
    foundProject = DataService.theProjects.find(project => project.id === id);

    if (typeof foundProject !== 'undefined') {
      return of(foundProject);
    } else {
      // The collaborator's id is not, or no more, available in the cache.
      // We try a direct access
      if (Constants.DEBUG) {
        console.log('Direct access for : ' + id);
      }
      return this.projectService.get(id).pipe(tap(
        (project: Project) => {
          if (Constants.DEBUG) {
            console.log('Direct access for : ' + id);
            if (typeof project !== 'undefined') {
              console.log('Project found : ' + project.name);
            } else {
              console.log('No project found for id ' + id);
            }
          }
        }));
    }
  }

}

