import {Component, OnInit} from '@angular/core';
import {CinematicService} from '../service/cinematic.service';
import {Constants} from '../constants';

import { Router } from '@angular/router';
import { MessageBoxService } from '../message-box/service/message-box.service';
import { MatDialogConfig, MatDialog } from '@angular/material';
import { DialogFilterComponent } from '../project/project-sunburst/dialog-filter/dialog-filter.component';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(
    private cinematicService: CinematicService,
    private dialog: MatDialog,
    private messageBoxService: MessageBoxService,
    private router: Router) {}

  test: Map<string, number>;

  ngOnInit() {
    this.cinematicService.setForm(Constants.WELCOME, this.router.url);
    this.cinematicService.setProjectTab(1);
    this.cinematicService.tabProjectActivated.subscribe( ret => {
      console.log(ret);
    });
 }

 public click() {
    this.cinematicService.setProjectTab(2);
 }
}
