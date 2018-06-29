import { Component, OnInit, Input } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import { DataService } from '../data.service';
import {Constants} from '../constants';
import {Collaborater} from '../data/collaborater';

@Component({
  selector: 'app-search-user',
  templateUrl: './search-user.component.html',
  styleUrls: ['./search-user.component.css']
})
export class SearchUserComponent implements OnInit {

	private collaboraters: Collaborater[] = [];

  	constructor(
  		private cinematicService:CinematicService,
  		private dataService:DataService) {}

  	ngOnInit() {
  	
  		this.cinematicService.setForm(Constants.DEVELOPERS_SEARCH); 		
  		if (!this.dataService.hasDataArrayCollaboratersAlreadySet()) {
  			console.log("hasDataArrayCollaboratersAlreadySet is false");
	   		this.dataService.setDataArrayCollaboraters (this.collaboraters);
	   	} else {
	   		this.collaboraters = this.dataService.getStaff();
	   	}
   	}

}
