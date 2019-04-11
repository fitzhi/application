import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { BaseComponent } from '../../base/base.component';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../../constants';

@Component({
    selector: 'app-backend-setup',
    templateUrl: './backend-setup.component.html',
    styleUrls: ['./backend-setup.component.css']
})
export class BackendSetupComponent extends BaseComponent implements OnInit, OnDestroy {

    /**
     * Button states : Edition, Selected, Ok, Error
     */
    currentState = 1;

    /**
     * 3 of the 4 states, The fourth (button activated) is handled in CSS file with the :hover event
     */
    BUTTON_IN_EDITION = 1;
    BUTTON_VALID_URL = 2;
    BUTTON_INVALID_URL = 3;

    messageValidationUrl = '';

    public backendSetupForm = new FormGroup({
        url: new FormControl('', [Validators.maxLength(16)])
    });

    private defaultUrl = 'http://localhost:8080';

    constructor(private httpClient: HttpClient) { super(); }

    ngOnInit() {
        this.backendSetupForm.get('url').setValue(this.defaultUrl);
    }

    get url(): any {
        return this.backendSetupForm.get('url');
    }

    /**
     * Test and save the URL.
     */
    onSubmit() {
        const urlCandidate = this.backendSetupForm.get('url').value;
        if (Constants.DEBUG) {
            console.log('Testing the URL', urlCandidate);
        }
        this.subscriptions
            .add(this.httpClient.get<String>(urlCandidate + '/ping', { responseType: 'text' as 'json' })
                .subscribe(
                    () => {
                        this.currentState = this.BUTTON_VALID_URL;
                        this.messageValidationUrl = 'This URL is valid. Let\'s go ahead !';
                    },
                    () => {
                        this.currentState = this.BUTTON_INVALID_URL;
                        this.messageValidationUrl = 'Error ! Either this URL is invalid, or your server is offline';
                    }));
    }

    /**
     * Called when the end-user edit the content of the url field.
     */
    urlInEdition() {
        this.currentState = this.BUTTON_IN_EDITION;
        this.messageValidationUrl = '';
    }

    /**
     * Class of the button corresponding to the  4 states : Edition, Selected, Ok, Error
     */
    classButton() {
        let classButton = '';
        switch (this.currentState) {
            case this.BUTTON_IN_EDITION:
                classButton = 'urlEdition';
                break;
            case this.BUTTON_VALID_URL:
                classButton = 'urlValid';
                break;
            case this.BUTTON_INVALID_URL:
                classButton = 'urlInvalid';
                break;
        }
        return classButton;
    }

    /**
     * Class of the label corresponding to the 2 states : Ok : green, Error : red
     */
    classLabel() {
        let classLabel = '';
        switch (this.currentState) {
            case this.BUTTON_VALID_URL:
                classLabel = 'validMessage';
                break;
            case this.BUTTON_INVALID_URL:
                classLabel = 'invalidMessage';
                break;
        }
        return classLabel;
    }

    /**
     * Calling the base class to unsubscribe all subscriptions.
     */
    ngOnDestroy() {
        super.ngOnDestroy();
    }

}
