import { TestBed, tick, fakeAsync, waitForAsync } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { ToolbarComponent } from './interaction/toolbar/toolbar.component';

import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from './service/referential/referential.service';
import { CinematicService } from './service/cinematic.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatGridListModule } from '@angular/material/grid-list';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { MessageComponent } from './interaction/message/message.component';
import { InstallService } from './admin/service/install/install.service';
import { AuthService } from './admin/service/auth/auth.service';

describe('AppComponent', () => {

	let authService: AuthService;
	let installService: InstallService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [AppComponent, ToolbarComponent, MessageComponent],
			providers: [ReferentialService, CinematicService, InstallService, AuthService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule,
				RouterTestingModule.withRoutes([])]
		}).compileComponents();

		authService = TestBed.inject(AuthService);
		installService = TestBed.inject(InstallService);
	}));

	it('The App component should be created without error', waitForAsync(() => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.debugElement.componentInstance;
		expect(app).toBeTruthy();
	}));

	it('The toolbar should NOT exist until user is connected', waitForAsync(() => {
		const spy = spyOn(authService, 'isConnected').and.returnValue(false);
		const fixture = TestBed.createComponent(AppComponent);
		fixture.detectChanges();
		expect(document.getElementById('toolbar')).toBeNull();
	}));

	it('The toolbar should NOT exist if user is connected BUT the installation is complete and successful.', fakeAsync(() => {
		const spy = spyOn(authService, 'isConnected').and.returnValue(true);
		installService.uninstall();
		const fixture = TestBed.createComponent(AppComponent);
		fixture.detectChanges();
		tick();
		expect(document.getElementById('toolbar')).toBeNull();
	}));

	it('The toolbar should exist if user is connected AND the installation is complete and successful.', waitForAsync(() => {
		const spy = spyOn(authService, 'isConnected').and.returnValue(true);
		installService.installComplete();
		const fixture = TestBed.createComponent(AppComponent);
		fixture.detectChanges();
		expect(document.getElementById('toolbar')).toBeDefined();
	}));

});
