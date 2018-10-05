import { Collaborator } from '../../../../data/collaborator';
import { Component, OnInit, Inject } from '@angular/core';
import { Subject } from 'rxjs';
import {MAT_DIALOG_DATA} from '@angular/material';

@Component({
  selector: 'app-staff-upload-cv',
  templateUrl: './staff-upload-cv.component.html',
  styleUrls: ['./staff-upload-cv.component.css'],
})
export class StaffUploadCvComponent implements OnInit {

  applicationFile: string;

  collaborator: Collaborator;
  
  progression = new Subject<number>()
  progress = this.progression.asObservable();

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
//  constructor() { }

  ngOnInit() {

    this.progression.next(30 / 100);
  }

}
