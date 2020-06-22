import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CiaoComponent } from './ciao.component';

describe('CiaoComponent', () => {
  let component: CiaoComponent;
  let fixture: ComponentFixture<CiaoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CiaoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CiaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
