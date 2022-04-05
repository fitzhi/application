import { Component, OnInit } from '@angular/core';
import { GoogleService } from 'src/app/service/google/google.service';

@Component({
  selector: 'alternative-openid-connection',
  templateUrl: './alternative-openid-connection.component.html',
  styleUrls: ['./alternative-openid-connection.component.css']
})
export class AlternativeOpenidConnectionComponent implements OnInit {

	public google = true;

	constructor(
		public googleService: GoogleService) { }

	ngOnInit(): void {
		this.googleService.initialize(document);
	}
	
}
