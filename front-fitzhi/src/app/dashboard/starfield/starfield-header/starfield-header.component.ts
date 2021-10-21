import { Component, OnInit } from '@angular/core';
import { StarfieldService } from '../service/starfield.service';

@Component({
  selector: 'app-starfield-header',
  templateUrl: './starfield-header.component.html',
  styleUrls: ['./starfield-header.component.css']
})
export class StarfieldHeaderComponent implements OnInit {

  constructor(public starfieldService: StarfieldService) { }

  ngOnInit(): void {
  }

}
