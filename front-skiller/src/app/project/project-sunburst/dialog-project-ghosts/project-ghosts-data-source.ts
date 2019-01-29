import { DataSource } from '@angular/cdk/table';
import { Unknown } from '../../../data/Unknown';
import { BehaviorSubject, Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { Project } from '../../../data/project';

export class ProjectGhostsDataSource implements DataSource<Unknown> {

    private unknownsSubject = new BehaviorSubject<Unknown[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();

    public project: Project;

    /**
     * @param project current project
     */
    constructor(project: Project) {
        this.project = project;
    }

    /**
     * Connect this datasource to the list
     */
    connect(collectionViewer: CollectionViewer): Observable<Unknown[]> {
        return this.unknownsSubject.asObservable();
    }

    /**
     * Disconnect this datasource to the list
     */
    disconnect(collectionViewer: CollectionViewer): void {
        this.unknownsSubject.complete();
        this.loadingSubject.complete();
    }

    /**
     * Send the loaded data from the backend.
     * @param unknowns list of unregistered contributors.
     */
    sendUnknowns (unknowns: Unknown[]): void {
        this.loadingSubject.next(true);
        this.unknownsSubject.next(unknowns);
    }
}
