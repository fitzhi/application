import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ListFilenamesComponent } from './list-filenames.component';


describe('ListFilenamesComponent', () => {
  let component: ListFilenamesComponent;
  let fixture: ComponentFixture<ListFilenamesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListFilenamesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListFilenamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
