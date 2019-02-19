import { Component, OnInit, AfterViewInit, Input, OnDestroy, Inject } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { Constants } from '../../constants';
import { MessageService } from '../../message/message.service';
import { ProjectService } from '../../service/project.service';
import { ActivatedRoute } from '@angular/router';
import { CinematicService } from '../../service/cinematic.service';
import { Project } from '../../data/project';
import { MatDialogConfig, MatDialog } from '@angular/material';
import { DialogProjectGhostsComponent } from './dialog-project-ghosts/dialog-project-ghosts.component';
import { ProjectGhostsDataSource } from './dialog-project-ghosts/project-ghosts-data-source';
import { DialogLegendSunburstComponent } from './dialog-legend-sunburst/dialog-legend-sunburst.component';
import { MessageBoxService } from '../../message-box/service/message-box.service';
import { DialogFilterComponent } from './dialog-filter/dialog-filter.component';
import { BaseComponent } from '../../base/base.component';
import { SettingsGeneration } from '../../data/settingsGeneration';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-project-sunburst',
  templateUrl: './project-sunburst.component.html',
  styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

  /**
   * The project loaded in the parent component.
   */
  @Input('subjProject') subjProject;

  /**
  * Project loaded on the parent component.
  */
  private project: Project;

  /**
   * Parameters passed to the generation method on the back-end.
   */
  private settings = new SettingsGeneration(-1, new Date(0).getTime(), 0);

  public dataGhosts: ProjectGhostsDataSource;

  // this boolean is indicating that the sunburst chart is ready to be viewed.
  public sunburst_ready = false;

  // this boolean is indicating that the sunburst chart is not possible.
  public sunburst_impossible = true;

  // this boolean is indicating that the sunburst chart is on fabrication
  public sunburst_waiting = false;

  // Waiting images previewed during the chart generation.
  public sunburstWaitingImage = '/assets/img/sunburst-waiting-image.png';

  // Rules of risks panel has to be displayed.
  public LEGEND_SUNBURST = 1;

  // Settings panel has to be displayed.
  public SETTINGS = 2;

  // Unknown contributors panel has to be displayed.
  public UNKNOWN = 3;

  // After confirmation, we reset the dashboard data.
  public RESET = 4;

  // Identifier of the panel selected.
  private idPanelSelected = -1;

  public projectName: string;

  private myChart: Sunburst;

  constructor(
    @Inject(DOCUMENT) private document: any,
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private messageBoxService: MessageBoxService,
    private dialog: MatDialog,
    private projectService: ProjectService) {
      super();
  }

  ngOnInit() {

  this.subscriptions.add(this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.settings.idProject = null;
      } else {
        this.settings.idProject = + params['id']; // (+) converts string 'id' to a number
      }
    }));

    this.subscriptions.add(
      this.subjProject.subscribe(project => {
      if (Constants.DEBUG) {
        console.log('Project ' + project.id + ' ' + project.name + ' reveived in sunburst-component');
      }
      this.project = project;
      this.projectName = this.project.name;
      if ((typeof this.project.urlRepository === 'undefined') || (this.project.urlRepository.length === 0)) {
        this.messageService.info('No repository URL avalaible !');
        this.sunburst_ready = false;
        this.sunburst_waiting = false;
        this.sunburst_impossible = true;
      }
    }));

    this.subscriptions.add(
      this.cinematicService.tabProjectActivated.subscribe(
      index => {
        if (index === Constants.PROJECT_IDX_TAB_SUNBURST) {
          if (Constants.DEBUG) {
           const today = new Date();
           console.log ('Tab selected ' + index + ' @ ' + today.getHours() + ':' + today.getMinutes() + ':' + today.getSeconds());
          }
          this.loadSunburst();
        }
      }
    ));

    if (this.settings.idProject == null) {
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

  /**
   * Load the dashboard data in order to produce the sunburst chart.
   */
  loadSunburst() {

     if ((typeof this.project === 'undefined') || (typeof this.project.id === 'undefined')) {
      this.sunburst_impossible = true;
      this.sunburst_waiting = false;
      this.sunburst_ready = false;
      return;
    } else {
      this.sunburst_impossible = false;
      this.sunburst_waiting = true;
      this.sunburst_ready = false;
    }

    if ((document.getElementById('chart') != null) && (this.settings.idProject != null)) {

      if (typeof this.myChart === 'undefined') {
        this.myChart = Sunburst();
      }

      this.subscriptions.add(
        this.projectService.loadDashboardData(this.settings)
          .subscribe(
            response => this.handleSunburstData(response),
            response => this.handleErrorData(response),
            () => {
              this.hackSunburstStyle();
              this.tooltipChart();
              this.sunburst_ready = true;
              this.sunburst_waiting = false;
            }));
    }
  }

  handleSunburstData(response: any) {
    if (this.myChart !== null) {
      this.myChart.data(response.sunburstData).width(500).height(500).label('location').size('numberOfFiles').color('color')
        (document.getElementById('chart'));
      if (typeof this.dataGhosts === 'undefined') {
        this.dataGhosts = new ProjectGhostsDataSource(this.project);
      }
      // Send the unregistered contributors to the panel list
      this.dataGhosts.sendUnknowns(response.ghosts);
    }
  }

  handleErrorData(response: any) {
    if (Constants.DEBUG) {
      console.log('Response returned while retrieving the sunburst data for the project identfier ' +
            this.settings.idProject);
    }
    switch (response.status) {
      case 404: {
          this.messageService.error(
            'Resource not found while retrieving the sunburst data for the project identfier ' +
              this.settings.idProject);
          break;
        }
      case 400: {
        switch (response.error.code) {
          case 201:
            // The generation is not accessible. A dashboard generation is launched asynchronously.
            this.messageBoxService.exclamation('Operation launched', response.error.message);
            break;
          case -999:
            // Operation already been launched.
            this.messageBoxService.exclamation('Operation already launched', response.error.message);
            break;
          default:
              // We display the error generated on the server
              this.messageService.error('ERROR ' + response.error.message);
            }
        break;
      }
      default: {
        // Unattempted error
        this.messageService.error('ERROR ' + response.message);
        break;
      }
    }
  }

  tooltipChart() {
    this.myChart.tooltipContent(function (graph) {
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
  * @param idPanel Panel identifier
  */
  public show(idPanel: number) {
    switch (idPanel) {
      case this.UNKNOWN:
        this.dialogGhosts();
        break;
      case this.LEGEND_SUNBURST:
        this.dialogLegend();
        break;
      case this.RESET:
        this.reset();
        break;
      case this.SETTINGS:
        this.dialogFilter();
        break;
      default:
        break;
    }
  }

  dialogGhosts() {
    if (typeof this.dataGhosts === 'undefined') {
      this.messageService.info('Please wait !');
      return;
    }

    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.position = { top: '5em', left: '5em' };
    dialogConfig.panelClass = 'default-dialog-container-class';
    dialogConfig.data = this.dataGhosts;
    const dialogReference = this.dialog.open(DialogProjectGhostsComponent, dialogConfig);
    this.subscriptions.add(
      dialogReference.afterClosed()
        .subscribe(result => {
          if (result !== null) {
            if (typeof result === 'boolean') {
              this.dataGhosts.ghostsSubject.next(this.dataGhosts.ghostsSubject.getValue());
            } else {
              this.dataGhosts.ghostsSubject.next(result);
            }
          }
        }));
  }

  dialogLegend() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.position = { top: '5em', left: '5em' };
    dialogConfig.panelClass = 'default-dialog-container-class';
    this.dialog.open(DialogLegendSunburstComponent, dialogConfig);
  }

  reset() {
    this.subscriptions.add(
      this.messageBoxService.question('Reset the dashboard',
        'Please confirm the dashboard reinitialization').subscribe(answer => {
          if (answer) {
            this.subscriptions.add(
              this.projectService.resetDashboard(this.settings.idProject).subscribe(response => {
                if (response) {
                  this.messageBoxService.exclamation('Operation complete',
                    'Dashboard reinitialization is done.');
                } else {
                  this.messageBoxService.exclamation('Operation failed',
                    'The request is not necessary : no dashboard available.');
                }
              }));
          }
        })
    );
  }

  dialogFilter () {
    if (typeof this.dataGhosts === 'undefined') {
      this.messageService.info('Please wait !');
      return;
    }

    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.position = { top: '6em', left: '5em'};
    dialogConfig.panelClass = 'default-dialog-container-class';
    const dlg = this.dialog.open(DialogFilterComponent, dialogConfig);
    this.subscriptions.add(
      dlg.afterClosed().subscribe( settings => {
        this.settings.idStaffSelected = (settings.idStaffSelected.length === 0) ? 0 : settings.idStaffSelected;
        this.settings.startingDate = settings.startingDate;
        this.subscriptions.add(
          this.projectService.loadDashboardData(this.settings)
            .subscribe(
              response => this.myChart.data(response.sunburstData),
              response => this.handleErrorData(response),
              () => {
                this.hackSunburstStyle();
                this.tooltipChart();
              }));
        }));
  }

  /**
  * The button associated to this panel id is activated.
  * @param idPanel panel identifier
  **/
  public buttonActivated(idPanel: number) {
    return (idPanel === this.idPanelSelected);
  }

  /**
   * Calling the base class to unsubscribe all subscriptions.
   */
  ngOnDestroy() {
    super.ngOnDestroy();
  }
}
