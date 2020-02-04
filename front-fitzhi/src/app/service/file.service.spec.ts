import { TestBed } from '@angular/core/testing';
import { RootTestModule } from '../root-test/root-test.module';
import { Project } from '../data/project';
import { ProjectService } from './project.service';
import { FileService } from './file.service';


describe('FileService', () => {

	let service;

	beforeEach(() => TestBed.configureTestingModule({
		imports: [RootTestModule]
	}));

	it('FileService should be correctly created', () => {
		service = TestBed.get(FileService);
		expect(service).toBeTruthy();
	});

	it('Testing extractFilename() ', () => {
		const filename = service.extractFilename('./test/mock Fred/Frédéric VIDAL.docx');
		expect('Frédéric VIDAL.docx').toEqual(filename);
	});

});
