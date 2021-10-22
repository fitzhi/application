import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { FileService } from 'src/app/service/file.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { StarfieldHeaderComponent } from 'target/classes/app/dashboard/starfield/starfield-header/starfield-header.component';
import { Constellation } from './data/constellation';
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
			providers: [StarfieldService, StaffService, FileService, MessageBoxService],
			imports: [MatDialogModule, HttpClientTestingModule]
		})
		.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
	});

	it('should be correctly created.', () => {
		expect(component).toBeTruthy();
	});

	it('should handle a new version of constellations.', done => {
		expect(document.getElementById('id-0')).toBeNull();
		const constellations = [];
		constellations.push(new Constellation(1, 50, 'black', 'lightGreen'));
		constellations.push(new Constellation(2, 100, 'black', 'lightGrey'));
		const starfieldService = TestBed.inject(StarfieldService);
		starfieldService.broadcastConstellations(constellations);
		fixture.detectChanges();
		setTimeout(() => {
			fixture.detectChanges();
			expect(document.getElementById('star-0')).not.toBeNull();
			expect(document.getElementById('star-149')).not.toBeNull();
			expect(document.getElementById('star-150')).toBeNull();
			done();
		}, 0);
	});
});
