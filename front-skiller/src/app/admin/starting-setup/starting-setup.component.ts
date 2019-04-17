import { Component, OnInit, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material';
import { Constants } from 'src/app/constants';
import { Collaborator } from 'src/app/data/collaborator';

@Component({
    selector: 'app-starting-setup',
    templateUrl: './starting-setup.component.html',
    styleUrls: ['./starting-setup.component.css']
})
export class StartingSetupComponent implements OnInit {

    /**
     * The main stepper is passed in order to procede a programmatly step.next().
     */
    @ViewChild('stepper') stepper: MatStepper;

    /**
     * Are we in the very first connection ?
     */
    veryFirstConnection = false;

    /**
     * Array representing the fact that each step has been completed.
     */
    completed: Array<boolean> = [false, false, false];

    /**
     * Current staff identifier.
     */
    idStaff: number;

    /**
     * Current staff.
     */
    staff: Collaborator;

    constructor() { }

    ngOnInit() {
    }

    /**
     * Setup the fact that this is the very first connection.
     */
    setVeryFirstConnection($event: boolean) {
        if (Constants.DEBUG) {
            console.log ('veryFirstConnecion :', $event);
        }
        this.veryFirstConnection = $event;
        this.completed[0] = true;
        setTimeout(() => {
            this.stepper.next();
        }, 0);
    }

    /**
     * Catch the staff identifier created the registerUserComponent.
     */
    setRegisteredUser($event: number) {
        if (Constants.DEBUG) {
            console.log ('idStaff created :', $event);
        }
        this.completed[1] = true;
        this.idStaff = $event;
        setTimeout(() => {
            this.stepper.next();
        }, 0);
    }

    /**
     * Catch the staff identifier created the registerUserComponent.
     */
    setStaffUpdatedForUser($event: Collaborator) {
        if (Constants.DEBUG) {
            console.log ('staff updated for :', $event.lastName);
        }
        this.completed[2] = true;
        this.staff = $event;
        setTimeout(() => {
            this.stepper.next();
        }, 0);
    }
}
