import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageBoxService } from './message-box.service';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Component } from '@angular/core';

describe('MessageBoxService', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let messageBoxService: MessageBoxService;

	@Component({
		selector: 'app-host-component',
		template: '<p>test</p>'
	})
	class TestHostComponent {
	}

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [MessageBoxService],
			imports: [MatDialogModule, BrowserAnimationsModule]
		})
		.compileComponents();

		messageBoxService = TestBed.inject(MessageBoxService);

		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should be created successfully.', () => {
		expect(messageBoxService).toBeTruthy();
	});

	it('should display correctly an exclamation message.', () => {
		const ref = messageBoxService.exclamation('my Title', 'my Message');
		fixture.detectChanges();
		expect(ref).toBeTruthy();
		setTimeout(() => { ref.close(); }, 10);
	});

});
