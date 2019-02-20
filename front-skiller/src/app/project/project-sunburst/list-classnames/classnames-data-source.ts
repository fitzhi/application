import { DataSource } from '@angular/cdk/table';
import { BehaviorSubject, Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { Filename } from '../../../data/filename';

export class ClassnamesDataSource implements DataSource<Filename> {

    public filenamesSubject = new BehaviorSubject<Filename[]>([]);
    public loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

    /**
     * Connect this datasource to the list
     */
    connect(collectionViewer: CollectionViewer): Observable<Filename[]> {
        return this.filenamesSubject.asObservable();
    }

    /**
     * Disconnect this datasource to the list
     */
    disconnect(collectionViewer: CollectionViewer): void {
        this.filenamesSubject.complete();
        this.loadingSubject.complete();
    }

    /**
     * Send the loaded data from the backend.
     * @param unknowns list of unregistered contributors.
     */
    sendClassnames(selClassnames: Filename[]): void {
        this.loadingSubject.next(true);
        const filenames = [];
        selClassnames.forEach(function (entry) {
            const cls = new Filename(entry.filename, entry.lastCommit);
            filenames.push(cls);
        });
        this.filenamesSubject.next(filenames);
    }

    constructor() {}
}
