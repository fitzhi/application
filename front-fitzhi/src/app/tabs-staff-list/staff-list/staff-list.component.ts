import { Component, OnInit, Input, OnDestroy, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { TabsStaffListService } from '../service/tabs-staff-list.service';
import { BaseDirective } from '../../base/base-directive.directive';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Constants } from '../../constants';
import { Profile } from '../../data/profile';
import { ReferentialService } from '../../service/referential/referential.service';
import { Experience } from '../../data/experience';
import { ListCriteria } from '../../data/listCriteria';
import { SkillService } from '../../skill/service/skill.service';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { UserSetting } from 'src/app/base/user-setting';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { CinematicService } from 'src/app/service/cinematic.service';

@Component({
	selector: 'app-staff-list',
	templateUrl: './staff-list.component.html',
	styleUrls: ['./staff-list.component.css']
})
export class StaffListComponent extends BaseDirective implements OnInit, OnDestroy {

	@Input() criteria: string;

	@Input() activeOnly: boolean;

	public dataSource: MatTableDataSource<Collaborator>;

	/**
	 * The paginator for the Staff list.
	 */
	@ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

	/**
	* The sort component for the Staff list
	*/
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	/**
	 * The columns of the table
	 */
	public displayedColumns: string[] = ['firstName lastName', 'level', 'skills', 'active', 'external'];

	/**
	 * Profiles array retrieved from the referential.
	 */
	private profiles: Profile[];

	/**
	 * Key used to save the page size in the local storage.
	 */
	public pageSize = new UserSetting('staff-list.pageSize', 10);

	constructor(
		private tabsStaffListComponent: TabsStaffListService,
		private cinematicService: CinematicService,
		private referentialService: ReferentialService,
		private skillService: SkillService,
		private router: Router) {
		super();
	}

	ngOnInit() {
		if (traceOn()) {
			console.log('Searching staff members for criteria:' + this.criteria + ', activeOnly:' + this.activeOnly);
		}

		this.profiles = this.referentialService.profiles;

		this.subscriptions.add(
			this.tabsStaffListComponent
				.search$(this.criteria, this.activeOnly)
				.subscribe(collaborators => {
					this.dataSource = new MatTableDataSource<Collaborator>(collaborators);
					this.dataSource.sortingDataAccessor = (item: Collaborator, property: string) => {
						switch (property) {
							case 'firstName lastName':
								return item.firstName.toLocaleLowerCase() + ' ' + item.lastName.toLocaleLowerCase();
							case 'active':
								console.log (property, (item.active) ? '1' : '0');
								return (item.active) ? '1' : '0';
							case 'level':
								return this.title(item.level);
							case 'skills':
								return this.skills(item.experiences);
							case 'external':
								console.log (property, (item.external) ? '1' : '0');
								return (item.external) ? '1' : '0';
						}
					};
					this.dataSource.sort = this.sort;
					this.dataSource.paginator = this.paginator;
				}));
		const key = this.tabsStaffListComponent.key(new ListCriteria(this.criteria, this.activeOnly));
		const context = this.tabsStaffListComponent.getContext(key);
		if ((context) && (context.isSorted())) {
			this.sort.sort(context.getSortConfiguration());
		}
	}

	sortData(event: Sort) {
		const key = this.tabsStaffListComponent.key(new ListCriteria(this.criteria, this.activeOnly));
		const context = this.tabsStaffListComponent.getContext(key);
		context.storeSortingContext(event.active, event.direction);
	}

	/**
	 * Return the CSS class corresponding to the active vs inactive status of a developer.
	 */
	public class_active_inactive(active: boolean) {
		return active ? 'contributor_active' : 'contributor_inactive';
	}

	public routeStaff(idStaff: number) {
		this.router.navigate(['/user/' + idStaff], {});
		setTimeout(() => {
			this.cinematicService.masterDetailSubject$.next(true);
		}, 0);
	}

	/**
	 * @param levelCode code of level of a collaborator
	 * @returns  the title corresponding to the code
	 */
	public title(levelCode: string) {
		const found = this.profiles.find(profile => (profile.code === levelCode));
		if (typeof found === 'undefined') {
			return '';
		} else {
			return found.title;
		}
	}

	public skills(experiences: Experience[]): string {
		let mainSkills = '';
		let evaluatedSkill: Experience[];
		for (let level = Constants.LEVEL_Expert; (level >= Constants.LEVEL_Beginner); level--) {
			evaluatedSkill = experiences.filter(expe => (expe.level === level));
			evaluatedSkill.forEach(experience => { mainSkills += this.skillService.title(experience.id) + ', '; });
		}
		return mainSkills;
	}

	/**
	 * This method is invoked if the user change the page size.
	 * @param $pageEvent event
	 */
	public page($pageEvent: PageEvent) {
		this.pageSize.saveSetting($pageEvent.pageSize);
	}


	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
