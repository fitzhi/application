import {InMemoryDbService} from 'angular-in-memory-web-api';


export class InMemoryDataService implements InMemoryDbService {

  constructor() {}

  createDb() {
    const skiller = [];
    return {skiller};
  }
}
