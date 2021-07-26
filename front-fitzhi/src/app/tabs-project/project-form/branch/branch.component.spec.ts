import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BranchComponent } from './branch.component';
import { ProjectService } from 'src/app/service/project/project.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { FileService } from 'src/app/service/file.service';
import { MatDialogModule } from '@angular/material/dialog';
import { of, BehaviorSubject } from 'rxjs';
import { Component } from '@angular/core';
import { Project } from 'src/app/data/project';
import { By } from '@angular/platform-browser';

describe('BranchComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let projectService: ProjectService;

	@Component({
		selector: 'app-host-component',
		template:
			'<div style="padding: 20px; margin: 20px;">' +
				'<app-branch-selector ' +
						'(messengerOnBranchChange)="onBranchChange($event)" >' +
				'</app-branch-selector>' +
			'</div>'
	})
	class TestHostComponent {
		public onBranchChange($event) {
			console.log ('selected branch', $event);
		}
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ BranchComponent, TestHostComponent ],
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [ProjectService, ReferentialService, CinematicService, FileService, MessageBoxService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		projectService = TestBed.inject(ProjectService);
		projectService.project = new Project();
		projectService.project.active = true;
		fixture.detectChanges();
	});

	it('should be created without error', () => {
		projectService.branches$.next(['master', 'branch-1.0', 'branch-1.1']);
		fixture.detectChanges();

		expect(component).toBeTruthy();

		const branch = field('#branch');
		expect(branch).toBeTruthy();
		expect(branch.disabled).toBeFalse();
	});

	it('should be readonly if the project is not active', () => {
		projectService.branches$.next(['master', 'branch-1.0', 'branch-1.1']);
		projectService.project.active = false;
		fixture.detectChanges();

		expect(component).toBeTruthy();

		expect(field('#branch')).toBeTruthy();
		const branch = field('#branch');
		expect(branch.disabled).toBeTrue();
	});

	it('should be selected on the actual branch of the project', () => {
		projectService.branches$.next(['master', 'branch-1.0', 'branch-1.1']);
		projectService.project.branch = 'branch-1.0';
		fixture.detectChanges();

		expect(component).toBeTruthy();

		const branch = field('#branch');
		expect(branch).toBeTruthy();
		expect(branch.disabled).toBeFalse();
		const option = fixture.debugElement.queryAll(By.css('option'))[1];
		expect(option).toBeDefined();
		expect(option.nativeElement.selected).toBe(true);


	});

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

});
