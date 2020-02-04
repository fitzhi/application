import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { BaseComponent } from '../../base/base.component';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../../constants';
import { BackendSetupService } from '../../service/backend-setup/backend-setup.service';
import { MessageService } from 'src/app/message/message.service';
import { take } from 'rxjs/operators';

@Component({
	selector: 'app-backend-setup',
	templateUrl: './backend-setup.component.html',
	styleUrls: ['./backend-setup.component.css']
})
export class BackendSetupComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
     * We'll send to the parent component (startingSetup) the fact that this is the very first connection.
     */
	@Output() messengerVeryFirstConnection = new EventEmitter<boolean>();

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

	/**
     * This boolean is equal to <code>true</code> if we are in the very fist call to Wibkac.
     * Specific setup forms should be filled to complete this startup procedure.
     */
	veryFirstConnection = false;

	public backendSetupForm = new FormGroup({
		url: new FormControl('', [Validators.maxLength(16)])
	});


	constructor(
		private httpClient: HttpClient,
		private messageService: MessageService,
		private backendSetupService: BackendSetupService) { super(); }

	ngOnInit() {
		console.log(this.backendSetupService.hasSavedAnUrl());
		this.backendSetupForm.get('url').setValue(
			this.backendSetupService.hasSavedAnUrl() ?
				this.backendSetupService.url() : this.backendSetupService.defaultUrl);
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
		this.backendSetupService.isVeryFirstConnection(urlCandidate)
			.pipe(take(1))
			.subscribe(
				data => {
					this.veryFirstConnection = (data === 'true');
					if (Constants.DEBUG && this.veryFirstConnection) {
						console.log('This is the very first connection into Wibkac');
					}
					this.currentState = this.BUTTON_VALID_URL;
					this.messageService.info('This URL is valid. Let\'s go ahead !');
					this.backendSetupService.saveUrl(urlCandidate);
					this.messengerVeryFirstConnection.emit(this.veryFirstConnection);
				},
				error => {
					if (Constants.DEBUG) {
						console.log('Connection error', error);
					}
					this.currentState = this.BUTTON_INVALID_URL;
					setTimeout(() => this.messageService.error(
						'Error ! Either this URL is invalid, or your server is offline'), 0);
				});
	}

	/**
     * Called when the end-user edit the content of the url field.
     */
	urlInEdition() {
		this.currentState = this.BUTTON_IN_EDITION;
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
     * Calling the base class to unsubscribe all subscriptions.
     */
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
