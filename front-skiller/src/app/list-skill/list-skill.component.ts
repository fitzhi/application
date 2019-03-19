import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {CinematicService} from '../service/cinematic.service';
import {Constants} from '../constants';
import {Skill} from '../data/skill';
import { StaffService } from '../service/staff.service';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material';
import { SkillService } from '../service/skill.service';

@Component({
  selector: 'app-list-skill',
  templateUrl: './list-skill.component.html',
  styleUrls: ['./list-skill.component.css']
})
export class ListSkillComponent implements OnInit {

  public dataSource: Skill[];

  public peopleCountExperience: Map<string, number> = null;

  private vide = {};

  public behaviorSubjectCountExperience = new BehaviorSubject(this.vide);

  public displayedColumns: string[] = ['skill', 'level-1', 'level-2', 'level-3', 'level-4', 'level-5'];

  /**
   * Fake arrays created to iterate with a *ngFor for the rating stars.
   */
  public fakeArray2 = new Array(2);
  public fakeArray3 = new Array(3);
  public fakeArray4 = new Array(4);
  public fakeArray5 = new Array(5);


  constructor(
    private cinematicService: CinematicService,
    private skillService: SkillService,
    private staffService: StaffService,
    private router: Router) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.SKILLS_SEARCH, this.router.url);
    this.peopleCountExperience = this.staffService.getPeopleCountExperience();
    this.dataSource = this.skillService.skills;
  }

}
