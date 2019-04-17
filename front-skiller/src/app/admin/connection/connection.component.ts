import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { BaseComponent } from '../../base/base.component';
import { MatStepper } from '@angular/material';
import { MustMatch } from 'src/app/service/mustmatch';

@Component({
    selector: 'app-connection',
    templateUrl: './connection.component.html',
    styleUrls: ['./connection.component.css']
})
export class ConnectionComponent extends BaseComponent implements OnInit, OnDestroy {

    /**
     * The main stepper is passed in order to procede a programmatly step.next().
     */
    @Input('stepper')
    stepper: MatStepper;

    /**
     * Is this ever the first connection to this server, assuming that the user has to be "administrator" ?
     */
    @Input('veryFirstConnection')
    veryFirstConnection: boolean;

    /**
     * Are supposed to change the password on this form ? is it simply a connection form ?
     */
    @Input('alterPassword')
    alterPassword: boolean;

    public connectionGroup: FormGroup;

    constructor(private formBuilder: FormBuilder) { super(); }

    ngOnInit() {
        this.connectionGroup = this.formBuilder.group({
            username: new FormControl('', [Validators.required, Validators.maxLength(16)]),
            password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)]),
            passwordConfirmation: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)]),
        }, {
            validator: MustMatch('password', 'passwordConfirmation')
        });
    }

    /**
     * Class of the button corresponding to the 3 possible states of the "Ok" button.
     */
    classOkButton() {
        return (this.connectionGroup.invalid) ?
            'okButtonInvalid' : 'okButtonValid';
    }

    get username(): any {
        return this.connectionGroup.get('username');
    }

    get password(): any {
        return this.connectionGroup.get('password');
    }

    get passwordConfirmation(): any {
        return this.connectionGroup.get('passwordConfirmation');
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
        console.log('onSubmit');
    }

}
