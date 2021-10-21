import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StarfieldHeaderComponent } from 'target/classes/app/dashboard/starfield/starfield-header/starfield-header.component';
import { Constellation } from './data/constellation';
import { Star } from './data/star';
import { StarfieldService } from './service/starfield.service';
import { StarfieldContentComponent } from './starfield-content/starfield-content.component';

import { StarfieldComponent } from './starfield.component';

describe('StarfieldComponent', () => {
  let component: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: `
	  <div style="width: 1200px; height: 800px; background-color: whiteSmoke">
		<app-starfield></app-starfield>
	  </div>`})

	class TestHostComponent {
	}

  beforeEach(async () => {
	await TestBed.configureTestingModule({
	  declarations: [ TestHostComponent, StarfieldComponent, StarfieldContentComponent, StarfieldHeaderComponent],
	  providers: [StarfieldService]
	})
	.compileComponents();
  });

  beforeEach(() => {
	fixture = TestBed.createComponent(TestHostComponent);
	component = fixture.componentInstance;

	const constellations = [];
	constellations.push(new Constellation(1, 50, 'black', 'lightGreen'));
	constellations.push(new Constellation(2, 100, 'black', 'lightGrey'));
	const starfieldService = TestBed.inject(StarfieldService);
	starfieldService.emit(constellations);

	fixture.detectChanges();
  });

  it('should create', () => {
	expect(component).toBeTruthy();
  });
});
