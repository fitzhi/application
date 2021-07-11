import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { TreemapProjectsContainerComponent } from './treemap-projects-container.component';


describe('TreemapProjectsContainerComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `<app-treemap-projects>
								</app-treemap-projects>`
	})
	class TestHostComponent {
	}

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TreemapProjectsContainerComponent, TestHostComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TreemapProjectsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
