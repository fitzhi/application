import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';

@Component({
  selector: 'app-search-user',
  templateUrl: './search-user.component.html',
  styleUrls: ['./search-user.component.css']
})
export class SearchUserComponent implements OnInit {

  constructor(private cinematicService:CinematicService) {}

  ngOnInit() {
    this.cinematicService.setForm("Searching for a developer");
  }

}
