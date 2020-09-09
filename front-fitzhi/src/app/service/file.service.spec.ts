import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { Project } from '../data/project';
import { ProjectService } from './project.service';
import { FileService } from './file.service';
import { MatDialogModule } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { InitTest } from '../test/init-test';


describe('FileService', () => {

	let service: FileService;

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [FileService],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		service = TestBed.get(FileService);
	});

	it('FileService should be correctly created', () => {
		service = TestBed.get(FileService);
		expect(service).toBeTruthy();
	});

	it('Testing extractFilename() ', () => {

		const appVer = navigator.appVersion;
		console.log ('appVer', appVer);
		const osDependentFileName =  (appVer.indexOf('Win') === -1) ?
			'./test/mock Fred/Frédéric VIDAL.docx' : 
			'.\\test\\mock Fred\\Frédéric VIDAL.docx'; 
		console.log ('test name for file depending on the os', osDependentFileName);
		const filename = service.extractFilename(osDependentFileName);
		expect('Frédéric VIDAL.docx').toEqual(filename);
	});

});
