import {Constants} from './constants';
import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Profile} from './data/profile';
import {RiskLegend} from './data/riskLegend';

@Injectable()
export class ReferentialService {

  HOST = 'http://localhost:8080/';

  /*
   * List of profiles
   */
  public profiles: Profile[] = [];

  /**
   * Profiles subject.
   */
  public subjectProfiles = new BehaviorSubject(this.profiles);

  /*
   * Legend of the sunburst chart.
   */
  public legends: RiskLegend[] = [];

  /**
   * subject pointed to the Profiles.
   */
  public subjectLegends = new BehaviorSubject(this.legends);

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
      (profiles: Profile[]) => { this.subjectProfiles.next(profiles); },
      response_error => console.error(response_error.error.message));

      this.httpClient.get<RiskLegend[]>(this.HOST + '/data/riskLegends').subscribe(
      (legends: RiskLegend[]) => {
        if (Constants.DEBUG) {
          console.group('Risk legends : ');
          legends.forEach(function (legend) {
            console.log (legend.level + ' ' + legend.color + ' ' + legend.description);
          });
          console.groupEnd();
        }
        this.subjectLegends.next(legends);
      },
      response_error => console.error(response_error.error.message));
  }
}
