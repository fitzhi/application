import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchSkillComponent } from './search-skill.component';

describe('SearchSkillComponent', () => {
  let component: SearchSkillComponent;
  let fixture: ComponentFixture<SearchSkillComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchSkillComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchSkillComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
