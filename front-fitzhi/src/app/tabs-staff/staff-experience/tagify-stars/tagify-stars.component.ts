import { Component, OnInit, Input, AfterViewInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import Tagify from '@yaireo/tagify';
import { TagStar } from '../tag-star';
import { Subject, BehaviorSubject, Subscription } from 'rxjs';
import { TagifyEditableState } from './tagify-editable-state';

@Component({
	selector: 'app-tagify-stars',
	templateUrl: './tagify-stars.component.html',
	styleUrls: ['./tagify-stars.component.css']
})
export class TagifyStarsComponent implements AfterViewInit, OnDestroy {

	/**
	 * Array of subscriptions activated on the child component.
	 */
	subscriptions: Subscription = new Subscription();

	/**
	 * The whitelist of tags
	 */
	@Input() whitelist = [];

	/**
	 * The backlist of tags
	 */
	@Input() blacklist = [];

	/**
	 * First values to put inside the component.
	 */
	@Input() originalValues;

	/**
	 * New values to replace the current content inside the component.
	 */
	@Input() values$: Subject<TagStar[]>;

	/**
	 * Additional data to be added in the component.
	 */
	@Input() additionalValues$: Subject<TagStar[]>;

	/**
	 * Observable hosting the edition state for the component.
	 */
	@Input() editableState$: BehaviorSubject<TagifyEditableState>;

	/**
	 * The placeholder to be displayed in the component
	 */
	@Input() placeholder;

	@Input() colorON;

	@Input() colorOFF;

	@Output() addTagEvent = new EventEmitter<TagStar>();

	@Output() editTagEvent = new EventEmitter<TagStar>();

	@Output() removeTagEvent = new EventEmitter<string>();

	tagify: Tagify;

	boundOnAddTag: any;

	boundOnRemoveTag: any;

	input: any;

	/**
	 * Editable state.
	 */
	public editableState = TagifyEditableState.READ_ONLY;

	// Array of eventHandler bound the the tagigy-stars component.
	// The goal of these eventHandler is to catch & save the selected star.
	boundOnClick = new Array(5);

	// The selected star
	public star: number;

	ngAfterViewInit() {

		this.input = document.getElementById('tagify-stars');

		this.tagify = new Tagify(this.input, {
			enforceWhitelist: true,
			placeholder: this.placeholder,
			readOnly: true,
			templates: {
				wrapper(input, settings) {
					return `<tags
				class="tagify ${settings.mode ? 'tagify--mix' : ''} ${input.className}" ${settings.readonly ? 'readonly' : ''}
				style="min-height:40px">
				<span id="tag-input" contenteditable data-placeholder="${settings.placeholder}" class="tagify__input"></span></tags>`;
				},
				tag(v, tagData) {
					return `<tag title='${v}' contenteditable='false' spellcheck="false" class='tagify__tag
			  ${tagData.class ? tagData.class : ''}' ${this.getAttributes(tagData)}>
				<x title='' class='tagify__tag__removeBtn'></x>
				<div style="background-color:lightGrey" ><span class='tagify__tag-text'>
				  ${v}
				  <i class="fas fa-star" id='tag-star-${v}-0'></i>
				  <i class="fas fa-star" id='tag-star-${v}-1'></i>
				  <i class="fas fa-star" id='tag-star-${v}-2'></i>
				  <i class="fas fa-star" id='tag-star-${v}-3'></i>
				  <i class="fas fa-star" id='tag-star-${v}-4'></i>
				  </span>
				</div>
				</tag>`;
				}
			}
		}
		);

		this.tagify.settings.whitelist = [];
		this.whitelist.forEach(element => this.tagify.settings.whitelist.push(element));
		this.tagify.settings.blacklist = [];
		this.blacklist.forEach(element => this.tagify.settings.blacklist.push(element));
		this.tagify.settings.placeholder = '';

		this.boundOnClick[0] = this.onClick_0.bind(this);
		this.boundOnClick[1] = this.onClick_1.bind(this);
		this.boundOnClick[2] = this.onClick_2.bind(this);
		this.boundOnClick[3] = this.onClick_3.bind(this);
		this.boundOnClick[4] = this.onClick_4.bind(this);

		this.addValues(this.originalValues);

		this.boundOnAddTag = this.onAddTag.bind(this);
		this.boundOnRemoveTag = this.onRemoveTag.bind(this);

		// Chainable event listeners
		this.tagify.on('add', this.boundOnAddTag)
			.on('remove', this.boundOnRemoveTag)
			.on('click', this.onTagClick.bind(this));

		this.updateEventHandlerStars(this.originalValues);

		this.subscriptions.add(
			this.additionalValues$.subscribe(addedValues => {
				this.addValues(addedValues);
				this.updateEventHandlerStars(addedValues);
			}));

		this.subscriptions.add(
			this.values$.subscribe(values => {
				this.removeValues();
				this.addValues(values);
				this.updateEventHandlerStars(values);
			}));

		this.subscriptions.add(
			this.editableState$.subscribe({
				next: state => this.handleEditableState(state)
			}));
	}

	/**
	 * Handle the change of states of edition for the component (READ_ONLY, STARS_ALLOWED, STARS_ALLOWED).
	 * @param state the new state of edition
	 */
	handleEditableState(state: TagifyEditableState) {
		this.editableState = state;

		// No placeholder if we are in read-only mode
		if (this.isReadOnly()) {
			this.tagify.settings.placeholder = '';
		}

		// The TextArea used to enter a new tag is disabled in read-only mode.
		const tagInput = document.getElementById('tag-input');
		tagInput.contentEditable = !this.isCompleteEditionAllowed() ? 'false' : 'true';

		// We hide the remove button "x" from each tag present in the container.
		document.querySelectorAll('.tagify__tag__removeBtn').forEach(elt =>
			elt.setAttribute('style', (!this.isCompleteEditionAllowed() ? 'visibility:hidden' : 'visibility:visible'))
		);
	}

	/**
	 * Return **true** if this component is completly __READ_ONLY__.
	 */
	isReadOnly() {
		return (this.editableState === TagifyEditableState.READ_ONLY);
	}

	/**
	 * Return true if this component allow the edition of the evaluation stars.
	 */
	isStarsOnly() {
		return (this.editableState === TagifyEditableState.STARS_ALLOWED);
	}

	/**
	 * Return true if this component allow the complete edition, (including the removal of tags).
	 */
	isCompleteEditionAllowed() {
		return (this.editableState === TagifyEditableState.ALL_ALLOWED);
	}

	/**
	 * Remove all tags from the component.
	 */
	private removeValues() {
		this.tagify.off('remove', this.boundOnRemoveTag);
		this.tagify.removeAllTags();
		this.tagify.on('remove', this.boundOnRemoveTag);
	}

	/**
	 * Add an array of tags inside the component.
	 * @param values the array of TagStar.
	 */
	private addValues(values: TagStar[]) {
		this.tagify.off('add', this.boundOnAddTag);
		this.tagify.addTags(values.map(tagStar => tagStar.tag));
		values.forEach(tagStar => {
			for (let i = 0; i <= tagStar.star; i++) {
				this.setColor(tagStar.tag, i, this.colorON);
			}
			for (let i = tagStar.star + 1; i < 5; i++) {
				this.setColor(tagStar.tag, i, this.colorOFF);
			}
		});
		this.tagify.on('add', this.boundOnAddTag);
	}

	/**
	 * Generate the key for a specific tag.
	 * @param tag the searched tag
	 * @param ind the associated level for the tag
	 */
	private idStar(tag: string, ind: number) {
		return 'tag-star-' + tag + '-' + ind;
	}

	/**
	 * Register an event handler for each star in the component.
	 */
	updateEventHandlerStars(tagStars: TagStar[]) {
		tagStars.forEach(tagstar => {
			for (let i = 0; i < 5; i++) {
				const id = this.idStar(tagstar.tag, i);
				document.getElementById(id).onclick = this.boundOnClick[i];
			}
		});
	}

	/**
	 * A new tag has been added in the list
	 * @param e the associated tag
	 */
	onAddTag(e: CustomEvent) {
		for (let i = 0; i < 5; i++) {
			const id = this.idStar(e.detail.data.value, i);
			document.getElementById(id).onclick = this.boundOnClick[i];
		}
		this.setColor(e.detail.data.value, 0, this.colorON);
		this.addTagEvent.emit(new TagStar(e.detail.data.value, 0));
	}

	/**
	 * An existing tag has been removed from the list
	 * @param e the associated tag
	 */
	onRemoveTag(e: CustomEvent) {
		for (let i = 0; i < 5; i++) {
			const id = this.idStar(e.detail.data.value, i);
			if (document.getElementById(id)) {
				document.getElementById(id).onclick = null;
			}
		}
		this.removeTagEvent.emit(e.detail.data.value);
	}

	/**
	 * This method's handling the user click on a tag.
	 * @param event the event fired
	 */
	onTagClick(event: CustomEvent) {

		//
		// If we are in a readonly mode, we cancel the edition.
		//
		if (this.isReadOnly()) {
			return;
		}

		const tag = event.detail.data.value;
		for (let i = 0; i <= this.star; i++) {
			this.setColor(tag, i, this.colorON);
		}
		for (let i = this.star + 1; i < 5; i++) {
			this.setColor(tag, i, this.colorOFF);
		}
		this.editTagEvent.emit(new TagStar(tag, this.star));
		this.star = 0;
	}

	/**
	 * Set the color for a specific star of a tag
	 * @param tag the tag
	 * @param star the rank star inside the tag
	 * @param color  the color to be set
	 */
	setColor(tag: string, star: number, color: string) {
		const element = document.getElementById(this.idStar(tag, star));
		if (element) {
			element.style.color = color;
		}
	}

	/**
	 * Save the number of the rank 'star'.
	 */
	public onClick_0() {
		this.star = 0;
	}

	/**
	 * Save the number of the rank 'star'.
	 */
	public onClick_1() {
		this.star = 1;
	}

	/**
	 * Save the number of the rank 'star'.
	 */
	public onClick_2() {
		this.star = 2;
	}

	/**
	 * Save the number of the rank 'star'.
	 */
	public onClick_3() {
		this.star = 3;
	}

	/**
	 * Save the number of the rank 'star'.
	 */
	public onClick_4() {
		this.star = 4;
	}

	ngOnDestroy(): void {
		this.subscriptions.unsubscribe();
	}

}
