import { Component, OnInit, AfterViewInit, Input } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { Constants } from '../../constants';
import { MessageService } from '../../message.service';
import { ProjectService } from '../../project.service';
import { ActivatedRoute } from '@angular/router';
import {CinematicService} from '../../cinematic.service';
import { Project } from '../../data/project';

@Component({
  selector: 'app-project-sunburst',
  templateUrl: './project-sunburst.component.html',
  styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent implements OnInit, AfterViewInit {

  /**
   * The project loaded in the parent component.
   */
  @Input('subjProject') subjProject;

 /**
   * Project loaded on the parent component.
   */
  private project: Project;

  /**
   * Project id passed by the router.
   */
  private idProject: number;

  private chart_is_drawn = false;

  // this boolean is indicating that the sunburst chart is ready to be viewed.
  public sunburst_ready = false;

  // this boolean is indicating that the sunburst chart is not possible.
  public sunburst_impossible = true;

  // this boolean is indicating that the sunburst chart is on fabrication
  public sunburst_waiting = false;

  // Waiting images previewed during the chart generation.
  public sunburstWaitingImage = '/assets/img/sunburst-waiting-image.png';

  // No button is selected.
  private UNSELECTED = -1;

  // Rules of risks panel has to be displayed.
  private RULES_OF_RISKS = 1;

  // Settings panel has to be displayed.
  private SETTINGS = 1;

  // Unknown contributors panel has to be displayed.
  private UNKNOWN = 1;

  // Identifier of the panel selected.
  private idPanelSelected = -1;

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

     this.subjProject.subscribe(project => {
        this.project = project;
        if ((typeof this.project.urlRepository === 'undefined') || (this.project.urlRepository.length === 0)) {
          this.messageService.info('No repository URL avalaible !');
          this.sunburst_ready = false;
          this.sunburst_waiting = false;
          this.sunburst_impossible = true;
        } else {
          this.loadSunburst ();
        }
     });

    if (this.idProject == null) {
      if (Constants.DEBUG) {
        console.log('No project identifier passed to this tab. No data available for preview !');
      }
      this.messageService.info('No project identifier passed to this form or no data available for preview !');
      this.sunburst_ready = false;
      this.sunburst_waiting = false;
      this.sunburst_impossible = true;
      return;
    }
  }


  loadSunburst() {

    // If chart has been already initialized, we do not need to generates it again.
    if (this.chart_is_drawn) {
      return;
    }

    if (typeof this.idProject === 'undefined') {
        this.sunburst_impossible = true;
        this.sunburst_waiting = false;
        this.sunburst_ready = false;
        return;
    } else {
        this.sunburst_impossible = false;
        this.sunburst_waiting = true;
        this.sunburst_ready = false;
    }



    if ((document.getElementById('chart') != null) && (this.idProject != null)) {

      const myChart = Sunburst();

      this.projectService.retrieveSuburstData(this.idProject)
        .subscribe(response => {
          myChart.data(response.sunburstData).width(500).height(500).label('location').size('numberOfFiles').color('color')
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
            myChart.tooltipContent(function (graph) {

              if (graph.lastUpdate != null) {
                const date = new Date(graph.lastUpdate);
                const day = date.getDate();
                const monthIndex = date.getMonth();
                const year = date.getFullYear();
                const tooltip = 'last commit ' + day + '/' + monthIndex + '/' + year;
                return tooltip;
              } else {
                return 'No commit here!';
              }
            });
            this.sunburst_ready = true;
            this.sunburst_waiting = false;
            // The chart is drawn. We do have to generate a new one.
            this.chart_is_drawn = true;
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

   /**
   * Show the panel associated to this id.
   * @param idPanel : Panel identifier
   */
  public show(idPanel: number) {
    if (this.idPanelSelected === this.UNSELECTED) {
      this.idPanelSelected = idPanel;
    } else {
      this.idPanelSelected = this.UNSELECTED;
    }
  }

   /**
   * The button associated to this panel id is activated.
   * @param idPanel : Panel identifier
   */
    public buttonActivated  (idPanel: number) {
      return (idPanel  === this.idPanelSelected);
    }
}
