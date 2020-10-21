import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { ToolbarComponent } from './toolbar.component';
import { InitTest } from '../../test/init-test';
import { RouterTestingModule } from '@angular/router/testing';
import { CinematicService } from '../../service/cinematic.service';
import { Constants } from '../../constants';
import { Component, AfterViewInit } from '@angular/core';
import { Form } from 'src/app/service/Form';

declare var $: any;

describe('ToolbarComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: '<app-toolbar></app-toolbar>'
	})
	class TestHostComponent implements AfterViewInit {
		public ngAfterViewInit () {
		}
	}

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [ToolbarComponent, TestHostComponent],
			providers: [CinematicService],
			imports: [RouterTestingModule.withRoutes([{path: 'user', component: ToolbarComponent}])]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;

		const cinematicService = TestBed.inject(CinematicService);
		cinematicService.currentActiveForm$.next (new Form(Constants.TABS_STAFF_LIST, 'Staff list'));

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
