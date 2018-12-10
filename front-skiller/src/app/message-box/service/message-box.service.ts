import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef, MatDialogConfig} from '@angular/material';
import {MessageBoxComponent} from '../dialog/message-box.component';

@Injectable({
  providedIn: 'root'
})
export class MessageBoxService {

  constructor(private dialog: MatDialog) { }

  /**
   * Display an ERROR message alert dialog.
   */
  public error (title: string, message: string) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.panelClass = 'default-dialog-container-class';
    dialogConfig.data = { message: message, title: title };
    return this.dialog.open(MessageBoxComponent, dialogConfig);
  }
}
