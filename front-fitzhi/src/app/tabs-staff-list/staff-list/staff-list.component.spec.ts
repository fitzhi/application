import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { InitTest } from 'src/app/test/init-test';
import { StaffListComponent } from './staff-list.component';

describe('StaffListComponent', () => {
	let component: StaffListComponent;
	let fixture: ComponentFixture<StaffListComponent>;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [StaffListComponent],
			providers: [],
			imports: [RouterTestingModule]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should be created without error', () => {
		expect(component).toBeTruthy();
	});

});
