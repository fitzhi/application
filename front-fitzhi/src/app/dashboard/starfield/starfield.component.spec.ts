import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';
import { Skill } from 'src/app/data/skill';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { FileService } from 'src/app/service/file.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { Constellation } from './data/constellation';
import { Star } from './data/star';
import { StarfieldService } from './service/starfield.service';
import { StarfieldContentComponent } from './starfield-content/starfield-content.component';
import { StarfieldHeaderComponent } from './starfield-header/starfield-header.component';
import { StarfieldComponent } from './starfield.component';


describe('StarfieldComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	function generateConstellations() {
		const constellations = [];
		// #28a745
		constellations.push(new Constellation(1, 50, 'var(--color-success)', 'transparent'));
		constellations.push(new Constellation(2, 100, 'var(--color-error)', 'transparent'));
		return constellations;
	}

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
			providers: [StarfieldService, StaffService, FileService, MessageBoxService, SkillService],
			imports: [MatDialogModule, HttpClientTestingModule]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		const skillService = TestBed.inject(SkillService);
		skillService.allSkills = [];
		skillService.allSkills.push (new Skill(1, "Java"));
		skillService.allSkills.push (new Skill(2, "Typescript"));
	});
	it('should be correctly created.', () => {
		expect(component).toBeTruthy();
	});

	it('should handle a new version of constellations.', done => {

		expect(document.getElementById('id-0')).toBeNull();
		const constellations = generateConstellations();
		const starfieldService = TestBed.inject(StarfieldService);
		starfieldService.broadcastConstellations(constellations);
		fixture.detectChanges();

		setTimeout(() => {
			fixture.detectChanges();
			expect(document.getElementById('star-0')).not.toBeNull();
			expect(document.getElementById('star-0').parentElement.style.cssText).toContain('--color-success');
			expect(document.getElementById('star-149')).not.toBeNull();
			expect(document.getElementById('star-149').parentElement.style.cssText).toContain('--color-error');
			expect(document.getElementById('star-150')).toBeNull();
			done();
		}, 0);
	});

	it('should handle the mouse move on the starfield.', done => {

		const constellations = generateConstellations();
		const starfieldService = TestBed.inject(StarfieldService);
		starfieldService.broadcastConstellations(constellations);
		fixture.detectChanges();

		const div = fixture.debugElement.query(By.css('#star-20')).parent;
		div.triggerEventHandler('mouseenter', new Star(20, 50, 'var(--color-success)', 'transparent'));
		fixture.detectChanges();

		expect(div.nativeElement.style.cssText).toContain('background-color: lightgrey');
		div.triggerEventHandler('mouseleave', new Star(20, 50, 'var(--color-success)', 'transparent'));
		fixture.detectChanges();

		expect(div.nativeElement.style.cssText).toContain('background-color: transparent');
		done();
	});

	it('should contextually display the detail panel for a skill in the starfield.', done => {

		const constellations = generateConstellations();
		const starfieldService = TestBed.inject(StarfieldService);
		starfieldService.broadcastConstellations(constellations);
		fixture.detectChanges();

		const detailSkillPanel = fixture.debugElement.query(By.css('#detail-skill'));
		expect(detailSkillPanel).toBeNull();
		const div = fixture.debugElement.query(By.css('#star-10')).parent;

		div.triggerEventHandler('mouseenter', new Star(10, 50, 'var(--color-success)', 'transparent'));
		fixture.detectChanges();
		expect(detailSkillPanel).toBeDefined();

		div.triggerEventHandler('mouseleave', new Star(10, 50, 'var(--color-success)', 'transparent'));
		fixture.detectChanges();
		expect(detailSkillPanel).toBeNull();

		done();
	});

});
