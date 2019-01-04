import { Component, OnInit } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-sunburst-test',
  templateUrl: './sunburst-test.component.html',
  styleUrls: ['./sunburst-test.component.css']
})
export class SunburstTestComponent implements OnInit {
  constructor(private httpClient: HttpClient) { }

  private testUrl = 'http://localhost:8080/test';  // URL to web api

  ngOnInit() {
    const myChart = Sunburst();

    this.httpClient.get<any>(this.testUrl + '/sunburst-test')
      .subscribe(data => {
        myChart.data(data).width(800).height(800).label('location').size('numberOfFiles').color('color')
          (document.getElementById('chart'));

        myChart.tooltipContent(function (graph) {
          console.log(graph.lastUpdate);
          return graph.lastUpdate;
        });
      });
  }
}
