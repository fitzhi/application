import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QuotationBadgeComponent } from './quotation-badge.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { ProjectService } from 'src/app/service/project.service';

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

		const projectService = TestBed.get(ProjectService);
		const spy = spyOn(projectService, 'getRiskColor').and.returnValue('blue');

		component = fixture.componentInstance;
		component.evaluation = 10;
		component.title = 'Title of test';
		component.index = 1;
	});

	it('should create for the single metric value (weight <100)', () => {
		component.weight = 10;
		fixture.detectChanges();
		expect(component).toBeTruthy();

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}
		expect(field('#quotationBadge').getAttribute('class')).toEqual('badge metric');
	});

	it('should create for the GLOBAL evaluation (weight = 100)', () => {
		component.weight = 100;
		fixture.detectChanges();
		expect(component).toBeTruthy();

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}
		expect(field('#quotationBadge').getAttribute('class')).toEqual('badge global');
	});
});
