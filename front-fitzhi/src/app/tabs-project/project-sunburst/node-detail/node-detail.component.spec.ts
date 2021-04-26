import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject } from 'rxjs';
import { Contributor } from 'src/app/data/contributor';
import { Filename } from 'src/app/data/filename';
import { ContributorsDataSource } from './contributors-data-source';
import { FilenamesDataSource } from './filenames-data-source';
import { ListContributorsComponent } from './list-contributors/list-contributors.component';
import { ListFilenamesComponent } from './list-filenames/list-filenames.component';
import { NodeDetailComponent } from './node-detail.component';

describe('NodeDetailComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `<div style="width: 400px; height: 300px">
						<app-node-detail 
							[filenames]="filenames" 
							[contributors]="contributors" 
							[location]="location$">
						</app-node-detail>
					</div>`
	})
	class TestHostComponent {
		public filenames = new FilenamesDataSource();
		public contributors = new MatTableDataSource<Contributor>()
		public location$ = new BehaviorSubject<string[]>([]);

		constructor() {
			this.filenames.setClassnames([new Filename('one', new Date()), new Filename('two', new Date())]);
			const c1 = new Contributor();
			c1.fullname = 'Active 1';
			c1.lastCommit = new Date();
			c1.active = true;
			c1.external = false;

			const c2 = new Contributor();
			c2.fullname = 'Inactive 2';
			c2.lastCommit = new Date();
			c2.active = false;
			c2.external = false;

			const e1 = new Contributor();
			e1.fullname = 'External 1';
			e1.lastCommit = new Date();
			e1.active = true;
			e1.external = true;
			
			this.contributors.data.push(...[c1, c2, e1]);
			this.location$.next(["filename"]);
		}

	}

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [TestHostComponent, NodeDetailComponent, ListFilenamesComponent, ListContributorsComponent],
			imports: [
				BrowserAnimationsModule,
				BrowserModule,
				MatTableModule,
				MatSortModule,
				MatButtonToggleModule,
				MatPaginatorModule,
				MatFormFieldModule,
				MatSidenavModule,
				// At this level, should comment the line below to see all entries
				MatExpansionModule,
				MatSelectModule]
		}).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
