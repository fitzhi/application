import { TestBed, TestModuleMetadata } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from './referential.service';
import { InitTest } from 'src/app/test/init-test';


describe('ReferentialService', () => {

	let service: ReferentialService;

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [ReferentialService],
			imports: [HttpClientTestingModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	});

	beforeEach(() => {
		service = TestBed.inject(ReferentialService);
	});

	it('should be correctly created.', () => {
		expect(service).toBeTruthy();
	});

});
