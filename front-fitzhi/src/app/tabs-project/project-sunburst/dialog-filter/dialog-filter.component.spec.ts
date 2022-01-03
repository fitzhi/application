import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { InitTest } from 'src/app/test/init-test';
import { DialogFilterComponent } from './dialog-filter.component';


describe('DialogFilterComponent', () => {

	let component: DialogFilterComponent;
	let fixture: ComponentFixture<DialogFilterComponent>;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [DialogFilterComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {}},
				{ provide: MAT_DIALOG_DATA, useValue: {} },
			],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(DialogFilterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create!', () => {
		expect(component).toBeTruthy();
	});
});
