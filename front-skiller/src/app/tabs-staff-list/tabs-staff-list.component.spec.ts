import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TabsStaffListComponent } from './tabs-staff-list.component';

describe('TabsStaffListComponent', () => {
  let component: TabsStaffListComponent;
  let fixture: ComponentFixture<TabsStaffListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TabsStaffListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TabsStaffListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
