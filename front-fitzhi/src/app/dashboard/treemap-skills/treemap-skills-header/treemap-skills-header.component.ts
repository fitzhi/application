import { Component, OnInit } from '@angular/core';
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

	constructor(public treeMapService: TreemapSkillsService) {
		this.treeMapService.treemapFilter.external = (localStorage.getItem('external') === '1');
		if (traceOn()) {
			console.log (this.treeMapService.treemapFilter.external ? 'with externals' : 'only internals');
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

}
