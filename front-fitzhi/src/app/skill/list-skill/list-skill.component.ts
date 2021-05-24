import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { BaseComponent } from '../../base/base.component';
import { Constants } from '../../constants';
import { CinematicService } from '../../service/cinematic.service';
import { SkillService } from '../service/skill.service';
import { StaffService } from '../../tabs-staff/service/staff.service';
import { TabsStaffListService } from '../../tabs-staff-list/service/tabs-staff-list.service';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { traceOn } from '../../global';
import { SkillCountExperiences } from './skill.count.experiences';
import { ListCriteria } from 'src/app/data/listCriteria';


@Component({
	selector: 'app-list-skill',
	templateUrl: './list-skill.component.html',
	styleUrls: ['./list-skill.component.css']
})
export class ListSkillComponent extends BaseComponent implements OnInit, OnDestroy {

	private experiences: SkillCountExperiences[];

	public dataSource;

	private vide = {};

	public behaviorSubjectCountExperience = new BehaviorSubject(this.vide);

	public displayedColumns: string[] = ['skill', 'level-1', 'level-2', 'level-3', 'level-4', 'level-5'];

	/**
	 * Fake arrays created to iterate with a *ngFor for the rating stars.
	 */
	public fakeArray2 = new Array(2);
	public fakeArray3 = new Array(3);
	public fakeArray4 = new Array(4);
	public fakeArray5 = new Array(5);

	/**
	 * The table of missions is sortable.
	 */
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	constructor(
		private cinematicService: CinematicService,
		private tabsStaffListService: TabsStaffListService,
		private skillService: SkillService,
		private staffService: StaffService,
		private router: Router) {
			super();
		}

	ngOnInit() {
		this.cinematicService.setForm(Constants.SKILLS_SEARCH, this.router.url);

		this.subscriptions.add(
			this.skillService.filteredSkills$.subscribe(skills => {
				this.experiences = [];
				skills.forEach(skill => {
					this.experiences.push(new SkillCountExperiences(skill.id, skill.title));
				});
			})
		);

		this.subscriptions.add(
			this.staffService.peopleCountExperience$.subscribe( {
				next: peopleCountExperience => this.generateDataSource(peopleCountExperience)
			})
		);
	}

	private generateDataSource(peopleCountExperience: Map<string, number>) {
		// Set the aggregation of the number of developers by skill.
		this.experiences.forEach(sce => {
			sce.count_1_star = this.count_n_star (peopleCountExperience, sce.id, 1);
			sce.count_2_star = this.count_n_star (peopleCountExperience, sce.id, 2);
			sce.count_3_star = this.count_n_star (peopleCountExperience, sce.id, 3);
			sce.count_4_star = this.count_n_star (peopleCountExperience, sce.id, 4);
			sce.count_5_star = this.count_n_star (peopleCountExperience, sce.id, 5);
		});

		this.dataSource = new MatTableDataSource(this.experiences);
		this.dataSource.sortingDataAccessor = (item: SkillCountExperiences, property: string) => {
			switch (property) {
				case 'skill':
					return item.title.toLocaleLowerCase();
				case 'level-1':
					return item.count_1_star;
				case 'level-2':
					return item.count_2_star;
				case 'level-3':
					return item.count_3_star;
				case 'level-4':
					return item.count_4_star;
				case 'level-5':
					return item.count_5_star;
			}
		};
		this.dataSource.sort = this.sort;
		this.subscriptions.add(
			this.dataSource.connect().subscribe({
				next: data => this.experiences = data
			})
		);
		if (traceOn()) {
			console.groupCollapsed ('Skills counting');
			this.experiences.forEach(element => {
				console.log (element.title,
				`${element.count_1_star} ${element.count_2_star} ${element.count_3_star} ${element.count_4_star} ${element.count_5_star}`);
			});
			console.groupEnd();
		}
	}

	private count_n_star (peopleCountExperience: Map<string, number>, idSkill: number, level: number): number | string {
		const count_n_star = peopleCountExperience.get(idSkill + '-' + level);
		return (!count_n_star) ? '' : count_n_star;
	}

	/**
	 *  Save the criteria and load the form list
	 * @param title the skill criteria
	 * @param level the level (if any)
	 */
	public listStaff(title: string, level: number) {

		const criteria = `skill:${title}:${level}`;
		this.tabsStaffListService.addTabResult(criteria, this.skillService.criteria.activeOnly);
		const key = this.tabsStaffListService.key(new ListCriteria(criteria, this.skillService.criteria.activeOnly));

		this.tabsStaffListService.activeKey = key;
		if (traceOn()) {
			console.log(`Criteria used ${criteria} for key ${key}`);
		}
		this.router.navigate(['/searchUser/'], {});
	}

	/**
     * Calling the base class to unsubscribe all subscriptions.
     */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
