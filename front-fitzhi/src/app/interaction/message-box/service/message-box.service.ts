import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MessageBoxComponent } from '../dialog/message-box.component';
import { Observable, Subject } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class MessageBoxService {

	constructor(private dialog: MatDialog) { }

	/**
	 * Display an ERROR message alert dialog.
	 */
	public error(title: string, message: string) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;
		dialogConfig.panelClass = 'default-dialog-container-class';
		dialogConfig.data = { message: message, title: title, ok: true, yes_no: false };
		return this.dialog.open(MessageBoxComponent, dialogConfig);
	}

	/**
	 * Display an ERROR message alert dialog.
	 */
	public exclamation(title: string, message: string) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;
		dialogConfig.panelClass = 'default-dialog-container-class';
		dialogConfig.data = { message: message, title: title, ok: true, yes_no: false };
		return this.dialog.open(MessageBoxComponent, dialogConfig);
	}

	/**
	 * Display an QUESTION message dialog.
	 * @param title the title on top of the question
	 * @param message the question
	 * @returns an observable emitting the answer
	 */
	public question(title: string, message: string): Observable<boolean> {
		const answerSubject$ = new Subject<boolean>();
		const answer$ = answerSubject$.asObservable();
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;
		dialogConfig.panelClass = 'default-dialog-container-class';
		dialogConfig.data = { message: message, title: title, ok: false, yes_no: true };
		const dialogRef = this.dialog.open(MessageBoxComponent, dialogConfig);
		dialogRef.afterClosed().subscribe(response => answerSubject$.next(response));
		return answer$;
	}
}
