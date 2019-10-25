import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageBoxComponent } from './message-box.component';
import { MatDialogModule } from '@angular/material/dialog';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('MessageBoxComponent', () => {
	let component: MessageBoxComponent;
	let fixture: ComponentFixture<MessageBoxComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [
				MatDialogModule,
				RootTestModule ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(MessageBoxComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create!', () => {
		expect(component).toBeTruthy();
	});
});
