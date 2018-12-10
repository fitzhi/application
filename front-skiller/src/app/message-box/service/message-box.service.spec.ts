import { TestBed } from '@angular/core/testing';

import { MessageBoxService } from './message-box.service';

describe('MessageBoxService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: MessageBoxService = TestBed.get(MessageBoxService);
    expect(service).toBeTruthy();
  });
});
