import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieProjectsComponent } from './pie-projects.component';
import { MatTableModule } from '@angular/material/table';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { RouterTestingModule } from '@angular/router/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';

describe('PieProjectsComponent', () => {
	let component: PieProjectsComponent;
	let fixture: ComponentFixture<PieProjectsComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ PieProjectsComponent ],
			imports: [MatTableModule, HttpClientTestingModule, RouterTestingModule, MatDialogModule],
			providers: [ReferentialService, CinematicService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(PieProjectsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
