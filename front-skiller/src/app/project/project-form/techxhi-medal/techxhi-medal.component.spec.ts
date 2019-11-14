import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TechxhiMedalComponent } from './techxhi-medal.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('TechxhiMedalComponent', () => {
	let component: TechxhiMedalComponent;
	let fixture: ComponentFixture<TechxhiMedalComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TechxhiMedalComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
