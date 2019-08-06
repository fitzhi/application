import { MatTableDataSource } from '@angular/material';
import { Library } from '../../../data/library';
import { BehaviorSubject } from 'rxjs';

export class DependenciesDataSource extends MatTableDataSource<Library> {

	private subjectLibraries$: BehaviorSubject<Library[]>;

	private libraries: Library[];

	constructor(libraries: Library[]) {
		super();
		this.libraries = libraries;
		this.subjectLibraries$ = new BehaviorSubject<Library[]>(this.libraries);
	}

	/**
	 * Update the datasource with new data.
	 * @param libraries the new libraries to be displayed
	 */
	update(libraries: Library[]) {
		this.libraries = libraries;
		this.subjectLibraries$.next(this.libraries);
	}

	/**
	 * Update the path associated to a dependency declared for the project
	 * @param dependency the dependency declared
	 * @param newPath the new path in the repository to exclude from the analysis
	 */
	public updatePath(updatedLibrary: Library, newPath: string) {
		const library = this.libraries
			.find(dep => dep.exclusionDirectory === updatedLibrary.exclusionDirectory);
		if (!library) {
			console.error('Data inconsistency for the dependency ' + library.exclusionDirectory);
			return;
		}

		// No modification has been made to the path.
		if (library.exclusionDirectory === newPath) {
			return;
		}

		// This path already exists.
		if (this.libraries.find(dep => dep.exclusionDirectory === newPath)) {
			return;
		}

		library.exclusionDirectory = newPath;
		// The path is no more a detected/calculated dependency path but a declared one. 
		library.type = 2;
		this.subjectLibraries$.next(this.libraries);
	}

	createNew() {
		if (this.libraries.filter(dep => dep.exclusionDirectory === '').length === 0) {
			this.libraries.push(new Library('', 2));
			this.subjectLibraries$.next(this.libraries);
		}
	}

	/**
	 * Remove a dependency from the datasource.
	 * @param library dependency record to be removed
	 */
	remove(library: Library) {
		const index = this.libraries.findIndex(dep => dep.exclusionDirectory === library.exclusionDirectory);
		if (index === -1) {
			console.error('Data inconsistency for the dependency ' + library.exclusionDirectory);
			return;
		}
		this.libraries.splice(index, 1);
		this.subjectLibraries$.next(this.libraries);
	}

	connect(): BehaviorSubject<Library[]> {
		return this.subjectLibraries$;
	}

	/**
	 * @returns the current libraries.
	 */
	public getLibraries(): Library[] {
		return this.libraries;
	}
}
