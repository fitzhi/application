import { Component, OnInit, AfterViewInit } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../../constants';
import { MessageService } from '../../message.service';

@Component({
  selector: 'app-sunburst-test',
  templateUrl: './sunburst-test.component.html',
  styleUrls: ['./sunburst-test.component.css']
})
export class SunburstTestComponent implements OnInit, AfterViewInit {
  constructor(
    private httpClient: HttpClient,
    private messageService: MessageService) { }

  public afterViewChecked = false;
/*
  public data = {
    'directory': 'VEGEO',
    'color': 'grey',
    'numberOfFiles': 20,
    'lastUpdate': '12/11/18',
    'children': [{
      'directory': 'com',
      'color': 'grey',
      'lastUpdate': '12/11/18',
      //       'numberOfFiles': 15,
      'children': [{
        'directory': 'google',
        'color': '#FF0000',
        'numberOfFiles': 5,
        'lastUpdate': '12/11/18',
      }, {
        'directory': 'amazon',
        'color': 'lightGrey',
        'numberOfFiles': 10,
        'lastUpdate': '12/11/18',
      }]
    }, {
      'directory': 'fr',
      'color': 'grey',
      'numberOfFiles': 5,
      'lastUpdate': '12/25/18',
    }]
  };
*/
  private testUrl = 'http://localhost:8080/test';  // URL to web api

  ngOnInit() {
    const myChart = Sunburst();

    this.httpClient.get<any>(this.testUrl + '/sunburst-test')
    .subscribe(data => {

      myChart.data(data).width(800).height(800).label('location').size('numberOfFiles').color('color')
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


  /* Replace the cssText for rule matching selectorText with value
** Changes all matching rules in all style sheets
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
              console.log(rule.style.stroke);
              console.log(rule.style.fill);
              rule.style.stroke = 'none';
              console.log(rule.style.stroke);
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
              console.log(rule.style.stroke);
              console.log(rule.style.fill);
              rule.style.stroke = 'none';
              console.log(rule.style.stroke);
            }
          }
        }
      }
    }
  }

  ngAfterViewInit() {
    this.hackSunburstStyle();
  }

}
