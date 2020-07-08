import { DataSource } from '@angular/cdk/table';
import { Project } from 'src/app/data/project';
import { Observable, EMPTY, of } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { switchMap } from 'rxjs/operators';
import { Slice } from 'dynamic-pie-chart';

export class ActivatedProjectsDatasSource implements DataSource<Project> {

	constructor(public pieDashboardService: PieDashboardService) {}

	connect(collectionViewer: CollectionViewer): Observable<any[]> {
		return this.pieDashboardService.sliceActivated$.
				pipe(
					switchMap((slice: Slice) => of(slice.children))
				);
	}

	disconnect(collectionViewer: CollectionViewer): void {
	}

}
