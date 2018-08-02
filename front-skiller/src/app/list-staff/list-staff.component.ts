import {Component, OnInit, Input} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {DataService} from '../data.service';
import {Constants} from '../constants';
import {Collaborator} from '../data/collaborator';

@Component({
  selector: 'app-list-staff',
  templateUrl: './list-staff.component.html',
  styleUrls: ['./list-staff.component.css']
})
export class ListStaffComponent implements OnInit {

  private collaborators: Collaborator[];

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService) {}

  ngOnInit() {

    this.cinematicService.setForm(Constants.DEVELOPPERS_SEARCH);
    this.collaborators = this.dataService.getStaff();
  }

}
