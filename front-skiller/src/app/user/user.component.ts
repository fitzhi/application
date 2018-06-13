import {Component, OnInit} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Collaborater} from '../collaborater';

import {Level} from '../data/level';

import {LIST_OF_LEVELS} from '../data/List_of_levels';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  
  levels: Level[] = LIST_OF_LEVELS;
  
  
  collaborater: Collaborater;

  code: string;
  
  constructor(private cinematicService: CinematicService) {}

  ngOnInit() {
    this.collaborater = new Collaborater(0, "Frédéric", "VIDAL", "altF4", 'frvidal@sqli.com', 'ET 2');
    this.cinematicService.setForm("Welcome to a new developer !");
    this.code = this.levels[0].code;
  }

  save(): void {
    console.log("saving data for M." + this.collaborater.lastName);
  }

  update = function() {
    console.log("updating the value");
    // $scope.item.size.code = $scope.selectedItem.code;
  }

  selectchange(args){ 
    console.log("change");
    console.log(args.target.value); 
//  this.countryName = args.target.options[args.target.selectedIndex].text; 
  } 
}


