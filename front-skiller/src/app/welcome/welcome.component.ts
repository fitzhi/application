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
 }

 click1() {
   this.messageBoxService.question('Question', 'Test de Frederic').subscribe(answer => {
    if (answer) {
      console.log ('oui');
    }
   });
   console.log ('Frederic');
 }

 click2() {
    console.log ('Frederic');
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.position = { top: '6em', left: '5em'};
    dialogConfig.panelClass = 'default-dialog-container-class';
    this.dialog.open(DialogFilterComponent, dialogConfig);
 }
}
