import {Injectable} from '@angular/core';
import {Collaborator} from './data/collaborator';
import {Skill} from './data/skill';
import {Project} from './data/project';
import {Constants} from './constants';
import {MOCK_COLLABORATORS} from './mock/mock-collaborators';
import {Subject, Observable, of} from 'rxjs';
import {catchError, map, tap, filter} from 'rxjs/operators';

import {StaffService} from './staff.service';
import {SkillService} from './skill.service';
import {ProjectService} from './project.service';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  /**
   * Current collaborator's identifier previewed on the form.
   */
  public emitActualCollaboratorDisplay = new Subject<number>();

  /**
   * Observable associated with the current collaborator.
   */
  newCollaboratorDisplayEmitted$ = this.emitActualCollaboratorDisplay.asObservable();

  /**
   * Construction.
   */
  constructor(
    private collaboratorService: StaffService,
    private skillService: SkillService,
    private projectService: ProjectService) {
  }



}

