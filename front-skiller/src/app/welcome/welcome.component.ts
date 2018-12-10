import {Component, OnInit} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {AppComponent} from '../app.component';
import {Constants} from '../constants';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {StaffService} from '../staff.service';
import {StaffDTO} from '../data/external/staffDTO';

import {BehaviorSubject, Subject} from 'rxjs';
import {Observable, of} from 'rxjs';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(private cinematicService: CinematicService, private http: HttpClient, private collaboratorService: StaffService) {}

  test: Map<string, number>;

  ngOnInit() {
    this.cinematicService.setForm(Constants.WELCOME);
    this.test = new Map<string, number>();
    this.test.set ('1-1', 6);
    this.test.set ('1-2', 1);
    console.log (this.test);
    console.log (JSON.stringify(this.test));
    console.log (JSON.stringify(this.test.get('1-1')));
  }

}
