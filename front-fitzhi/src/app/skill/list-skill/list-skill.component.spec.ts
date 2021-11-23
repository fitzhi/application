import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Skill } from 'src/app/data/skill';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { CinematicService } from '../../service/cinematic.service';
import { ReferentialService } from '../../service/referential/referential.service';
import { SkillService } from '../service/skill.service';
import { ListSkillComponent } from './list-skill.component';

describe('ListSkillComponent', () => {
	let component: ListSkillComponent;
	let fixture: ComponentFixture<ListSkillComponent>;
	let skillService: SkillService;
	let staffService: StaffService;

	@Component({
		selector: 'app-host-component',
		template: 	`	<p></p>
					`
	})
	class TestHostComponent {
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ ListSkillComponent],
			providers: [ReferentialService, CinematicService, StaffService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule,
				RouterTestingModule.withRoutes([{ path: 'skill/1', component: TestHostComponent}])]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ListSkillComponent);
		component = fixture.componentInstance;

		skillService = TestBed.inject(SkillService);
		const sk1 = new Skill(1, 'One');
		const sk2 = new Skill(2, 'Two');
		skillService.filteredSkills$.next([sk1, sk2]);

		staffService = TestBed.inject(StaffService);
		staffService.peopleCountExperience$.next(new Map());
		fixture.detectChanges();
	});

	it('should list correctly the Staff experiences aggregation', done => {
		expect(component).toBeTruthy();

		expect(fixture.debugElement.query(By.css('#idSkill-1'))).toBeNull();
		expect(fixture.debugElement.query(By.css('#idSkill-2'))).toBeNull();
		expect(fixture.debugElement.query(By.css('#count_1_star-1'))).toBeNull();

		staffService = TestBed.inject(StaffService);
		const map = new Map();
		map.set('1-1', 1789);
		map.set('2-5', 1805);
		staffService.peopleCountExperience$.next(map);
		fixture.detectChanges();

		expect(fixture.debugElement.query(By.css('#idSkill-1'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#idSkill-2'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#count_1_star-1'))).toBeDefined();
		expect(fixture.debugElement.query(By.css('#count_1_star-1')).nativeNode.innerText).toBe('1789');
		expect(fixture.debugElement.query(By.css('#count_5_star-2')).nativeNode.innerText).toBe('1805');

		done();
	});

	it('should route the user to the skill form if he clicks on the skill-title.', done => {
		staffService = TestBed.inject(StaffService);
		const map = new Map();
		map.set('1-1', 1789);
		map.set('2-5', 1805);
		staffService.peopleCountExperience$.next(map);
		fixture.detectChanges();

		const skill1 = fixture.debugElement.query(By.css('#idSkill-1')).nativeElement;
		expect(skill1.getAttribute('href')).toBe('/skill/1');

		const spy = spyOn(component, 'listStaff').and.returnValue(null);
		let skillLevel = fixture.debugElement.query(By.css('#count_1_star-1')).nativeElement;
		skillLevel.click();
		expect(spy).toHaveBeenCalledWith('One', 1);

		skillLevel = fixture.debugElement.query(By.css('#count_5_star-2')).nativeElement;
		skillLevel.click();
		expect(spy).toHaveBeenCalledWith('Two', 5);

		done();

	});
});
