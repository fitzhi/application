import {Constants} from '../../constants';
import {Collaborator} from '../../data/collaborator';
import {ListStaffService} from '../../list-staff-service/list-staff.service';
import {MessageService} from '../../message.service';
import {StaffService} from '../../staff.service';
import {CinematicService} from '../../cinematic.service';
import {Profile} from '../../data/profile';
import {ReferentialService} from '../../referential.service';
import {StaffDataExchangeService} from '../service/staff-data-exchange.service';

import {Component, OnInit, Input} from '@angular/core';

import {FormGroup, FormControl, Validators} from '@angular/forms';
import { Router } from '@angular/router';


@Component({
  selector: 'app-staff-form',
  templateUrl: './staff-form.component.html',
  styleUrls: ['./staff-form.component.css']
})
export class StaffFormComponent implements OnInit {

  @Input('idStaff')
  private idStaff: number;

  private collaborator: Collaborator;

  /**
   * Label on side of the active check box.
   */
  label_isActive: String;
  label_dateInactive: Date;

  /**
   * list of profiles used on the SELECT field.
   */
  profiles: Profile[];

  public profileStaff = new FormGroup({
    firstName: new FormControl('', [Validators.maxLength(16)]),
    lastName: new FormControl('', [Validators.required, Validators.maxLength(16)]),
    nickName: new FormControl('', [Validators.maxLength(16)]),
    login: new FormControl('', [Validators.required, Validators.maxLength(16)]),
    email: new FormControl('', [Validators.required, Validators.maxLength(32)]),
    profile: new FormControl(null, [Validators.required]),
    active: new FormControl(1)
  });

  constructor(
    private staffService: StaffService,
    private messageService: MessageService,
    private cinematicService: CinematicService,
    private referentialService: ReferentialService,
    private staffDataExchangeService: StaffDataExchangeService,
    private router: Router) {}

  ngOnInit() {

    /**
     * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
     */

    this.staffDataExchangeService.collaboratorObserver
      .subscribe((employee: Collaborator) => {
        this.collaborator = employee;
        this.profileStaff.get('firstName').setValue(this.collaborator.firstName);
        this.profileStaff.get('lastName').setValue(this.collaborator.lastName);
        this.profileStaff.get('nickName').setValue(this.collaborator.nickName);
        this.profileStaff.get('login').setValue(this.collaborator.login);
        this.profileStaff.get('email').setValue(this.collaborator.email);
        this.profileStaff.get('profile').setValue(this.collaborator.level);
        this.profileStaff.get('active').setValue(this.collaborator.isActive);
        if (this.collaborator.isActive) {
          this.label_isActive = this.collaborator.firstName + ' ' +this.collaborator.lastName
          + '\'s still belonging to the company. Uncheck this box to inform of his departure.';
          // There is no READONLY attribute in the SELECT widget.
          // We need to enable this field within the code and not in HTML like the rest of the form.
          this.profileStaff.get('profile').disable();
          this.profileStaff.get('profile').enable();
        } else {
          this.label_isActive = this.collaborator.firstName + ' ' +
          this.collaborator.lastName + ' does not belong anymore to the staff since ';
          this.label_dateInactive = this.collaborator.dateInactive;
          // There is no READONLY attribute in the SELECT widget.
          // We need to disable this field within the code and not in HTML like the rest of the form.
          this.profileStaff.get('profile').disable();
        }
        this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
      });

    this.referentialService.behaviorSubjectProfiles.subscribe(
      (profiles: Profile[]) => this.profiles = profiles);

  }

  /**
  * The Submit Button has been activated
  */
  onSubmit(): void {
    if (Constants.DEBUG) {
      console.log('Saving data for the collaborator below');
      console.log(this.collaborator);
    }
    this.collaborator.firstName = this.profileStaff.get('firstName').value;
    this.collaborator.lastName = this.profileStaff.get('lastName').value;
    this.collaborator.nickName = this.profileStaff.get('nickName').value;
    this.collaborator.login = this.profileStaff.get('login').value;
    this.collaborator.email = this.profileStaff.get('email').value;
    this.collaborator.level = this.profileStaff.get('profile').value;
    this.collaborator.isActive = this.profileStaff.get('active').value;

    this.staffService.save(this.collaborator)
      .subscribe(
      staff => {
        this.collaborator = staff;
        this.messageService.info('Staff member ' + this.collaborator.firstName + ' ' + this.collaborator.lastName + ' saved');
      });
  }

  /**
   * Test if the collaborator has been already deactivated on the database.
   * You can test this state by testing the dateInactive, filled by the back-end during the deactivation process.
   */
  public isAlreadyDeactived(): boolean {
    return (this.collaborator.dateInactive != null);
  }

  get firstName(): any {
    return this.profileStaff.get('firstName');
  }

  get lastName(): any {
    return this.profileStaff.get('lastName');
  }

  get nickName(): any {
    return this.profileStaff.get('nickName');
  }

  get login(): any {
    return this.profileStaff.get('login');
  }

  get profile(): any {
    return this.profileStaff.get('profile');
  }

  get email(): any {
    return this.profileStaff.get('email');
  }

}
