import { Component } from '@angular/core';
import { CinematicService } from './cinematic.service';
import { DataService } from './data.service';
import {Constants} from './constants';
import { Location } from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
   
   /**
   * Title of the form
   */
	public formTitle: string;
 
   /**
   * Form Id
   */
	public formId: Number;

 	/**
 	* Searching mode ON. The INPUT searching is enabled. 
 	*/
   	is_searching: boolean;
   
 	/**
 	* content of the searching filed. 
 	*/
   	searching_what: string;
   
      
	constructor(
		private cinematicService:CinematicService,
		private dataService:DataService,
		private location:Location) { 
 
 
		this.cinematicService.newFormDisplayEmitted$.subscribe(data => {
		
			this.formId = data;
			switch(this.formId) { 
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
	
	/**
	* Search button has been clicked.
	*/
	search() : void {
		if (Constants.DEBUG) {
			console.log ("Searching " + this.searching_what );
		}
		switch(this.formId) { 
			case Constants.DEVELOPERS_SEARCH:
				this.dataService.reloadCollaboraters (this.searching_what);
				break;
			case Constants.SKILLS_SEARCH: { 
	      		break; 
			} 
		} 		
	}
	
	goBack(): void { 
		this.location.back();
	}
	
}
