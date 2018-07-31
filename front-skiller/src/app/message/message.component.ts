import { Component, OnInit } from '@angular/core';

import {MessageService} from '../message.service';
import {Message} from '../message';
import {Constants} from '../constants';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {

	myMessage: string = '';
	isError: boolean = false;
	isInfo : boolean = false;
	
	constructor(
		private messageService: MessageService) {}

  	ngOnInit() {
    	this.messageService.newMessage$.subscribe( (data: Message) => 
    	{ 
    		this.myMessage = data.message; 
    		switch (data.severity) {
    			case Constants.MESSAGE_VOID:
    				this.isError = false;
    				this.isInfo = false;
    				break;
    			case Constants.MESSAGE_ERROR:
    				this.isError = true;
    				this.isInfo = false;
    				break;
    			case Constants.MESSAGE_INFO:
    				this.isError = false;
    				this.isInfo = true;
    				break;    				
    		}
    		console.log(this.isError);
    	});
  	}
}
