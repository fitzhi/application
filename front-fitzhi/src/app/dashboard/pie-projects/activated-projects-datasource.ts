import { DataSource } from '@angular/cdk/table';
import { Project } from 'src/app/data/project';
import { Observable, EMPTY, of } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { switchMap } from 'rxjs/operators';
import { Slice } from '../slice';

export class ActivatedProjectsDatasSource implements DataSource<Project> {

	constructor(public pieDashboardService: PieDashboardService) {}

	connect(collectionViewer: CollectionViewer): Observable<Project[]> {
		return this.pieDashboardService.sliceActivated$.
				pipe(
					switchMap((slice: Slice) => of(slice.projects))
				);
	}

	disconnect(collectionViewer: CollectionViewer): void {
	}

}
