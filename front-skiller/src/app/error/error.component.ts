import { Component, OnInit } from '@angular/core';
import { Constants } from '../constants';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {

  private sub: any;

	error: string;
	
  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.sub = this.route.queryParams.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'error\'] ' + params['error']);
      }
      if (params['error'] == null) {
		this.error = 'Unknown error !!!';   
      } else {
        this.error = params['error'];
      }
	});
  }

}
