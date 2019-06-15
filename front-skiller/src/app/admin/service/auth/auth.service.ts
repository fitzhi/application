import { Injectable } from '@angular/core';
import { InternalService } from 'src/app/internal-service';
import { Constants } from 'src/app/constants';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { MessageService } from 'src/app/message/message.service';
import { take, switchMap } from 'rxjs/operators';
import { Token } from './token';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
providedIn: 'root'
})
export class AuthService extends InternalService {

    /**
     * This boolean is TRUE if the user is connected.
     */
     private connected = false;

     constructor(
         private backendSetupService: BackendSetupService,
         private httpClient: HttpClient) { super(); }

     public connect(username: string, password: string) {
         if (Constants.DEBUG) {
              console.log('Trying a connection with user/pass ' + username + ':' + password
              + ' on url ' + this.backendSetupService.url() + '/oauth/token');
         }

         let headers: HttpHeaders = new HttpHeaders();
         headers = headers.append('Content-Type', 'application/x-www-urlencoded');
         headers = headers.append('Authorization', 'Basic ' + btoa('wibkac-trusted-client' + ':secret'));

         const params = new HttpParams()
         .set('username', username)
         .set('password', password)
         .set('grant_type', 'password');

         this.httpClient.post<Token>(
              this.backendSetupService.url() + '/oauth/token', '', { headers: headers, params: params })
              .pipe(take(1))
              .subscribe(
                  token => {
                       if (Constants.DEBUG) {
                           console.groupCollapsed('Identifity retrieved : ');
                           console.log('access_token', token.access_token);
                           console.log('refresh_token', token.refresh_token);
                           console.log('expires_in', token.expires_in);
                           console.groupEnd();
                       }
                       // store the new tokens
                       localStorage.setItem('refresh_token', token.refresh_token);
                       localStorage.setItem('access_token', token.access_token);
                       this.connected = true;

                  },
                  error => {
                       if (Constants.DEBUG) {
                           console.error('Connection error ', error);
                       }
                  });
     }

    /**
     * @returns TRUE if the user is connected, FALSE otherwise.
     */
     public isConnected() {
         return this.connected;
     }

     getAccessToken() {
         return localStorage.getItem('access_token');
     }

     refreshToken(): Observable<string> {
        /*
            The call that goes in here will use the existing refresh token to call
            a method on the oAuth server (usually called refreshToken) to get a new
            authorization token for the API calls.
        */
         if (Constants.DEBUG) {
              console.log('refresh current token', localStorage.getItem('access_token'));
         }

         let headers: HttpHeaders = new HttpHeaders();
         headers = headers.append('Content-Type', 'application/x-www-urlencoded');
         headers = headers.append('Authorization', 'Basic ' + btoa('wibkac-trusted-client' + ':secret'));

         let access_token = 'empty';

         const params = new HttpParams()
         .set('access_token', localStorage.getItem('access_token'))
         .set('refresh_token', localStorage.getItem('refresh_token'))
         .set('grant_type', 'refresh_token');

         return this.httpClient.post<Token>(
              this.backendSetupService.url() + '/oauth/token', '', { headers: headers, params: params })
			  .pipe(
				take(1),
              	switchMap( token => {
                       if (Constants.DEBUG) {
                           console.groupCollapsed('Identifity retrieved : ');
                           console.log('access_token', token.access_token);
                           console.log('refresh_token', token.refresh_token);
                           console.log('expires_in', token.expires_in);
                           console.groupEnd();
                       }
                       // store the new tokens
                       access_token = token.access_token;
                       localStorage.setItem('refresh_token', token.refresh_token);
                       localStorage.setItem('access_token', token.access_token);
					   this.connected = true;

					   return of (token.access_token);
                  }
			  ));
     }
}


