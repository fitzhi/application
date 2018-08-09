import { Component, OnInit } from '@angular/core';
import {Constants} from '../constants';
import {Skill} from '../data/skill';
import {ActivatedRoute} from '@angular/router';
import {FormGroup, FormControl} from '@angular/forms';

import {DataService} from '../data.service';
import {MessageService} from '../message.service';
import { CinematicService } from '../cinematic.service';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {

  private skill: Skill;

  /**
   * Id parameter received if any;
   */
  private id: number;

  private profileSkill = new FormGroup({
    skillTitle: new FormControl('')
  });

  private sub: any;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private dataService: DataService,
    private messageService: MessageService ) {}

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.id = null;
      } else {
        this.id = + params['id']; // (+) converts string 'id' to a number
      }

      // Either we are in creation mode, or we load the collaborator from the back-end...
      // We create an empty collaborator until the subscription is complete
      this.skill = new Skill();
      if (this.id != null) {
        this.dataService.getSkill(this.id).subscribe(
          (skill: Skill) => {
            this.skill = skill;
            this.profileSkill.get('skillTitle').setValue(skill.title);
          },
          error => {
            if (error.status === 404) {
              if (Constants.DEBUG) {
                console.log ('404 : cannot find a skill for the id ' + this.id);
              }
              this.messageService.error('There is no skill for id ' + this.id);
              this.skill = new Skill();
            } else {
                console.error (error.message);
            }
          },
          () => {
                    if (this.skill.id === 0) {
                      console.log ('No skill found for the id ' + this.id);
                    }
                    if (Constants.DEBUG) {
                      console.log('Loading comlete for id ' + this.id);
                    }
                  }
            );
      }
    });
    this.cinematicService.setForm(Constants.SKILLS_CRUD);
  }

  /**
   * Submit the change. The project will be created, or updated.
   */
  onSubmit() {
    this.skill.title = this.profileSkill.get('skillTitle').value;
    if (Constants.DEBUG) {
      console.log('saving the skill ' + this.skill.title + ' with id ' + this.skill.id);
    }
    this.dataService.saveSkill(this.skill).subscribe(
        skill => {
          this.skill = skill;
          this.messageService.info('Skill ' + this.skill.title + '  saved !');
        });
  }

}
