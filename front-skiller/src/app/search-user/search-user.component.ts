import { Component, OnInit, Input } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import {Constants} from '../constants';
import {Collaborater} from '../data/collaborater';

@Component({
  selector: 'app-search-user',
  templateUrl: './search-user.component.html',
  styleUrls: ['./search-user.component.css']
})
export class SearchUserComponent implements OnInit {

		//FIXME
    m_collaboraters: Collaborater[] = [

   		{ 
    		id:0, 
    		firstName:'Zinédine', 
	    	lastName:'ZIDANE', 
    		nickName:'altF4', 
    		email:'frvidal@sqli.com', 
	    	level:'ET 2', 
    		projects: 
    		[	{project_id: 1, from_date: null, to_date: null}, 
    			{project_id: 2, from_date: null, to_date: null}
			]
		}

    ];
	
  	constructor(private cinematicService:CinematicService) {}

  	ngOnInit() {
   		this.cinematicService.setForm(Constants.DEVELOPERS_SEARCH);
   	}

	public search(what: String) : void {
		console.log ('search what ?' + what);
	}

}
