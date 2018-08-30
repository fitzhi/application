import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import { AppComponent } from '../app.component';
import {Constants} from '../constants';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {CollaboratorService} from '../collaborator.service';
import {StaffDTO} from '../data/external/staffDTO';


@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

	constructor(private cinematicService:CinematicService, private http: HttpClient, private collaboratorService: CollaboratorService) { }

	ngOnInit() {
		this.cinematicService.setForm(Constants.WELCOME);
		console.log ('Go ?');
		this.collaboratorService.addProject (2, 'TEST').subscribe( 
			(staffDTO: StaffDTO) => { console.log (staffDTO.staff) }
			, response => console.log(response.error.message)
			);
  	}

}
