import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { FileService } from 'src/app/service/file.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { Star } from '../data/star';
import { StarfieldService } from '../service/starfield.service';

import { StarfieldHeaderComponent } from './starfield-header.component';

describe('StarfieldHeaderComponent', () => {
	let component: StarfieldHeaderComponent;
	let fixture: ComponentFixture<StarfieldHeaderComponent>;
	let starfieldService: StarfieldService;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [ StarfieldHeaderComponent ],
			providers: [StarfieldService, StaffService, FileService, MessageBoxService],
			imports: [MatDialogModule, HttpClientTestingModule, MatCheckboxModule]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(StarfieldHeaderComponent);
		component = fixture.componentInstance;
		starfieldService = TestBed.inject(StarfieldService);
		fixture.detectChanges();
	});

	it('should be correctly created.', () => {
		expect(component).toBeTruthy();
	});

	it('should take in account every new version of the array of stars.', done => {
		expect(document.getElementById('numberOfStars').innerText).toBe('0');
		const stars = [];
		stars.push(new Star(1, 1));
		starfieldService.broadcastStars(stars);
		fixture.detectChanges();
		setTimeout(() => {
			expect(document.getElementById('numberOfStars').innerText).toBe('1');
			done();
		}, 0);

	});

	it('should broadcast a new version of the constellations if the user checks, or unchecks, the external checkbox.', fakeAsync(() => {
		const spy1 = spyOn(starfieldService, 'switchExternalFilter').and.callThrough();
		const spy2 = spyOn(starfieldService, 'generateAndBroadcastConstellations');
		const cbExternal = fixture.debugElement.query(By.css('#external'));
		cbExternal.triggerEventHandler('change', null);
		tick(); // simulates the passage of time until all pending asynchronous activities finish
		expect(spy1).toHaveBeenCalled();
		expect(spy2).toHaveBeenCalled();
	}));

});
