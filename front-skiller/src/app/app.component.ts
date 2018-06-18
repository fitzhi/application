import { Component } from '@angular/core';
import { CinematicService } from './cinematic.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
   
	public form: String;
 
 	/**
 	* Searching mode ON. The INPUT searching is enabled. 
 	*/
   	is_searching: boolean;
   
	constructor(private cinematicService:CinematicService) { 
 
		this.cinematicService.newFormDisplayEmitted$.subscribe(data => {
			this.form = data;
			this.is_searching = this.cinematicService.is_searching;
		})
   }
    
	ngOnInit() {
		this.form = "welcome";
		this.is_searching = true;
	}
}
