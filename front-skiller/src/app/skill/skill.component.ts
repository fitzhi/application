import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {

  constructor(private cinematicService:CinematicService) {}

  ngOnInit() {
    this.cinematicService.setForm("Skills repository");
  }

}
