import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from './message';
import { Constants } from '../../constants';

@Injectable({
	providedIn: 'root'
})
export class MessageService {

	myMessage: Subject<Message> = new Subject<Message>();

	newMessage$ = this.myMessage.asObservable();

	constructor() {
	}

	/*
	* set a new message
	* @param gravity the gravity level of the message
	* @param message the text of the message
	*/
	public set(gravity: number, message: string) {
		this.myMessage.next(new Message(gravity, message));
		setTimeout(() => this.myMessage.next(new Message(Constants.MESSAGE_VOID, '')), 5000);
	}

	/*
	* Display a new SUCCESS message
	*/
	public success(message: string) {
		this.set(Constants.MESSAGE_SUCCESS, message);
	}

	/*
	* Display a new ERROR message
	*/
	public error(message: string) {
		this.set(Constants.MESSAGE_ERROR, message);
	}

	/*
	* display an snack warning message.
	*/
	public warning(message: string) {
		this.set(Constants.MESSAGE_WARNING, message);
	}

	/*
	* display an snack info message.
	*/
	public info(message: string) {
		this.set(Constants.MESSAGE_INFO, message);
	}
}
