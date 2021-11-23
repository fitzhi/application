import { Injectable } from '@angular/core';
import { FormGroup, ValidatorFn } from '@angular/forms';

/**
 * custom validator to check that two fields match.
 * @param controlName the first control.
 * @param matchingControlName the second which must match.
 */
@Injectable({ providedIn: 'root' })
export class PasswordConfirmationMustMatchValidator {
	public check(): ValidatorFn {
		return (formGroup: FormGroup) => {

			const control = formGroup.controls['password'];
			const matchingControl = formGroup.controls['passwordConfirmation'];

			// return if another validator has already found an error on the matchingControl
			if (matchingControl.errors) {
				return null;
			}

			// set error on matchingControl if validation fails
			if (control.value !== matchingControl.value) {
				return ({ mustMatch: true });
			} else {
				return null;
			}
		};
	}
}
