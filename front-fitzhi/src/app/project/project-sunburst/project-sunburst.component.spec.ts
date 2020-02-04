import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectSunburstComponent } from './project-sunburst.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('ProjectSunburstComponent', () => {
	let component: ProjectSunburstComponent;
	let fixture: ComponentFixture<ProjectSunburstComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [],
			imports: [RootTestModule]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ProjectSunburstComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
