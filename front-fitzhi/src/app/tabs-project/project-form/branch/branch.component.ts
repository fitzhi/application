import { Component, OnInit, EventEmitter, Output, OnDestroy } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';
import { traceOn } from 'src/app/global';
import { BaseDirective } from 'src/app/base/base-directive.directive';

@Component({
	selector: 'app-branch-selector',
	templateUrl: './branch.component.html',
	styleUrls: ['./branch.component.css']
})
export class BranchComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * We'll send to the parent component that the selected branch has been changed.
	 */
	@Output() messengerOnBranchChange = new EventEmitter<string>();

	constructor(public projectService: ProjectService) {
		super();
	}

	ngOnInit(): void {
		this.subscriptions.add(
			this.projectService.branches$.subscribe({
				next: branches => {
					if (traceOn()) {
						console.groupCollapsed ('List of branches received by the BranchComponent');
						branches.forEach(branch => console.log( branch));
						console.groupEnd();
					}
				}
			})
		);
	}

	/**
	 * End-user has selected a branch name.
	 * @param $event the select widget when a specific branch has been selected
	 */
	onBranchChange($event) {
		if (traceOn()) {
			console.log ('selection has changed to', $event.target.value);
		}
		this.messengerOnBranchChange.emit($event.target.value);
	}

	selectBranch(branch: string) {
		if (traceOn()) {
			console.log ('Set the selected branch to', this.projectService.project.branch);
		}

	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
