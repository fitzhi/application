import { DataSource } from '@angular/cdk/table';
import { BehaviorSubject, Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { Contributor } from '../../../data/contributor';

export class ContributorsDataSource implements DataSource<Contributor> {

    public committersSubject = new BehaviorSubject<Contributor[]>([]);
    public loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

    /**
     * Connect this datasource to the list
     */
    connect(collectionViewer: CollectionViewer): Observable<Contributor[]> {
        return this.committersSubject.asObservable();
    }

    /**
     * Disconnect this datasource to the list
     */
    disconnect(collectionViewer: CollectionViewer): void {
        this.committersSubject.complete();
        this.loadingSubject.complete();
    }

    /**
     * Send the loaded data from the backend.
     * @param committers list of contributors.
     */
    sendContributors(committers: Contributor[]): void {
        this.loadingSubject.next(true);
        const contributors = [];
        committers.forEach(function (entry) {
            contributors.push(entry);
        });
        this.committersSubject.next(contributors);
    }

    public constructor() {}
}
