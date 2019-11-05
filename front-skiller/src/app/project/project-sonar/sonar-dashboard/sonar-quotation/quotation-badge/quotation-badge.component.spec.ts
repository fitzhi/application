import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotationBadgeComponent } from './quotation-badge.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('QuotationBadgeComponent', () => {
	let component: QuotationBadgeComponent;
	let fixture: ComponentFixture<QuotationBadgeComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [],
			imports: [RootTestModule]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(QuotationBadgeComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
