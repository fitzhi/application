import { Component, OnInit, Input, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { Topic } from '../table-categories/topic';
import { BaseComponent } from 'src/app/base/base.component';
import { Subject, Observable } from 'rxjs';
import { Constants } from 'src/app/constants';
import { MatSliderChange } from '@angular/material/slider';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
	selector: 'app-audit-task-form',
	templateUrl: './audit-task-form.component.html',
	styleUrls: ['./audit-task-form.component.css']
})
export class AuditTaskFormComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Observable emitting the current active project/topic.
	 */
	@Input() topic$: Subject<Topic>;

	/**
	 * Current active topic received from the observable `topic$`.
	 */
	private topic: Topic;

	/**
	 * Corresponding slider value;
	 */
	private sliderWeightValue = 0;

	profileAuditTask = new FormGroup({
		evaluation: new FormControl('', Validators.max(100)),
		weight: new FormControl('', Validators.max(100)),
		comment: new FormControl('', [Validators.maxLength(2000)])
	});

	constructor() { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.topic$.subscribe(topic => {
				this.topic = topic;
				if (Constants.DEBUG) {
					console.log ('Active topic', this.topic.id + ':' + this.topic.title);
				}
			})
		);
	}

	/**
	* Content of a field has been updated.
	* @param field field identified throwing this event.
	*/
	public onChange(field: string) {
		if (field === 'weight') {
			this.sliderWeightValue = this.profileAuditTask.get('weight').value;
		}
	}

	/**
	 * The method is invoked when the slider is moved.
	 * @param sliderWeight the event emit by the slider.
	 */
	onChangeWeight(sliderWeight: MatSliderChange) {
		this.profileAuditTask.get('weight').setValue(sliderWeight.value);
	}

	/**
	 * Return the weight.
	 */
	get weight(): any {
		return this.profileAuditTask.get('weight');
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}


}
