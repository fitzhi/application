import { Component, OnInit, AfterViewInit } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { Constants } from '../../constants';
import { MessageService } from '../../message.service';
import { ProjectService } from '../../project.service';
import { ActivatedRoute } from '@angular/router';
import {CinematicService} from '../../cinematic.service';

@Component({
  selector: 'app-project-sunburst',
  templateUrl: './project-sunburst.component.html',
  styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent implements OnInit, AfterViewInit {

  /**
   * Project id passed by the router.
   */
  private idProject: number;

  private charInitilalized = false;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private projectService: ProjectService) { }

  ngOnInit() {

    this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.idProject = null;
      } else {
        this.idProject = + params['id']; // (+) converts string 'id' to a number
      }
    });

    if (this.idProject == null) {
      if (Constants.DEBUG) {
        console.log('No project identifier passed to this tab. No data available for preview !');
      }
      this.messageService.error('No project identifier passed to this tab. No data available for preview !');
      return;
    }

    this.cinematicService.tabProjectActivated.subscribe (
      index => {
        if (index === Constants.PROJECT_TAB_SUNBURST) {
          this.loadSunburst ();
        }
      }
    );

  }


  loadSunburst() {

    // If chart has been already initialized, we do not need to generates it again.
    if (this.charInitilalized) {
      return;
    }

   if ((document.getElementById('chart') != null) && (this.idProject != null)) {

      const myChart = Sunburst();

      this.projectService.retrieveSuburstData(this.idProject)
        .subscribe(response => {
          myChart.data(response.sunburstData).width(600).height(600).label('location').size('numberOfFiles').color('color')
            (document.getElementById('chart'));
        },
          response => {
            if (Constants.DEBUG) {
              console.log('Error ' + response.status + ' while retrieving the sunburst data for the project identfier ' + this.idProject);
            }
            switch (response.status) {
              case 404: {
                this.messageService.error(
                  'Resource Not found while retrieving the sunburst data for the project identfier ' + this.idProject);
              }
                break;
              case 400: {
                // We display the error generated on the server
                this.messageService.error('ERROR ' + response.error.message);
                break;
              }
              default: {
                // Unattempted error
                this.messageService.error('ERROR ' + response.message);
                break;
              }
            }
          },
          () => {
            this.hackSunburstStyle();
            this.charInitilalized = true;
            myChart.tooltipContent(function (graph) {
              const tooltip = (graph.lastUpdate == null) ? '' : graph.lastUpdate;
              return tooltip;
            });
          });
    }
  }

  /**
   * After creation treatment.
   */
  ngAfterViewInit() {
  }

  /*
  * Replace the cssText for rule matching selectorText.
  * 2 rules are changes :
  *   1) We remove the white contour around the label inside the slices (stroke:'none')
  *   2) We put the tooltip on top of the components stack (z-index:'500')
  */
  hackSunburstStyle() {
    const sheets = document.styleSheets;
    let sheet, rules, rule;
    let i, j, k, iLen, jLen, kLen;

    for (i = 0, iLen = sheets.length; i < iLen; i++) {
      sheet = sheets[i];

      // W3C model
      if (sheet.cssRules) {
        rules = sheet.cssRules;
        for (j = 0, jLen = rules.length; j < jLen; j++) {
          rule = rules[j];
          if (typeof rule.selectorText !== 'undefined') {

            if (rule.selectorText.indexOf('sunburst-viz text .text-contour') > 0) {
              rule.style.stroke = 'none';
            }
            if (rule.selectorText.indexOf('sunburst-tooltip') > 0) {
              rule.style.zIndex = '500';
            }
          }
        }

        // IE model
      } else if (sheet.rules) {
        rules = sheet.rules;
        for (k = 0, kLen = rules.length; k < kLen; k++) {
          rule = rules[k];
          if (typeof rule.selectorText !== 'undefined') {
            if (rule.selectorText.indexOf('sunburst-viz text .text-contour') > 0) {
              rule.style.stroke = 'none';
            }
            if (rule.selectorText.indexOf('sunburst-tooltip') > 0) {
              rule.style.zIndex = '500';
            }
          }
        }
      }
    }
  }

}
