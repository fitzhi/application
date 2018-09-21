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

  ngOnInit() {
    this.cinematicService.setForm(Constants.WELCOME);
  }

}
