import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { SkylineIconComponent } from './skyline-icon.component';


describe('SkylineIconComponent', () => {
	let component: SkylineIconComponent;
	let fixture: ComponentFixture<SkylineIconComponent>;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ SkylineIconComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(SkylineIconComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create the Skyline icon', waitForAsync(() => {
		expect(component).toBeTruthy();
	}));
});
