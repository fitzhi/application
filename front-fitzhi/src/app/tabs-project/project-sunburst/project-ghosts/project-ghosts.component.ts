import { Component, Input, OnDestroy } from '@angular/core';
import { BaseDirective } from '../../../base/base-directive.directive';

@Component({
	selector: 'app-project-ghosts',
	templateUrl: './project-ghosts.component.html',
	styleUrls: ['./project-ghosts.component.css']
})
export class ProjectGhostsComponent extends BaseDirective implements OnDestroy {

	/**
	 * Observable to the active project.
	 */
	@Input() dataSourceGhosts$;

	constructor() {
		super();
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
