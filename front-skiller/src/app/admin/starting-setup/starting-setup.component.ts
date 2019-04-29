import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatStepper } from '@angular/material';
import { Constants } from 'src/app/constants';
import { Collaborator } from 'src/app/data/collaborator';
import { BaseComponent } from 'src/app/base/base.component';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { BooleanDTO } from 'src/app/data/external/booleanDTO';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-starting-setup',
    templateUrl: './starting-setup.component.html',
    styleUrls: ['./starting-setup.component.css']
})
export class StartingSetupComponent extends BaseComponent implements OnInit, OnDestroy {

    /**
     * The main stepper is passed in order to procede a programmatly step.next().
     */
    @ViewChild('stepper') stepper: MatStepper;

    /**
     * Are we in the very first connection ?
     */
    public veryFirstConnection = false;

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

    constructor(
        private backendSetupService: BackendSetupService,
        private httpClient: HttpClient) { super(); }

    ngOnInit() {
    }

    /**
     * Setup the fact that this is the very first connection.
     */
    onChangeVeryFirstConnection($event: boolean) {
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
        // Operation has been cancelled.
        if ($event === -1) {
            this.completed[1] = false;
            setTimeout(() => {
                this.stepper.previous();
            }, 0);
            return;
        }
        this.completed[1] = true;
        this.idStaff = $event;
        setTimeout(() => {
            this.stepper.next();
        }, 0);
    }

    /**
     * Catch the complete staff entity, updated by the staff-form.
     */
    setStaffUpdatedForUser($event: Collaborator) {
        if (Constants.DEBUG) {
            console.log ('staff updated for :', $event.lastName);
        }

        this.subscriptions
            .add(this.httpClient.get<BooleanDTO>(this.backendSetupService.url() + '/admin/saveVeryFirstConnection')
                .subscribe(
                    (data: BooleanDTO) => {
                        console.log (data);
                        const veryFirstConnectionIsRegistered = data.result;
                        if (Constants.DEBUG && veryFirstConnectionIsRegistered) {
                            console.log ('The very first connection is registered into Wibkac');
                        }
                        this.completed[2] = true;
                        this.staff = $event;
                        setTimeout(() => {
                            this.stepper.next();
                        }, 0);
                    },
                    (error: BooleanDTO) => {
                        if (Constants.DEBUG) {
                            console.log ('Connection error ', error);
                        }
                    }));
    }


    /**
     * @param $event we move from one step in the installation.
     */
    selectionChange () {
        window.scroll(0, 0);
    }

    /**
     * Calling the base class to unsubscribe all subscriptions.
     */
    ngOnDestroy() {
        super.ngOnDestroy();
    }


}
