import {Component, OnInit} from '@angular/core';
import {CinematicService} from '../service/cinematic.service';
import {Constants} from '../constants';

import { Router } from '@angular/router';
import { MessageService } from '../message/message.service';
import { MessageBoxService } from '../message-box/service/message-box.service';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(
    private cinematicService: CinematicService,
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
   this.messageBoxService.error('Title', 'Test de Frederic');
   console.log ('Frederic');
 }
}
