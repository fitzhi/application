import { Constants } from '../../../../../constants';
import { DeclaredExperience } from '../../../../../data/declared-experience';
import {Component, OnInit, Inject, ViewChild, AfterViewInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatTableDataSource } from '@angular/material';
import { MatPaginator } from "@angular/material/paginator";

@Component({
  selector: 'app-uploaded-skills-pickup',
  templateUrl: './uploaded-skills-pickup.component.html',
  styleUrls: ['./uploaded-skills-pickup.component.css']
})
export class UploadedSkillsPickupComponent implements AfterViewInit {

  displayedColumns: string[] = ['Pickup', 'Title', 'Occurrences'];
  
  private dataSource: MatTableDataSource<DeclaredExperience>;
  
  @ViewChild(MatPaginator) paginator: MatPaginator;
  
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any) {
    this.dataSource = new MatTableDataSource<DeclaredExperience>(this.data.experience);
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
  submit() {
    console.log ('Submit');
  }
}
