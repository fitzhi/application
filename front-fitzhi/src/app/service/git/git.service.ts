import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { Branch } from 'src/app/data/git/branch';
import { Repository } from 'src/app/data/git/repository';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';

@Injectable({
	providedIn: 'root'
})
export class GitService {

	/**
	 * This **behaviorSubject** is handling an assistance message given to the end-user,
     * in which he's invited to save the project in order to get the git branches
	 */
	public assistanceMessageGitBranches$ = new BehaviorSubject<boolean>(false);

	private headerAccept = 'application/vnd.github.v3+json';

	constructor(
		private httpClient: HttpClient,
		private messageService: MessageService) { }


	/**
	 * Return **true** if the given url is a Github URL.
	 * @param url the HTTP url given by the end user
	 */
	isGithubUrl(url: string): boolean {
		try {
			const myUrl = new URL(url);
			const allowedHosts = [
				'api.github.com',
				'www.github.com',
				'github.com'
			];
			return allowedHosts.includes(myUrl.hostname);
		} catch (TypeError) {
			return false;
		}
	}

	/**
	 * Generate & return the url associated to the given HTTP url to access GitHub with its REST API.
	 *
	 * For instance, the given URL 'https://github.com/fitzhi/application'
	 * will produce 'https://api.github.com/repos/fitzhi/application'
	 *
	 * @param httpUrl the HTTP url used to clone the repository.
	 */
	generateUrlApiGithub(httpUrl: String): string {
		const start = httpUrl.toLowerCase().indexOf('github.com') + 'github.com'.length;
		const res = 'https://api.github.com/repos' + httpUrl.substring(start);
		if (traceOn()) {
			console.log('generateUrlApiGithub(%s)=%s', httpUrl, res);
		}
		return res;
	}

	/**
	 * This function is trying to connect to a GitHub repository with the Rest Rest API
	 * @param url the given API url
	 */
	public connect$(url: string): Observable<Repository> {

		console.log('connect$', url);

		const headers = new HttpHeaders();
		headers.set('Accept', this.headerAccept);
		return this.httpClient.get(url, { headers: headers, responseType: 'json' })
			.pipe(
				tap((result: Repository) => {
					if (traceOn()) {
						console.log('Obtaining the rough response', result.name);
					}
				}),
				switchMap(
					(repo: Repository) => of(repo)),
				catchError(error => {
					if (traceOn()) {
						console.log('Error with url ' + url, error);
					}
					if (error.status === 404) {
						if (traceOn()) {
							console.log('The url ' + url + ' is un recheable from here. It migth be Ok');
						}
					} else {
						setTimeout(() => this.messageService.error('Unattempted error ' + error.status + ' with url ' + url), 0);
					}
					return of(null);
				})
			);

	}

	/**
	 * This function is returning all branches declared for this user.
	 *
	 * @param url an access URL through the GitHub API to retrieved the current branches.
	 * @param defaultBranch the default branch, if (for any reason) the HTTP get failed.
	 */
	public branches$(url: string, defaultBranch: string): Observable<string[]> {

		const headers = new HttpHeaders();
		headers.set('Accept', this.headerAccept);
		return this.httpClient.get(url, { headers: headers, responseType: 'json' })
			.pipe(
				tap(() => {
					if (traceOn()) {
						console.log('Retrieving the branches @ %s', url);
					}
				}),
				switchMap(
					(branches: Branch[]) => of(branches.map(branch => branch.name).sort(
						function (a, b) {
							return a.toLowerCase().localeCompare(b.toLowerCase());
						}))),
				catchError(error => {
					if (traceOn()) {
						console.log('Get the error when retrieving the branches', error);
					}
					return of([defaultBranch]);
				})
			);
	}

}
