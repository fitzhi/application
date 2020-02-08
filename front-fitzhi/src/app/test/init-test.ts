import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, MatOptionModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatTableModule } from '@angular/material/table';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCheckbox, MatCheckboxModule } from '@angular/material/checkbox';
import { MatSortModule } from '@angular/material/sort';
import { ReferentialService } from '../service/referential.service';
import { CinematicService } from '../service/cinematic.service';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterTestingModule } from '@angular/router/testing';
import { APP_BASE_HREF } from '@angular/common';

export class InitTest {

	public static addProviders(providers: any[]): void {
		providers.push(ReferentialService, CinematicService, {provide: APP_BASE_HREF, useValue: '/my/app'});
	}

	public static addImports(imports: any[]): void {
		imports.push(
			FormsModule, ReactiveFormsModule,
			MatDatepickerModule, MatNativeDateModule,
			MatInputModule, MatFormFieldModule, MatOptionModule, MatSelectModule, MatCheckboxModule,
			MatDialogModule,
			BrowserAnimationsModule,
			MatTableModule, MatPaginatorModule, MatSortModule,
			HttpClientTestingModule,
			MatExpansionModule,
			MatProgressBarModule,
			MatTabsModule,
			RouterTestingModule.withRoutes([])
		);
	}
}
