import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import {Constants} from '../constants';

@Component({
  selector: 'app-search-user',
  templateUrl: './search-user.component.html',
  styleUrls: ['./search-user.component.css']
})
export class SearchUserComponent implements OnInit {

  constructor(private cinematicService:CinematicService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.DEVELOPERS_SEARCH);
  }

}
