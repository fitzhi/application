import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ListSkillComponent } from './list-skill.component';
import { RootTestModule } from '../root-test/root-test.module';

describe('ListSkillComponent', () => {
	let component: ListSkillComponent;
	let fixture: ComponentFixture<ListSkillComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ListSkillComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
