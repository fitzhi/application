import {InMemoryDbService} from 'angular-in-memory-web-api';
import {MOCK_COLLABORATORS} from '../mock/mock-collaborators';

export class InMemoryDataService implements InMemoryDbService {

  constructor() {}

  createDb() {
    const collaborators = MOCK_COLLABORATORS;
    return {collaborators};
  }
}
