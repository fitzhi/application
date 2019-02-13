import { Component, OnInit } from '@angular/core';
import { ProjectStaffService } from '../../project-staff-service/project-staff.service';
import { Constants } from '../../../constants';

@Component({
  selector: 'app-dialog-filter',
  templateUrl: './dialog-filter.component.html',
  styleUrls: ['./dialog-filter.component.css']
})
export class DialogFilterComponent implements OnInit {

  constructor(private projectStaffService: ProjectStaffService) { }

  ngOnInit() {
    if (Constants.DEBUG) {
      console.group ('Contributors list');
      this.projectStaffService.contributors.forEach (entry => {
        console.log(entry.fullname);
      });
      console.groupEnd ();
    }
  }

  submit() {
    console.log ('submit');
  }
}
