import { Component, Input, OnInit } from '@angular/core';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { MessageService } from 'src/app/interaction/message/message.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { Constants } from 'src/app/constants';
import { Collaborator } from 'src/app/data/collaborator';
import { EMPTY, BehaviorSubject } from 'rxjs';

@Component({
	selector: 'app-staff-remove',
	templateUrl: './staff-remove.component.html',
	styleUrls: ['./staff-remove.component.css']
})
export class StaffRemoveComponent implements OnInit {

	/**
	 * Selected TAB.
	 * This observable is fired by the StaffComponent class when the user changes the tab selected.
	 */
	@Input() selectedTab$ = new BehaviorSubject<number>(0);

	constructor(
			public staffListService: StaffListService,
			public staffService: StaffService,
			public messageService: MessageService,
			public cinematicService: CinematicService) { }

	ngOnInit() {
	}

	removeStaff() {
		this.staffService.removeStaff$().subscribe({
			next: () => {
				// We remove the selected Project from the projects set
				const indexToDelete = this.staffListService.findIndex(this.staffService.collaborator.idStaff);
				if (indexToDelete === -1) {
					throw new Error ('WTF : Should not pass here !');
				}
				this.staffListService.allStaff.splice(indexToDelete, 1);

				// We reinitialize the tab form sequence.
				this.staffService.collaborator = this.staffService.emptyStaff();
				this.staffService.collaboratorLoaded$.next(true);
				// this.cinematicService.projectTabIndex = Constants.PROJECT_IDX_TAB_FORM;
				this.selectedTab$.next(Constants.PROJECT_IDX_TAB_FORM);

				this.messageService.success('Staff member successfully removed!');
			}
		});

}

}

