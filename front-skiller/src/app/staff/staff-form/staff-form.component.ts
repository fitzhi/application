import {Constants} from '../../constants';
import {Collaborator} from '../../data/collaborator';
import {MessageService} from '../../message/message.service';
import {StaffService} from '../../service/staff.service';
import {CinematicService} from '../../service/cinematic.service';
import {Profile} from '../../data/profile';
import {ReferentialService} from '../../service/referential.service';
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
    active: new FormControl(1),
    external: new FormControl(0)
  });

  constructor(
    private staffService: StaffService,
    private messageService: MessageService,
    private cinematicService: CinematicService,
    private referentialService: ReferentialService,
    private staffDataExchangeService: StaffDataExchangeService,
    private router: Router) {}

  ngOnInit() {

    if (Constants.DEBUG) {
      console.log ('Current staff member id ' + this.idStaff);
    }
    if (this.idStaff === null) {
        this.collaborator = { idStaff: -1, firstName: '', lastName: '', nickName: '', login: '', email: '', level: '',
        dateInactive: null,  application: '', typeOfApplication: null, isActive: true, external: true,
        experiences: [], missions: [] };
        this.profileStaff.get('firstName').setValue('');
        this.profileStaff.get('lastName').setValue('');
        this.profileStaff.get('nickName').setValue('');
        this.profileStaff.get('login').setValue('');
        this.profileStaff.get('email').setValue('');
        this.profileStaff.get('profile').setValue('');
        this.profileStaff.get('active').setValue(true);
        this.label_isActive = 'will be considered in activity as long as this box is checked ';
        this.profileStaff.get('external').setValue(false);
    } else {
      /**
       * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
       */
      this.staffDataExchangeService.collaboratorObserver
        .subscribe((employee: Collaborator) => {
          if (Constants.DEBUG) {
            console.log('Employee loaded ' + employee.idStaff);
          }
          this.collaborator = employee;
          this.profileStaff.get('firstName').setValue(this.collaborator.firstName);
          this.profileStaff.get('lastName').setValue(this.collaborator.lastName);
          this.profileStaff.get('nickName').setValue(this.collaborator.nickName);
          this.profileStaff.get('login').setValue(this.collaborator.login);
          this.profileStaff.get('email').setValue(this.collaborator.email);
          this.profileStaff.get('profile').setValue(this.collaborator.level);
          this.profileStaff.get('active').setValue(this.collaborator.isActive);
          if (this.collaborator.isActive) {
            if (this.collaborator.idStaff === null) {
              this.label_isActive = 'will be considered in activity as long as this box is checked ';
            } else {
              this.label_isActive = this.collaborator.firstName + ' ' + this.collaborator.lastName
              + ' is still in activity. Uncheck this box to inform of his leave.';
            }
            // There is no READONLY attribute in the SELECT widget.
            // We need to enable this field within the code and not in HTML like the rest of the form.
            this.profileStaff.get('profile').enable();
            this.profileStaff.get('external').enable();
          } else {
            this.label_isActive = this.collaborator.firstName + ' ' +
            this.collaborator.lastName + ' in no more in activity since ';
            this.label_dateInactive = this.collaborator.dateInactive;
            // There is no READONLY attribute in the SELECT widget.
            // We need to disable this field within the code and not in HTML like the rest of the form.
            this.profileStaff.get('profile').disable();
            this.profileStaff.get('external').disable();
          }
          this.profileStaff.get('external').setValue(this.collaborator.external);
          this.cinematicService.setForm(Constants.DEVELOPERS_CRUD, this.router.url);
      });
    }
    this.referentialService.subjectProfiles.subscribe(
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
    this.collaborator.external = this.profileStaff.get('external').value;

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
    return (!this.collaborator.isActive);
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

  get external(): any {
    return this.profileStaff.get('external');
  }
}
