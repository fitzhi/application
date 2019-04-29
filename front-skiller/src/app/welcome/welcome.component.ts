import { Component, OnInit } from '@angular/core';
import { BackendSetupService } from '../service/backend-setup/backend-setup.service';
import { AuthService } from '../admin/service/auth/auth.service';

@Component({
    selector: 'app-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {

    /**
     * Is this the VERY fist launch into WIBKAC ?
     * We speak about the FIRST user connecting for the FIRST time into Wibkac ?
     */
    veryFirstLaunch = false;

    /**
     * Is this the FIRST launch into WIBKAC ?
     * This is not the FIRST user, but this is the first time a user is trtying to connect
     * from this desktop/browser insoide Wibkac.
     */
    firstLaunch = false;

    constructor(
        private backendSetupService: BackendSetupService,
        private authService: AuthService) {
            this.firstLaunch = !this.backendSetupService.hasSavedAnUrl();
    }

   ngOnInit() { }

    /**
     * @returns TRUE if the connection is requested.
     * There are 2 possible reasons for that :
     * 1) This is the first connection on WibKac
     * 2) The user is not connected for this session
     */
    connectionIsNeeded() {
        return (!this.authService.isConnected() && !this.firstLaunch);
    }

}
