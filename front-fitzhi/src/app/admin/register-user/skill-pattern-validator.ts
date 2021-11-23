import { Injectable } from '@angular/core';
import { FormGroup, ValidatorFn } from '@angular/forms';

@Injectable({ providedIn: 'root' })
export class SkillPatternValidator {
	/**
	 * This function is a validator for the field File `pattern` which permits to detect a file.
	 */
	public check(): ValidatorFn {
		return (formGroup: FormGroup) => {
			const detectionType: string = formGroup.get('detectionType').value;
			const pattern: string = formGroup.get('pattern').value;
			if ((detectionType.length === 0) && (pattern.length === 0)) {
				return null;
			} else {
				if ((+detectionType >= 0) && (pattern.length === 0)) {
					return { 'patternRequired': true };
				}
			}
			return null;
		};
	}
}
