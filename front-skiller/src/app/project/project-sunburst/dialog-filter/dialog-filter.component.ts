import { Component, OnInit } from '@angular/core';
import { ProjectStaffService } from '../../project-staff-service/project-staff.service';
import { Constants } from '../../../constants';
import { Contributor } from '../../../data/contributor';

@Component({
  selector: 'app-dialog-filter',
  templateUrl: './dialog-filter.component.html',
  styleUrls: ['./dialog-filter.component.css']
})
export class DialogFilterComponent implements OnInit {

  public contributors: Contributor[];

  constructor(private projectStaffService: ProjectStaffService) { }

  ngOnInit() {
    if (Constants.DEBUG) {
      console.group('Contributors list');
      this.projectStaffService.contributors.forEach(entry => {
        console.log(entry.fullname);
      });
      console.groupEnd();
    }

    this.contributors = this.projectStaffService.contributors;
  }

  submit() {
    console.log('submit');
  }
}
