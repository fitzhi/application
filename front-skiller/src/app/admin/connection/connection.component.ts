import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { BaseComponent } from '../../base/base.component';

@Component({
    selector: 'app-connection',
    templateUrl: './connection.component.html',
    styleUrls: ['./connection.component.css']
})
export class ConnectionComponent extends BaseComponent implements OnInit, OnDestroy {

    /**
     * Is this ever the first connection to this server, assuming that the user has to be "administrator" ?
     */
    @Input('firstConnection')
    firstConnection: boolean;

    /**
     * Are supposed to change the password on this form ? is it simply a connection form ?
     */
    @Input('alterPassword')
    alterPassword: boolean;

    /**
     * Title of the submit button, which might be either<br/>
     * - "Connect" (in the connection form state)<br/>
     * - "Save" (if we're planning to change the password)
     */
    submitTitle = 'Connect';

    public connectionGroup = new FormGroup({
        username: new FormControl('', [Validators.maxLength(8)]),
        password: new FormControl('', [Validators.maxLength(16)]),
        confirmPassword: new FormControl('', [Validators.maxLength(16)])
    });

    constructor() { super(); }

    ngOnInit() {
        if (this.firstConnection) {
            this.submitTitle = 'Save';
        }
    }

    get username(): any {
        return this.connectionGroup.get('username');
    }

    get password(): any {
        return this.connectionGroup.get('password');
    }

    get confirmPassword(): any {
        return this.connectionGroup.get('confirmPassword');
    }

    /**
     * Calling the base class to unsubscribe all subscriptions.
     */
    ngOnDestroy() {
        super.ngOnDestroy();
    }

    /**
     * Try of connection.
     */
    onSubmit() {
    }
}
