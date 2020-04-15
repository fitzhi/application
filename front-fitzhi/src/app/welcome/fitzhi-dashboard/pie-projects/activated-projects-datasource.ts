import { DataSource } from '@angular/cdk/table';
import { Project } from 'src/app/data/project';
import { Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { Constants } from 'src/app/constants';

export class ActivatedProjectsDatasSource implements DataSource<Project> {

	constructor(public pieDashboardService: PieDashboardService) {}

	connect(collectionViewer: CollectionViewer): Observable<Project[]> {
		return this.pieDashboardService.projectsActivated$;
	}

	disconnect(collectionViewer: CollectionViewer): void {
	}

}
