import { Component, OnInit, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material';
import { Constants } from 'src/app/constants';

@Component({
    selector: 'app-starting-setup',
    templateUrl: './starting-setup.component.html',
    styleUrls: ['./starting-setup.component.css']
})
export class StartingSetupComponent implements OnInit {

    @ViewChild('stepper') stepper: MatStepper;

    /**
     * Are we in the very first connection ?
     */
    veryFirstConnection = false;

    /**
     * Array representing the fact that each step has been completed.
     */
    completed: Array<boolean> = [false, false, false];

    constructor() { }

    ngOnInit() {
    }

    /**
     * Setup the fact that this is the very first connection.
     */
    setVeryFirstConnection($event: boolean) {
        if (Constants.DEBUG) {
            console.log ('veryFirstConnecion ?', $event);
        }
        this.veryFirstConnection = $event;
        this.completed[0] = true;
    }
}
