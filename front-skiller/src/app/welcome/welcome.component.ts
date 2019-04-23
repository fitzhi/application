import { Component, OnInit, OnDestroy } from '@angular/core';
import { BackendSetupService } from '../service/backend-setup/backend-setup.service';
import { BaseComponent } from '../base/base.component';
import { Constants } from '../constants';
import { HttpClient } from '@angular/common/http';
import { MessageService } from '../message/message.service';

@Component({
    selector: 'app-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent extends BaseComponent implements OnInit, OnDestroy {

    veryFirstLaunch = true;

    constructor(
        private backendSetupService: BackendSetupService,
        private httpClient: HttpClient,
        private messageService: MessageService) { super(); }

    ngOnInit() {
        if (!this.backendSetupService.hasSavedAnUrl()) {
            this.loadVeryFirstLaunch();
        }
    }

    loadVeryFirstLaunch() {
        this.subscriptions.add(this.httpClient.get<String>(this.backendSetupService.url() + '/admin/isVeryFirstConnection',
            { responseType: 'text' as 'json' }).subscribe(
            (data: string) => {
                this.veryFirstLaunch = (data === 'true');
                if (Constants.DEBUG && this.veryFirstLaunch) {
                    console.log ('This is the very first connection into Wibkac.');
                }
            },
            (error: string) => {
                if (Constants.DEBUG) {
                    console.log ('Connection error ', error);
                }
                this.messageService.error('Error ! Either this URL is invalid, or your server is offline');
            }));
    }

    /**
     * Calling the base class to unsubscribe all subscriptions.
     */
    ngOnDestroy() {
        super.ngOnDestroy();
    }
}
