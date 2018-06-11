import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import {Collaborater} from '../collaborater';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  collaborater: Collaborater;
  
  constructor(private cinematicService:CinematicService) {}

  ngOnInit() {
    this.collaborater = new Collaborater(0, "Frédéric", "VIDAL", "altF4", 'frvidal@sqli.com');
    this.cinematicService.setForm("Welcome to a new developer !");
  }

    save() : void {
      console.log ("saving data for M." + this.collaborater.lastName);
    }

}
