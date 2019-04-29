import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { AuthService } from '../service/auth/auth.service';

@Component({
    selector: 'app-connect-user',
    templateUrl: './connect-user.component.html',
    styleUrls: ['./connect-user.component.css']
})
export class ConnectUserComponent implements OnInit {

    /**
     * Group of the components present in the form.
     */
    public connectionGroup: FormGroup;

    constructor(
        private authService: AuthService,
        private formBuilder: FormBuilder) {
        this.connectionGroup = this.formBuilder.group({
            username: new FormControl('', [Validators.required, Validators.maxLength(16)]),
            password: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16)])
        });
    }

    ngOnInit() {
    }

    /**
     * Class of the button corresponding to the 3 possible states of the "Ok" button.
     */
    classOkButton() {
        return (this.connectionGroup.invalid) ?
            'okButton okButtonInvalid' : 'okButton okButtonValid';
    }

    /**
     * Cancel the installation
     */
    onCancel() {
        console.log('onCancel');
    }

    /**
     * Cancel the installation
     */
    onSubmit() {
        const username: string = this.connectionGroup.get('username').value;
        const password: string = this.connectionGroup.get('password').value;
        this.authService.connect(username, password);
    }

    get username(): any {
        return this.connectionGroup.get('username');
    }

    get password(): any {
        return this.connectionGroup.get('password');
    }

}
