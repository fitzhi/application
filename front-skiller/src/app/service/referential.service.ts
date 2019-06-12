import {Constants} from '../constants';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Profile} from '../data/profile';
import {RiskLegend} from '../data/riskLegend';
import { Skill } from '../data/skill';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { take } from 'rxjs/operators';

@Injectable()
export class ReferentialService {

  /*
   * List of profiles
   */
  public profiles: Profile[] = [];

  /*
   * Legend of the sunburst chart.
   */
  public legends: RiskLegend[] = [];

  /*
   * Skills.
   */
  public skills: Skill[] = [];

  constructor(private httpClient: HttpClient, private backendSetupService: BackendSetupService) {
  }

  /**
   * Loading all referential.
   * This method should be called on the main container (app.component) at startup...
   */
  public loadAllReferentials(): void {

    if (Constants.DEBUG) {
        if (!this.backendSetupService.hasSavedAnUrl()) {
            console.log('First start of application. Referentials loading is postponed.');
            return;
        } else {
            console.log('Fetching the profiles on URL ' + this.backendSetupService.url() + '/referential/profiles');
        }
    }

    if (!this.backendSetupService.hasSavedAnUrl()) {
        return;
    }

    this.httpClient.get<Profile[]>(this.backendSetupService.url() + '/referential/profiles')
        .pipe(take(1))
        .subscribe(
      (profiles: Profile[]) => {
          if (Constants.DEBUG) {
            console.groupCollapsed('Staff profiles : ');
            profiles.forEach(function (profile) {
              console.log (profile.code + ' ' + profile.title);
            });
            console.groupEnd();
          }
          profiles.forEach(profile => this.profiles.push(profile));
      });

      this.httpClient.get<RiskLegend[]>(this.backendSetupService.url() + '/referential/riskLegends')
         .pipe(take(1))
         .subscribe(
      (legends: RiskLegend[]) => {
        if (Constants.DEBUG) {
          console.groupCollapsed('Risk legends : ');
          legends.forEach(function (legend) {
            console.log (legend.level + ' ' + legend.color + ' ' + legend.description);
          });
          console.groupEnd();
        }
        legends.forEach(legend => this.legends.push(legend));
      });
  }

}
