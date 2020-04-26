import { TestBed } from '@angular/core/testing';

import { AccountRequestService } from './account-request.service';

describe('AccountRequestService', () => {
  let service: AccountRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
