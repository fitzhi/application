import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { environment } from 'src/environments/environment';
import { AuthService } from './admin/service/auth/auth.service';
import { InstallService } from './admin/service/install/install.service';
import { AppComponent } from './app.component';
import { MessageComponent } from './interaction/message/message.component';
import { ToolbarComponent } from './interaction/toolbar/toolbar.component';
import { CinematicService } from './service/cinematic.service';
import { ReferentialService } from './service/referential/referential.service';


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

	it('The toolbar should NOT exist until user is connected ' + environment.autoConnect, waitForAsync(() => {
		const spy = spyOn(authService, 'isConnected').and.returnValue(false);
		const fixture = TestBed.createComponent(AppComponent);
		fixture.detectChanges();
		expect(document.getElementById('toolbar')).toBeNull();
	}));

	it('The toolbar should NOT exist if user is connected BUT the installation is not complete and successful.' + environment.autoConnect,
		fakeAsync(() => {
			const spy = spyOn(authService, 'isConnected').and.returnValue(true);
			installService.uninstall();
			const fixture = TestBed.createComponent(AppComponent);
			fixture.detectChanges();
			tick();
			expect(document.getElementById('toolbar')).toBeNull();
		})
	);

	it('The toolbar should exist if user is connected AND the installation is complete and successful.' + environment.autoConnect,
		waitForAsync(() => {
			const spy = spyOn(authService, 'isConnected').and.returnValue(true);
			installService.installComplete();
			const fixture = TestBed.createComponent(AppComponent);
			fixture.detectChanges();
			expect(document.getElementById('toolbar')).toBeDefined();
		})
	);

});
