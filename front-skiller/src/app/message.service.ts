import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {Subject} from "rxjs/Subject";
import {Message} from "./message";
import {Constants} from "./constants";

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
  */
  public set(gravity: number, message: string) {
    this.myMessage.next(new Message(gravity, message));
    setTimeout(() => this.myMessage.next(new Message(Constants.MESSAGE_VOID, '')), 5000);
  }

  /*
  * set a new ERROR message
  */
  public error(message: string) {
    this.set(Constants.MESSAGE_ERROR, message);
  }

  /*
  * set a new INFO message
  */
  public info(message: string) {
    this.set(Constants.MESSAGE_INFO, message);
  }
}
