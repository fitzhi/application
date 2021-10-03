import { ApplicationRef, ChangeDetectorRef, Component, HostBinding, Input, OnInit } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';
import { TreemapSkillsService } from '../treemap-skills-service/treemap-skills.service';

@Component({
	selector: 'app-treemap-skills',
	templateUrl: './treemap-skills.component.html',
	styleUrls: ['./treemap-skills.component.css']
})
export class TreemapSkillsComponent implements OnInit {

	/**
	 * Height of the help pane in the treemap Skills component. 
	 */
	@HostBinding('style.--help-height')
	@Input() helpHeight = '110px';

	helpPaneVisible = true;

	constructor(
		public projectService: ProjectService, 
		public treemapSkillsService: TreemapSkillsService) {
		const localHelpHeight = localStorage.getItem('helpHeight');
		if (localHelpHeight) {
			this.helpHeight = localHelpHeight;
		}
		this.helpPaneVisible =  (this.helpHeight === '110px');
		console.log ('this.helpPaneVisible', this.helpPaneVisible);
	}

	ngOnInit() {
	}

	public displayHelp($event: boolean) {
		this.helpHeight = (!$event) ? '0' : '110px';
		localStorage.setItem('helpHeight', this.helpHeight);
		this.treemapSkillsService.filterUpdated$.next(true);
	}
}
