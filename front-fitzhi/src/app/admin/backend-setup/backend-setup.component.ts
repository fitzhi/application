import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { take } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { environment } from '../../../environments/environment';
import { BaseDirective } from '../../base/base-directive.directive';
import { BackendSetupService } from '../../service/backend-setup/backend-setup.service';

@Component({
	selector: 'app-backend-setup',
	templateUrl: './backend-setup.component.html',
	styleUrls: ['./backend-setup.component.css']
})
export class BackendSetupComponent extends BaseDirective implements OnInit, OnDestroy {

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
     * This boolean is equal to <code>true</code> if we are in the very fist call to fitzhì.
     * Specific setup forms should be filled to complete this startup procedure.
     */
	veryFirstConnection = false;

	public environment = environment;

	public backendSetupForm = new FormGroup({
		url: new FormControl('', [Validators.maxLength(16)])
	});


	constructor(
		private messageService: MessageService,
		private backendSetupService: BackendSetupService) { super(); }

	ngOnInit() {
		console.log (`starting release ${environment.version}`);
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
		if (traceOn()) {
			console.log('Testing the URL', urlCandidate);
		}
		this.backendSetupService.isVeryFirstConnection$(urlCandidate)
			.pipe(take(1))
			.subscribe({
				next:
					data => {
						if (traceOn() && this.veryFirstConnection) {
							console.log('This is the very first connection into fitzhì');
						}

						this.veryFirstConnection = data.connected;
						this.backendSetupService.saveUrl(urlCandidate);
						this.currentState = this.BUTTON_VALID_URL;
						this.messengerVeryFirstConnection.emit(this.veryFirstConnection);

						this.messageService.info('This URL is valid. Let\'s go ahead !');
					},
				error: error => {
					if (traceOn()) {
						console.log('Connection error', error);
					}
					this.currentState = this.BUTTON_INVALID_URL;
					this.messageService.error('Error ! Either this URL is invalid, or your server is offline');
				}
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
