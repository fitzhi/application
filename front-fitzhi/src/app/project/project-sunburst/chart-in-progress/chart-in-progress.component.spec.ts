import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { FileService } from 'src/app/service/file.service';
import { ProjectService } from 'src/app/service/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { SSEWatcherComponent } from '../ssewatcher/ssewatcher.component';
import { ChartInProgressComponent } from './chart-in-progress.component';


describe('ChartInProgressComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;


	@Component({
		selector: 'app-host-component',
		template:
			`
      <div style="width:600px;height:100px;background-color:whiteSmoke;margin-left:50px;margin-top:50px;">
        <app-chart-in-progress></app-chart-in-progress>
			</div>
			`
  })
	class TestHostComponent {
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChartInProgressComponent, TestHostComponent, SSEWatcherComponent ],
      providers: [ReferentialService, ProjectService, CinematicService, MessageBoxService, FileService],
      imports: [MatProgressBarModule, HttpClientTestingModule, MatDialogModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Should create correctly the chart in progress component', () => {
    expect(component).toBeTruthy();
  });
});
