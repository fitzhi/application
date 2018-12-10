import {Constants} from './constants';
import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {Profile} from './data/profile';

@Injectable()
export class ReferentialService {

  HOST = 'http://localhost:8080/';

  /*
   * List of profiles
   */
  public profiles: Profile[];

  private vide = Profile[0];
  public behaviorSubjectProfiles = new BehaviorSubject(this.vide);

  constructor(private httpClient: HttpClient) {
  }

  /**
   * Loading all referential. 
   * This method should be called on the main container (app.component) at startup...
   */
  public loadAllReferentials(): void {
    if (Constants.DEBUG) {
      console.log('Fetching the profiles on URL ' + this.HOST + '/data/profiles');
    }
    this.httpClient.get<Profile[]>(this.HOST + '/data/profiles').subscribe(
      (profiles: Profile[]) => {
         this.behaviorSubjectProfiles.next(profiles);
      },
      response_error => console.error(response_error.error.message));
  }
}
