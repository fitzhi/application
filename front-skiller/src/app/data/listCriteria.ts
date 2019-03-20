export class ListCriteria {
  criteria: string;
  activeOnly: boolean;

  public constructor (criteria: string, activeOnly: boolean) {
    this.criteria = criteria;
    this.activeOnly = activeOnly;
  }

}
