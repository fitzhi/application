import { TestBed } from '@angular/core/testing';
import { doesNotReject } from 'assert';

import { InstallService } from './install.service';

describe('InstallService', () => {
  let service: InstallService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InstallService);
    localStorage.removeItem('installation')
  });

  it('The InstallService should be instantiated without error.', () => {
    expect(service).toBeTruthy();
  });

  it('The method installComplete() should store the completion into the local storage.', () => {
    expect(service).toBeTruthy();
		expect(localStorage.getItem('installation')).toBeNull();
    service.installComplete();
		expect(localStorage.getItem('installation')).toBe("1");
  });

  it('The method installComplete() should emit a TRUE on the behaviorSubject installComplete$.', done => {
    expect(service).toBeTruthy();
    service.installComplete();
    service.installComplete$.subscribe({
      next: ok => {
        expect(ok).toBeTruthy();
        done();
      }
    });
  });
  
});
