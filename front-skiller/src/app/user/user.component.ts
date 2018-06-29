import {AppModule} from '../app.module';
import {Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Router} from '@angular/router';

import {CinematicService} from '../cinematic.service';
import {Collaborater} from '../data/collaborater';

import {Level} from '../data/level';
import {Attribution} from '../data/attribution';
import {Project} from '../data/project';

import {LIST_OF_LEVELS} from '../data/List_of_levels';
import {PROJECTS} from '../mock/mock-projects';
import {Constants} from '../constants';

import { DataService } from '../data.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

	private id: number;
	private sub: any;
	
  	private levels: Level[] = LIST_OF_LEVELS;
  	private projects: Project[] = PROJECTS;

  	private collaborater: Collaborater;

  	constructor(
  		private cinematicService: CinematicService,
  		private route: ActivatedRoute,
  		private dataService: DataService) {}

	ngOnInit() {
		this.sub = this.route.params.subscribe(params => {
			if (params['id'] == null) {
				console.log ("id is null");		 
				this.id = null;   
			} else {
	       		this.id = + params['id']; // (+) converts string 'id' to a number
	       	}
	    });	
		console.log (this.id);		    
		if (this.id == null) {
			// creation mode...
			this.collaborater = {id:null, firstName:null, lastName:null, nickName:null, email:null, level:null, projects:[]}
		} else {
//			this.sub = this.route.params.subscribe(params => {
//		       this.id = +params['id']; // (+) converts string 'id' to a number
//		    });	
		    
	    	this.collaborater = this.dataService.getCollaborater(this.id);
	    	if (Constants.DEBUG) {
	      		console.log('Reading the collaborater below');
	      		console.log(this.collaborater);
	 		}
 		}
 		this.cinematicService.setForm(Constants.DEVELOPERS_CRUD);
	}

	/**
	* The Validate Button has been activated
	*/
  	save(): void {
    	if (Constants.DEBUG) {
      		console.log('Saving data for the collaborater below');
      		console.log(this.collaborater);
    	}
  	}

}


