import { Component, OnInit, AfterViewInit } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../../constants';
import { MessageService } from '../../message.service';

@Component({
  selector: 'app-project-sunburst',
  templateUrl: './project-sunburst.component.html',
  styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent implements OnInit, AfterViewInit {

  constructor(
    private httpClient: HttpClient,
    private messageService: MessageService) { }

  private testUrl = 'http://localhost:8080/test';  // URL to web api

  ngOnInit() {

    const myChart = Sunburst();

    this.httpClient.get<any>(this.testUrl + '/sunburst-test')
      .subscribe(data => {

        myChart.data(data).width(500).height(500).label('location').size('numberOfFiles').color('color')
          (document.getElementById('chart'));

        myChart.tooltipContent(function (graph) {
          return graph.lastUpdate;
        });
      },
        error => {
          if (error.status === 404) {
            if (Constants.DEBUG) {
              console.log('404');
            }
            this.messageService.error('Error message ' + error.status);
          } else {
            console.error(error.message);
          }
        });
  }

  /**
   * After creation treatment.
   */
  ngAfterViewInit() {
//    this.hackSunburstStyle();
  }

}
