import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { QuotationBadgeComponent } from './quotation-badge.component';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { InitTest } from 'src/app/test/init-test';

describe(' QuotationBadgeComponent', () => {
	let component: QuotationBadgeComponent;
	let fixture: ComponentFixture<QuotationBadgeComponent>;

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [QuotationBadgeComponent],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(QuotationBadgeComponent);

		const projectService = TestBed.inject(ProjectService);
		const spy = spyOn(projectService, 'getRiskColor').and.returnValue('blue');

		component = fixture.componentInstance;
		component.evaluation = 10;
		component.title = 'Title of test';
		component.index = 1;
	});

	it('should be sucessfully created for the single metric value (weight <100)', () => {
		component.weight = 10;
		fixture.detectChanges();
		expect(component).toBeTruthy();

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}
		expect(field('#quotationBadge').getAttribute('class')).toEqual('badge metric');
	});

	it('should be sucessfully created for the GLOBAL evaluation (weight = 100)', () => {
		component.weight = 100;
		fixture.detectChanges();
		expect(component).toBeTruthy();

		function field(id: string): HTMLInputElement {
			return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
		}
		expect(field('#quotationBadge').getAttribute('class')).toEqual('badge global');
	});
});
