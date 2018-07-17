import {Component, OnInit, Input} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {DataService} from '../data.service';
import {Constants} from '../constants';
import {Collaborator} from '../data/collaborator';

@Component({
  selector: 'app-search-user',
  templateUrl: './search-user.component.html',
  styleUrls: ['./search-user.component.css']
})
export class SearchUserComponent implements OnInit {

  private collaborators: Collaborator[];

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService) {}

  ngOnInit() {

    this.cinematicService.setForm(Constants.DEVELOPERS_SEARCH);
    this.collaborators = this.dataService.getStaff();
  }

}
