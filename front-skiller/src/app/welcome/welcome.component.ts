import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import { AppComponent } from '../app.component';
import {Constants} from '../constants';


@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

	constructor(private cinematicService:CinematicService) { }

	ngOnInit() {
		this.cinematicService.setForm(Constants.WELCOME);
  	}

}
