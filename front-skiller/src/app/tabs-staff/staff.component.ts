import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Collaborator} from '../data/collaborator';
import {Constants} from '../constants';

import {StaffListService} from '../staff-list-service/staff-list.service';
import { StaffDataExchangeService } from './service/staff-data-exchange.service';
import {CinematicService} from '../service/cinematic.service';
import {MessageService} from '../message/message.service';
import { BaseComponent } from '../base/base.component';

@Component({
  selector: 'app-staff',
  templateUrl: './staff.component.html',
  styleUrls: ['./staff.component.css']
})
export class StaffComponent extends BaseComponent implements OnInit, OnDestroy {

  /**
   * Staff member identifier shared with the child components (staffTabs, StaffForm)
   */
  public idStaff: number;

  private collaborator: Collaborator;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private staffListService: StaffListService,
    private messageService: MessageService,
    private staffDataExchangeService: StaffDataExchangeService,
    private router: Router) {
      super();
    }

  ngOnInit() {
    this.subscriptions.add( this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.idStaff = null;
      } else {
        this.idStaff = + params['id']; // (+) converts string 'id' to a number
      }

      // Either we are in creation mode, or we load the collaborator from the back-end...
      // We create an empty collaborator until the subscription is complete
      this.collaborator = {
        idStaff: null, firstName: null, lastName: null, nickName: null, login: null, email: null, level: null,
        isActive: true, dateInactive: null, application: null, typeOfApplication: null, external: false,
        missions: [], experiences: []
      };
      this.staffDataExchangeService.changeCollaborator (this.collaborator);
      /*
       * By default, you cannot add a project/skill for an unregistered developer.
       */
      document.querySelector('body').style.cssText = '--actions-button-visible: hidden';
      if (this.idStaff != null) {
        this.staffListService.getCollaborator(this.idStaff).subscribe(
          (collab: Collaborator) => {
            this.staffDataExchangeService.changeCollaborator(collab);
            this.collaborator = collab;
            if (collab.isActive) {
              document.querySelector('body').style.cssText = '--actions-button-visible: visible';
            } else {
              document.querySelector('body').style.cssText = '--actions-button-visible: hidden';
            }
            this.cinematicService.emitActualCollaboratorDisplay.next(this.collaborator.idStaff);
            this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
          },
          error => {
            if (error.status === 404) {
              if (Constants.DEBUG) {
                console.log('404 : cannot found a collaborator for the id ' + this.idStaff);
              }
              this.messageService.error('There is no staff member for id ' + this.idStaff);
              this.collaborator = {
                idStaff: null, firstName: null, lastName: null, nickName: null, login: null, email: null, level: null,
                isActive: true, dateInactive: null, application: null, typeOfApplication: null, external: false,
                missions: [], experiences: []
              };
            } else {
              console.error(error.message);
            }
          },
          () => {
            if (this.collaborator.idStaff === 0) {
              console.log('No collaborator found for the id ' + this.idStaff);
            }
            if (Constants.DEBUG) {
              console.log('Loading complete for id ' + this.idStaff);
            }
          }
        );
      }
    }));
    this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
  }

  /**
   * Calling the base class to unsubscribe all subscriptions.
   */
  ngOnDestroy() {
    super.ngOnDestroy();
  }

}


