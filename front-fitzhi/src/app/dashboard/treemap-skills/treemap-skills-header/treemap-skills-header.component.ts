import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Subject } from 'rxjs';
import { traceOn } from 'src/app/global';
import { TagifyEditableState } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-editable-state';
import { TagStar } from 'src/app/tabs-staff/staff-form/tag-star';
import { TreemapSkillsService } from '../treemap-skills-service/treemap-skills.service';

@Component({
	selector: 'app-treemap-skills-header',
	templateUrl: './treemap-skills-header.component.html',
	styleUrls: ['./treemap-skills-header.component.css']
})
export class TreemapHeaderComponent implements OnInit {

	/**
	 * Display the Help pane or not.
	 */
	@Output() messengerDisplayHelp = new EventEmitter<boolean>();

	/**
	 * Data expected to be added.
	 */
	public whitelist = [];

	/**
	 * Data prohibited
	 */
	public blacklist = [];

	/**
	 * Original Values which will be added to the component TagifyStar on startup.
	 */
	public originalValues = [];

	/**
	 * Additional Values which might be added to the component TagifyStar.
	 */
	public additionalValues$ = new Subject<TagStar[]>();

	/**
	 * Values to replace the content of he component TagifyStar.
	 */
	public values$ = new Subject<TagStar[]>();

	/**
	 * The tag stars component should be inactive readonly.
	 */
	public editableState$ = new Subject<TagifyEditableState>();

	/**
	 * This boolean is used in an *ngIf to show the the data help pane.
	 */
	public displayData = false;

	/**
	 * This boolean is used in an *ngIf to show the help pane.
	 */
	public displayHelp = true;

	constructor(public treeMapService: TreemapSkillsService) {
		this.treeMapService.treemapFilter.external = (localStorage.getItem('external') === '1');
		if (traceOn()) {
			console.log (this.treeMapService.treemapFilter.external ? 'with externals' : 'only internals');
		}
		this.displayHelp = ( (localStorage.getItem('helpHeight') === null) || (localStorage.getItem('helpHeight') === '110px'));
		if (traceOn()) {
			console.log ( (this.displayHelp) ? 'Display the help pane' : 'Do not display the help pane');
		}
	}

	ngOnInit(): void {
		const label = TreemapSkillsService.TAG_LABEL;
		this.whitelist.push(label);
		this.originalValues.push(this.treeMapService.buildTag());

		setTimeout(() => this.editableState$.next(TagifyEditableState.STARS_ALLOWED), 0);
	}

	onChangeExternal() {
		this.treeMapService.treemapFilter.external = !this.treeMapService.treemapFilter.external;
		localStorage.setItem('external', (this.treeMapService.treemapFilter.external ? '1' : '0'));
		this.treeMapService.filterUpdated$.next(true);
	}

	onAddTagEvent(tagStar: TagStar) {
		if (traceOn()) {
			console.log ('Add event for ' + tagStar.tag + ' ' + tagStar.star);
		}
	}

	onEditTagEvent(tagStar: TagStar) {
		if (traceOn()) {
			console.log ('Edit event for ' + tagStar.tag + ' ' + tagStar.star);
		}
		this.treeMapService.treemapFilter.level = tagStar.star;
		this.treeMapService.filterUpdated$.next(true);
	}

	onRemoveTagEvent(tag: string) {
		throw Error('SHOULD NOT PASS HERE !');
	}

	data() {
		this.displayData = !this.displayData;
		this.treeMapService.displaySettings(this.displayData);
	}

	help() {
		this.displayHelp = !this.displayHelp;
		this.messengerDisplayHelp.emit(this.displayHelp);
	}

}
