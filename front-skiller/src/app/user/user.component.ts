import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import {Collaborater} from '../collaborater';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  constructor(private cinematicService:CinematicService) {}

  ngOnInit() {
    this.cinematicService.setForm("Welcome to a new developer !");
  }

}
