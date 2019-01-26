import {Component, OnInit, ViewChild} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Constants} from '../constants';

import { Router } from '@angular/router';
import { Unknown } from '../data/Unknown';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  public unknowns: Unknown[] = [{login: 'Test'}, {login: 'Sonarcube'}];

  public projectName = 'VEGEO';

  constructor(
    private cinematicService: CinematicService,
    private router: Router) {}

  test: Map<string, number>;

  ngOnInit() {
    this.cinematicService.setForm(Constants.WELCOME, this.router.url);
 }

}
