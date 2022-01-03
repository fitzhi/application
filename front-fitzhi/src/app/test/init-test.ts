import { APP_BASE_HREF } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule, MatOptionModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { CinematicService } from '../service/cinematic.service';
import { ReferentialService } from '../service/referential/referential.service';

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
