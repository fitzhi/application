import { Component, OnInit, Type } from '@angular/core';
import { EMPTY } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthenticationServer } from 'src/app/data/authentication-server';
import { TypeAuthenticationServer } from 'src/app/data/type-authentication-server';
import { ReferentialService } from 'src/app/service/referential/referential.service';

@Component({
  selector: 'alternative-openid-connection',
  templateUrl: './alternative-openid-connection.component.html',
  styleUrls: ['./alternative-openid-connection.component.css']
})
export class AlternativeOpenidConnectionComponent implements OnInit {

	public google: AuthenticationServer = undefined;

	constructor(public referentialService: ReferentialService) { }

	ngOnInit(): void {	
		this.referentialService.referentialLoaded$
			.pipe(switchMap(doneAndOk => (doneAndOk) ? this.referentialService.authenticationServers$ : EMPTY))
			.subscribe({
				next: (servers: AuthenticationServer[]) => {
					this.google = servers.find(server => server.typeAuthenticationServer === TypeAuthenticationServer.Google);
				}
			})
	}

}
