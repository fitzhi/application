import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-staff-upload-cv',
  templateUrl: './staff-upload-cv.component.html',
  styleUrls: ['./staff-upload-cv.component.css']
})
export class StaffUploadCvComponent implements OnInit {

  applicationFile: string;

  progression = new Subject<number>()
  progress = this.progression.asObservable();

  constructor() { }

  ngOnInit() {
    this.progression.next(30 / 100);
  }

}
