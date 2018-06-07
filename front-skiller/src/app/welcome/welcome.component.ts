import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import { AppComponent } from '../app.component';


@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(private cinematicService:CinematicService) { }

  ngOnInit() {
  	console.log ("welcome");
    this.cinematicService.setForm("Who's who !");
  }

}
