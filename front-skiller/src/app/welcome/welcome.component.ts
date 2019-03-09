import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

  public criteria = 'a';

  public activeOnly = false;

  constructor() { }

  ngOnInit() {
  }

}
