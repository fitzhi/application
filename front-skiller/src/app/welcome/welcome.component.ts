import {Component, OnInit} from '@angular/core';
import {CinematicService} from '../service/cinematic.service';
import {Constants} from '../constants';

import { Router } from '@angular/router';
import { MessageService } from '../message/message.service';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  constructor(
    private cinematicService: CinematicService,
    private messageService: MessageService,
    private router: Router) {}

  test: Map<string, number>;

  ngOnInit() {
    this.cinematicService.setForm(Constants.WELCOME, this.router.url);
 }

 click() {
   console.log ('click...');
   this.messageService.info('Test de Frederic');
 }
}
