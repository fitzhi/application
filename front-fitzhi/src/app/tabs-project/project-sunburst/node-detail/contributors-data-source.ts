import { DataSource } from '@angular/cdk/table';
import { BehaviorSubject, Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { Contributor } from '../../../data/contributor';

export class ContributorsDataSource implements DataSource<Contributor> {

	public committers$ = new BehaviorSubject<Contributor[]>([]);

	public loaded$ = new BehaviorSubject<boolean>(false);

	/**
	 * Public simple construction.
	 */
	public constructor() { }

	/**
     * Connect this datasource to the list
     */
	connect(collectionViewer: CollectionViewer): Observable<Contributor[]> {
		return this.committers$.asObservable();
	}

	/**
     * Disconnect this datasource to the list
     */
	disconnect(collectionViewer: CollectionViewer): void {
		this.committers$.complete();
		this.loaded$.complete();
	}

	/**
     * Send the loaded data from the backend.
     * @param committers list of contributors.
     */
	sendContributors(committers: Contributor[]): void {
		this.loaded$.next(true);
		const contributors = [];
		committers.forEach(function (entry) {
			contributors.push(entry);
		});
		this.committers$.next(contributors);
	}
}
