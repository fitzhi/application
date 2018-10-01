import { Constants } from '../../../constants';
import { Component, OnInit } from '@angular/core';

import {Ng2SmartTableModule} from 'ng2-smart-table';
import {LocalDataSource} from 'ng2-smart-table';
import {StarsSkillLevelRenderComponent} from './../../starsSkillLevelRenderComponent';
import {Ng2SmartTableComponent} from 'ng2-smart-table/ng2-smart-table.component';

@Component({
  selector: 'app-staff-experience',
  templateUrl: './staff-experience.component.html',
  styleUrls: ['./staff-experience.component.css']
})
export class StaffExperienceComponent implements OnInit {

  private sourceExperience = new LocalDataSource([]);
  private settings_experience = Constants.SETTINGS_EXPERIENCE_SMARTTABLE;

  constructor() { }

  ngOnInit() {
  }

}
