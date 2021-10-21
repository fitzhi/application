import { ComponentFixture, TestBed } from '@angular/core/testing';
import { doesNotReject } from 'assert';
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
			providers: [StarfieldService]
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
		stars.push(new Star(1));
		starfieldService.broadcastStars(stars);
		fixture.detectChanges();
		setTimeout(() => {
			expect(document.getElementById('numberOfStars').innerText).toBe('1');
			done();
		}, 0);

	});
});
