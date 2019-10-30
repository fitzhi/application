import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SonarQuotationComponent } from './sonar-quotation.component';

describe('SonarQuotationComponent', () => {
  let component: SonarQuotationComponent;
  let fixture: ComponentFixture<SonarQuotationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SonarQuotationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SonarQuotationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
