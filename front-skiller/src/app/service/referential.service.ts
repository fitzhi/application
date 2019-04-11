import {Constants} from '../constants';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Profile} from '../data/profile';
import {RiskLegend} from '../data/riskLegend';
import { Skill } from '../data/skill';
import { BackendSetupService } from './backend-setup/backend-setup.service';

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
      console.log('Fetching the profiles on URL ' + this.backendSetupService.url() + '/data/profiles');
    }
    const subProfiles = this.httpClient.get<Profile[]>(this.backendSetupService.url() + '/data/profiles').subscribe(
      (profiles: Profile[]) => {
          if (Constants.DEBUG) {
            console.groupCollapsed('Staff profiles : ');
            profiles.forEach(function (profile) {
              console.log (profile.code + ' ' + profile.title);
            });
            console.groupEnd();
          }
          profiles.forEach(profile => this.profiles.push(profile));
      },
      response_error => console.error(response_error.error.message),
      () => setTimeout(() => {subProfiles.unsubscribe(); }, 1000));

      const subLegends = this.httpClient.get<RiskLegend[]>(this.backendSetupService.url() + '/data/riskLegends').subscribe(
      (legends: RiskLegend[]) => {
        if (Constants.DEBUG) {
          console.groupCollapsed('Risk legends : ');
          legends.forEach(function (legend) {
            console.log (legend.level + ' ' + legend.color + ' ' + legend.description);
          });
          console.groupEnd();
        }
        legends.forEach(legend => this.legends.push(legend));
      },
      response_error => console.error(response_error.error.message),
      () => setTimeout(() => {subLegends.unsubscribe(); }, 1000));
  }

}
