import { DataSource } from '@angular/cdk/table';
import { Unknown } from '../../../data/Unknown';
import { BehaviorSubject, Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { Project } from '../../../data/project';
import { Ghost } from '../../../data/Ghost';

export class ProjectGhostsDataSource implements DataSource<Unknown> {

    public ghostsSubject = new BehaviorSubject<Ghost[]>([]);
    private loadingSubject = new BehaviorSubject<boolean>(false);

    private ghosts: Ghost[] = [];

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
    sendUnknowns (unknowns: Unknown[]): void {
        this.loadingSubject.next(true);
        const _this = this;
        unknowns.forEach(function(unknown) {
            const g = new Ghost();
            g.pseudo = unknown.login;
            g.idStaff = -1;
            g.login = 'login';
            g.technical = false;
            _this.ghosts.push(g);
        });
        this.ghostsSubject.next(this.ghosts);
    }

    getGhosts() {
        return this.ghosts;
    }

    setGhosts(ghosts: Ghost[]) {
        this.ghosts = ghosts;
    }

}
