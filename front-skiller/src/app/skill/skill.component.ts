import { Component, OnInit } from '@angular/core';
import { Constants } from '../constants';
import { Skill } from '../data/skill';
import { ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { MessageService } from '../message.service';
import { CinematicService } from '../cinematic.service';
import { ListSkillService } from '../list-skill-service/list-skill.service';
import { SkillService } from '../skill.service';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {

  private skill: Skill;

  profileSkill = new FormGroup({
    title: new FormControl('', [Validators.required])
  });

  /**
   * Id parameter received if any;
   */
  private id: number;

  private sub: any;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private skillService: SkillService,
    private listSkillService: ListSkillService,
    private messageService: MessageService) { }

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
        this.listSkillService.getSkill(this.id).subscribe(
          (skill: Skill) => {
            this.skill = skill;
            this.profileSkill.get('title').setValue(skill.title);
          },
          error => {
            if (error.status === 404) {
              if (Constants.DEBUG) {
                console.log('404 : cannot find a skill for the id ' + this.id);
              }
              this.messageService.error('There is no skill for id ' + this.id);
              this.skill = new Skill();
            } else {
              console.error(error.message);
            }
          },
          () => {
            if (this.skill.id === 0) {
              console.log('No skill found for the id ' + this.id);
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
   * Submit the change. The SKILL will be created, or updated. succesfully.
   */
  onSubmit() {
    this.skill.title = this.profileSkill.get('title').value;
    if (Constants.DEBUG) {
      console.log('saving the skill ' + this.skill.title + ' with id ' + this.skill.id);
    }
    this.skillService.save(this.skill).subscribe(
      skill => {
        this.messageService.info('The skill ' + skill.title + ' has been succesfully saved !');
        this.skill = new Skill();
        this.id = null;
        this.profileSkill.get('title').setValue(this.skill.title);
      });
  }

  get title(): any {
    return this.profileSkill.get('title');
  }

}
