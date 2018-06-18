import { Component } from '@angular/core';
import { CinematicService } from './cinematic.service';
import {Constants} from './constants';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
   
	public formTitle: String;
 
 	/**
 	* Searching mode ON. The INPUT searching is enabled. 
 	*/
   	is_searching: boolean;
   
	constructor(private cinematicService:CinematicService) { 
 
		this.cinematicService.newFormDisplayEmitted$.subscribe(data => {
		
			switch(data) { 
   				case Constants.WELCOME: { 
   					this.formTitle = "Who's who";
		    	  	this.is_searching = false;
		      		break; 
				} 
   				case Constants.SKILLS_CRUD: { 
   					this.formTitle = "Register a new skill";
		    	  	this.is_searching = false;
		      		break; 
				} 
   				case Constants.SKILLS_SEARCH: { 
   					this.formTitle = "Searching a skill";
		    	  	this.is_searching = true;
		      		break; 
				} 
   				case Constants.DEVELOPERS_CRUD: { 
   					this.formTitle = "Registering a new developer...";
		    	  	this.is_searching = false;
		      		break; 
				} 
   				case Constants.DEVELOPERS_SEARCH: { 
   					this.formTitle = "Looking for a hero...";
		    	  	this.is_searching = true;
		      		break; 
				} 
			} 		
		
		})
   }
    
	ngOnInit() {
		this.formTitle = "welcome";
		this.is_searching = true;
	}
}
