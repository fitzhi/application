import { Collaborator } from './collaborator';
import { StaffListCriteria } from '../tabs-staff-list/service/staffListCriteria';

export class StaffListContext {
  public criteria: string;
  public activeOnly: boolean;
  public staffSelected: Collaborator[] = [];

  public constructor(criterias: StaffListCriteria) {
      this.criteria = criterias.criteria;
      this.activeOnly = criterias.activeOnly;
  }

  store (collaborators: Collaborator[]) {
    collaborators.forEach(collaborator => {
      this.staffSelected.push(JSON.parse(JSON.stringify(collaborator)));
    });
  }
}
