import {Component, OnInit} from '@angular/core';
import {CinematicService} from './cinematic.service';
import {DataService} from './data.service';
import {Constants} from './constants';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

  /**
  * Title of the form
  */
  public formTitle: string;

  /**
  * Form Id
  */
  public formId: Number;

  /**
  * Searching mode ON. The INPUT searching is enabled.
  */
  is_searching: boolean;

  /**
  * Master/Detail mode ON. The goBack() and goFoward() buttons are visible
  */
  in_master_detail: boolean;

  /**
  * content of the searching filed.
  */
  searching_what: string;

  private nextId: number;
  private previousId: number;

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService,
    private location: Location,
    private router: Router) {


    this.cinematicService.newFormDisplayEmitted$.subscribe(data => {

      this.formId = data;
      switch (this.formId) {
        case Constants.WELCOME: {
          this.formTitle = 'Who\'s who';
          this.in_master_detail = false;
          this.is_searching = false;
          break;
        }
        case Constants.SKILLS_CRUD: {
          this.formTitle = 'Register a new skill';
          this.in_master_detail = false;
          this.is_searching = false;
          break;
        }
        case Constants.SKILLS_SEARCH: {
          this.formTitle = 'Searching a skill';
          this.in_master_detail = false;
          this.is_searching = true;
          break;
        }
        case Constants.DEVELOPERS_CRUD: {
          this.in_master_detail = (this.searching_what != null);
          this.is_searching = false;
          this.formTitle = 'Developer Update...';
          break;
        }
        case Constants.DEVELOPERS_SEARCH: {
          this.formTitle = 'Looking for a hero...';
          this.in_master_detail = false;
          this.is_searching = true;
          break;
        }
      }

    });


    this.dataService.newCollaboratorDisplayEmitted$.subscribe(data => {
      if (Constants.DEBUG) {
        console.log('Receiving data ' + data);
      }
      setTimeout(() => {
        this.previousId = dataService.previousCollaboratorId(data);
        this.nextId = dataService.nextCollaboratorId(data);
      }
      );
      if (Constants.DEBUG) {
        console.log('this.previousId ' + this.previousId);
        console.log('this.nextId ' + this.nextId);
      }
    });
  }

  ngOnInit() {
    this.formTitle = 'Welcome';
    this.is_searching = true;
  }

  /**
	* Search button has been clicked.
	*/
  search(): void {
    if (Constants.DEBUG) {
      console.log('Searching ' + this.searching_what);
    }
    switch (this.formId) {
      case Constants.DEVELOPERS_SEARCH:
        if (Constants.DEBUG) {
          console.log('Reloading collaborators for search criteria ' + this.searching_what);
        }
        this.dataService.reloadCollaborators(this.searching_what);
        break;
      case Constants.SKILLS_SEARCH: {
        break;
      }
    }
  }

  goBack(): void {
    this.location.back();
  }

  goNewDeveloper(): void {
    if (Constants.DEBUG) {
      console.log('Entering in the method goNewDeveloper()');
    }
    this.searching_what = null;
    this.router.navigate(['/user'], {});
  }  
}
