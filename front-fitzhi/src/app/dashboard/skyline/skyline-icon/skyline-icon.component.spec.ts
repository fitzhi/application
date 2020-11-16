import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BehaviorSubject } from 'rxjs';
import { SkylineIconComponent } from './skyline-icon.component';


describe('SkylineIconComponent', () => {
  let component: SkylineIconComponent;
  let fixture: ComponentFixture<SkylineIconComponent>;

/*
	@Component({
		selector: 'app-host-component',
    template: `
    <div style="width: 200px; height: 200px;background-color: whiteSmoke">
      <app-skyline-icon
        [selected$]="selected$" 
        [width]="'60px'" 
        [height]="'60px'" 
        (onClick)="switchTo(selection.skyline)" >
      </app-skyline-icon>
    </div>`
	})
	class TestHostComponent {
    public selected$ = new BehaviorSubject<boolean>(false);
    public switchTo(i: number) {
    }
	}
 */
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SkylineIconComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SkylineIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the Skyline icon', async(() => {
    expect(component).toBeTruthy();
  }));
});
