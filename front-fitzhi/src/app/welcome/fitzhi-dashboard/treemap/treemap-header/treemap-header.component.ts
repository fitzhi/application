import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { TagStar } from 'src/app/tabs-staff/staff-form/tag-star';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { thresholdSturges } from 'd3';
import { traceOn } from 'src/app/global';
import { TagifyEditableState } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-editable-state';
import { TagifyStarsComponent } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { TreemapService } from '../service/treemap.service';

@Component({
	selector: 'app-treemap-header',
	templateUrl: './treemap-header.component.html',
	styleUrls: ['./treemap-header.component.css']
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

	constructor(private treeMapService: TreemapService) {}

	ngOnInit(): void {
		this.treeMapService.treemapFilter.external = (localStorage.getItem('external') === '1');
		const label = TreemapService.TAG_LABEL;
		this.whitelist.push(label);
		this.originalValues.push(this.treeMapService.buildTag());

		setTimeout(() => this.editableState$.next(TagifyEditableState.STARS_ALLOWED), 0);
	}

	onChangeExternal() {
		this.treeMapService.treemapFilter.external = !this.treeMapService.treemapFilter.external;
		localStorage.setItem('external', (this.treeMapService.treemapFilter.external ? '1' : '0'));
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
		this.values$.next([this.treeMapService.buildTag()]);
	}

	onRemoveTagEvent(tag: string) {
		if (traceOn()) {
			console.log ('Remove tag ' + tag);
		}
	}

}
