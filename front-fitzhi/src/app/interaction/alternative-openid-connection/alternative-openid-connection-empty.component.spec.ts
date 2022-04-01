import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AuthenticationServer } from 'src/app/data/authentication-server';
import { TypeAuthenticationServer } from 'src/app/data/type-authentication-server';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { AlternativeOpenidConnectionComponent } from './alternative-openid-connection.component';


describe('Empty alternative OpenidConnection Component', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `
			<div style="width: 400px; height: 300px; background-color: lightYellow; margin-top: 30px; margin-left: 100px; padding: 20px">
				<alternative-openid-connection></alternative-openid-connection>
			</div>`})

	class TestHostComponent {
		constructor() {}
	}
 
	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ AlternativeOpenidConnectionComponent, TestHostComponent ],
			providers: [ReferentialService],
			imports: [HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule]

		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		fixture.detectChanges();
	});

	it('should be created without error.', () => {
		expect(component).toBeTruthy();
	});

	it('should NOT display the Google button if Google is NOT registered as a possible authentication server.', () => {
		const btnGoogle = fixture.debugElement.nativeElement.querySelector('#btnGoogle');
		expect(btnGoogle).toBeNull();
	});
});
