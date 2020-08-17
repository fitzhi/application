import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';
import { traceOn } from 'src/app/global';

@Component({
  selector: 'app-branch-selector',
  templateUrl: './branch.component.html',
  styleUrls: ['./branch.component.css']
})
export class BranchComponent implements OnInit {

	/**
   * We'll send to the parent component that the selected branch has been changed.
   */
  @Output() messengerOnBranchChange = new EventEmitter<string>();

  constructor(public projectService: ProjectService) { }

  ngOnInit(): void {
  }

  /**
   * End-user has selected a branch name.
   * @param $event the select widget when a specific branch has been selected
   */
  onBranchChange($event) {
    if (traceOn()) {
      console.log ('selection has changed to', $event.target.value);
    }
    this.messengerOnBranchChange.emit($event.target.value);
  }

  selectBranch(branch: string) {
    if (traceOn()) {
      console.log ('Set the selectde branch to', this.projectService.project.branch);
    }

  }
}
