import {Component} from '@angular/core';
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
export class AppComponent {

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
          /*
           * Cleaning up the list if we are in CREATION mode
           */
          if (this.searching_what == null) {
            this.formTitle = 'New developer registration...';
            dataService.cleanUpCollaboraters();
          } else {
            this.formTitle = 'Developer Update...';
          }
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
        this.dataService.reloadCollaboraters(this.searching_what);
        if (Constants.DEBUG) {
          console.log('this.dataService.hasDataArrayCollaboratersAlreadySet() ' + this.dataService.hasDataArrayCollaboratersAlreadySet());
        }
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

  nextEntry(): void {
    if (Constants.DEBUG) {
      console.log('Entering in the method nextEntry()');
    }
  }

  previousEntry(): void {
    if (Constants.DEBUG) {
      console.log('Entering in the method previousEntry()');
    }
  }

}
