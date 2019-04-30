import { Injectable } from '@angular/core';
import { InternalService } from 'src/app/internal-service';
import { Constants } from 'src/app/constants';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { HttpClient } from '@angular/common/http';
import { StaffDTO } from 'src/app/data/external/staffDTO';
import { MessageService } from 'src/app/message/message.service';
import { take } from 'rxjs/operators';

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
        private messageService: MessageService,
        private httpClient: HttpClient) { super(); }

    public connect (username: string, password: string) {
        if (Constants.DEBUG) {
            console.log ('Trying a connection with user/pass ' + username + ':' + password);
        }
        this.httpClient.get<StaffDTO>(
            this.backendSetupService.url() + '/admin/connect',
                { params: { login: username, password: password }})
                .pipe(take(1))
                .subscribe(
                    response => {
                        if (response.code === 0) {
                            if (Constants.DEBUG) {
                                console.log ('Successful connection for user '
                                    + response.staff.firstName + ' ' + response.staff.lastName );
                            }
                            this.connected = true;
                        } else {
                            this.messageService.error (response.message);
                        }
                    },
                    error => {
                        if (Constants.DEBUG) {
                            console.error ('Connection error ', error);
                        }
                    });
    }

    /**
     * @returns TRUE if the user is connected, FALSE otherwise.
     */
    public isConnected() {
        return this.connected;
    }
}
