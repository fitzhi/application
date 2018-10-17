import { Constants } from '../../../../../constants';
import { DeclaredExperience } from '../../../../../data/declared-experience';
import { SelectionModel } from "@angular/cdk/collections";
import {Component, OnInit, Inject, ViewChild, AfterViewInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatTableDataSource } from '@angular/material';
import { MatPaginator } from "@angular/material/paginator";

@Component({
  selector: 'app-uploaded-skills-pickup',
  templateUrl: './uploaded-skills-pickup.component.html',
  styleUrls: ['./uploaded-skills-pickup.component.css']
})
export class UploadedSkillsPickupComponent implements AfterViewInit {

  displayedColumns: string[] = ['select', 'title', 'occurrences'];
  
  /**
   * Datasource associated to the table.
   */
  private dataSource: MatTableDataSource<DeclaredExperience>;
  
  private selection: SelectionModel<DeclaredExperience>;
  
  @ViewChild(MatPaginator) paginator: MatPaginator;
  
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any) {
    this.dataSource = new MatTableDataSource<DeclaredExperience>(this.data.experience);
    this.selection = new SelectionModel<DeclaredExperience>(true, []);
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    
  }
  
  /** 
   * Whether the number of selected elements matches the total number of rows.
   */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** 
   * Selects all rows if they are not all selected; otherwise clear selection. * 
   */
  masterToggle() {
    this.isAllSelected() ?
        this.selection.clear() :
        this.dataSource.data.forEach(row => this.selection.select(row));
  } 
   
  submit() {
    console.log ('Submit');
  }
}
