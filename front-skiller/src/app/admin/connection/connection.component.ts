import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { BaseComponent } from '../../base/base.component';

@Component({
    selector: 'app-connection',
    templateUrl: './connection.component.html',
    styleUrls: ['./connection.component.css']
})
export class ConnectionComponent extends BaseComponent implements OnInit, OnDestroy {

    public connectionGroup = new FormGroup({
        username: new FormControl('', [Validators.maxLength(8)]),
        password: new FormControl('', [Validators.maxLength(16)])
    });

    constructor() { super(); }

    ngOnInit() {
    }

    get username(): any {
        return this.connectionGroup.get('username');
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
