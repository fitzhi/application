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

    veryFirstConnection = true;

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
    }
}
