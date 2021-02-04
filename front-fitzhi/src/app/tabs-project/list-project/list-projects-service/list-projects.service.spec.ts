import { TestBed, inject } from '@angular/core/testing';

import { ListProjectsService } from './list-projects.service';
import { ReferentialService } from '../../../service/referential.service';
import { CinematicService } from '../../../service/cinematic.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatGridListModule } from '@angular/material/grid-list';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';

describe('ListProjectsService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [ListProjectsService, ReferentialService, CinematicService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule,
				RouterTestingModule.withRoutes([])]
		});
	});

	it('should be created', inject([ListProjectsService], (service: ListProjectsService) => {
		expect(service).toBeTruthy();
	}));
});
