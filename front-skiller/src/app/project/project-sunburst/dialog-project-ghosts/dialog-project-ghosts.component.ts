import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';
import { ProjectService } from '../../../project.service';
import { PseudoList } from '../../../data/PseudoList';
import { Constants } from '../../../constants';
import { MessageService } from '../../../message.service';

@Component({
  selector: 'app-dialog-project-ghosts',
  templateUrl: './dialog-project-ghosts.component.html',
  styleUrls: ['./dialog-project-ghosts.component.css']
})
export class DialogProjectGhostsComponent implements OnInit {

  /**
   * The undeclared contributors in the repository.
   */
  public dataSource: ProjectGhostsDataSource;

  constructor(
    private dialogRef: MatDialogRef<DialogProjectGhostsComponent>,
    private projectService: ProjectService,
    private messageService: MessageService,
    @Inject(MAT_DIALOG_DATA) public data: ProjectGhostsDataSource) { }

  ngOnInit() {
    this.dataSource = this.data;
  }

  public submit() {
    this.projectService.saveGhosts( new PseudoList(
        this.dataSource.project.id,
        this.dataSource.ghostsSubject.getValue()))
        .subscribe(pseudoList => {
            if (Constants.DEBUG) {
              console.group('[Response from /project/api-ghosts] peudoList');
              console.log('idProject ' + pseudoList.idProject);
              pseudoList.unknowns.forEach(function (value) { console.log(value); });
              console.groupEnd();
            }
        },
        responseInError => {
          if (Constants.DEBUG) {
            console.log('Error ' + responseInError.error.code + ' ' + responseInError.error.message);
          }
          this.messageService.error(responseInError.error.message);
        });

    this.dialogRef.close(this.dataSource.ghostsSubject.getValue());
  }
}
