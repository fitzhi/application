import { Component, OnInit } from '@angular/core';

import {MessageService} from '../message.service';
import {Message} from '../message';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {

	myMessage: string = '';
	
	constructor(
		private messageService: MessageService) {}

  	ngOnInit() {
    	this.messageService.newMessage$.subscribe( (data: Message) => 
    	{ this.myMessage = data.message; });
  	}
}
