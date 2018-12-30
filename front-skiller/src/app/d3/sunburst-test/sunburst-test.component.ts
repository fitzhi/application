import { Component, OnInit } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-sunburst-test',
  templateUrl: './sunburst-test.component.html',
  styleUrls: ['./sunburst-test.component.css']
})
export class SunburstTestComponent implements OnInit {
  /*
    data = {
      'directory': 'VEGEO',
      'color': 'grey',
      'numberOfFiles': 20,
      'lastUpdate' : '12/11/18',
      'children': [{
          'directory': 'com',
          'color': 'grey',
          'lastUpdate' : '12/11/18',
   //       'numberOfFiles': 15,
          'children': [{
              'directory': 'google',
              'color': '#FF0000',
              'numberOfFiles': 5,
              'lastUpdate' : '12/11/18',
          }, {
              'directory': 'amazon',
              'color': 'lightGrey',
              'numberOfFiles': 10,
             'lastUpdate' : '12/11/18',
         }]
      }, {
          'directory': 'fr',
          'color': 'grey',
          'numberOfFiles': 5,
          'lastUpdate' : '12/25/18',
     }]
  };
  */
  constructor(private httpClient: HttpClient) { }

  private testUrl = 'http://localhost:8080/test';  // URL to web api

  ngOnInit() {
    const myChart = Sunburst();

    this.httpClient.get<any>(this.testUrl + '/sunburst-test')
      .subscribe(data => {
        myChart.data(data).width(800).height(800).label('directory').size('numberOfFiles').color('color')
          (document.getElementById('chart'));

        myChart.tooltipContent(function (graph) {
          console.log(graph.lastUpdate);
          return graph.lastUpdate;
        });
      });
  }
}
