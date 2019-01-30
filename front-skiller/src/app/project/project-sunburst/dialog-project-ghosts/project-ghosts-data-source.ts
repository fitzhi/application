import { DataSource } from '@angular/cdk/table';
import { Unknown } from '../../../data/Unknown';
import { BehaviorSubject, Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { Project } from '../../../data/project';
import { Ghost } from '../../../data/Ghost';

export class ProjectGhostsDataSource implements DataSource<Ghost> {

    public ghostsSubject = new BehaviorSubject<Ghost[]>([]);
    public loadingSubject = new BehaviorSubject<boolean>(false);

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
    connect(collectionViewer: CollectionViewer): Observable<Ghost[]> {
        return this.ghostsSubject.asObservable();
    }

    /**
     * Disconnect this datasource to the list
     */
    disconnect(collectionViewer: CollectionViewer): void {
        this.ghostsSubject.complete();
        this.loadingSubject.complete();
    }

    /**
     * Send the loaded data from the backend.
     * @param unknowns list of unregistered contributors.
     */
    sendUnknowns(unknowns: Unknown[]): void {
        this.loadingSubject.next(true);
        const ghosts = [];
        unknowns.forEach(function (unknown) {
            const g = new Ghost();
            g.pseudo = unknown.login;
            g.idStaff = -1;
            g.login = '';
            g.technical = false;
            ghosts.push(g);
        });
        this.ghostsSubject.next(ghosts);
    }

}
