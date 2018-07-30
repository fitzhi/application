import { Injectable } from '@angular/core';
import {Observable, of} from 'rxjs';
import {Subject} from "rxjs/Subject";

@Injectable({
  providedIn: 'root'
})
export class MessageService {

	private message: Subject<string> = new Subject<string>();
 
  	newMessage$ = this.message.asObservable();

 	constructor() { 
 	}
 	
 	/*
 	* set a new message
 	*/
 	public set (msg: string) {
 		this.message.next(msg);
 	}
}
