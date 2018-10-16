import {Constants} from '../../../../constants';
import {Collaborator} from '../../../../data/collaborator';
import { DeclaredExperienceDTO } from '../../../../data/external/declaredExperienceDTO';
import {MessageBoxService} from '../../../../message-box/service/message-box.service';
import { StaffDataExchangeService } from '../../../service/staff-data-exchange.service';
import {HttpClient} from '@angular/common/http';
import {HttpResponse} from '@angular/common/http';
import {HttpEventType} from '@angular/common/http';
import {HttpRequest} from '@angular/common/http';
import {Component, OnInit, Inject} from '@angular/core';
import {Subject, Observable} from 'rxjs';
import {MAT_DIALOG_DATA} from '@angular/material';

@Component({
  selector: 'app-staff-upload-cv',
  templateUrl: './staff-upload-cv.component.html',
  styleUrls: ['./staff-upload-cv.component.css'],
})
export class StaffUploadCvComponent implements OnInit {

  /**
   * Full path of the selected file.
   */
  applicationFile: File;

  /**
   * Current collaborator active in the Staff Form.
   */
  collaborator: Collaborator;

  /**
   * Progression Bar representing the upload speed.
   */
  progression = new Subject<number>();
  progress = this.progression.asObservable();

  constructor(
    private staffDataExchangeService: StaffDataExchangeService,
    private httpClient: HttpClient,
    private messageBoxService: MessageBoxService,
    @Inject(MAT_DIALOG_DATA) public data: any) {}

  ngOnInit() {
    /**
     * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
     */
    this.staffDataExchangeService.collaboratorObserver
      .subscribe((collabRetrieved: Collaborator) => {
        this.collaborator = collabRetrieved;
      });
  }

  submit() {
    if (typeof this.applicationFile === 'undefined') {
      const dialogRef = this.messageBoxService.error('ERROR', 'You must select the application file first !');
    } else {
      if (this.checkApplicationFormat()) {
        this.upload(this.applicationFile);
      }
    }
  }

  checkApplicationFormat(): boolean {
    if (this.applicationFile != null) {
      if (!Constants.APPLICATION_FILE_TYPE_ALLOWED.has(this.applicationFile.type)) {
        this.messageBoxService.error('ERROR', 'Only the formats .DOC, .DOCS and .PDF are supported !');
        return false;
      } else {
        return true;
      }
    }
  }

  public fileEvent($event) {
   this.applicationFile = $event.target.files[0];
    if (Constants.DEBUG) {
      console.log ('Testing checkApplicationFormat for ' + this.applicationFile.type);
    }
   this.checkApplicationFormat();
}

  upload(file: File) {

    if (Constants.DEBUG) {
      console.log('Uploading the file ' + this.applicationFile.name);
    }
    // create a new multipart-form for the file to upload.
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('id', <string><any>this.collaborator.idStaff);
    formData.append('type', <string><any>Constants.APPLICATION_FILE_TYPE_ALLOWED.get(this.applicationFile.type));

    // create a HTTP-post request and pass the form
    // tell it to report the upload progress
    const req = new HttpRequest('POST', Constants.URL_BACKEND + '/staff/api/uploadCV', formData, {
      reportProgress: true
    });

    // send the HTTP-request and subscribe for progress-updates
    this.httpClient.request(req).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress) {

        // calculate the progress percentage
        const percentDone = Math.round(100 * event.loaded / event.total);

        // pass the percentage into the progress-stream
        this.progression.next(percentDone);
      } else if (event instanceof HttpResponse) {
        console.log ('Upload successfull');
        const response = <DeclaredExperienceDTO> event.body;
        if (Constants.DEBUG) {
          console.log ('experiences detected : ');
          console.log (response.experience);
        }
        // Close the progress-stream if we get an answer form the API
        // The upload is complete
        this.progression.complete();
      }
    });
  }
}
