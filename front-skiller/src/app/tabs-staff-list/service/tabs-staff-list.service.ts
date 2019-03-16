import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { StaffListCriteria } from './staffListCriteria';
import { Collaborator } from '../../data/collaborator';
import { StaffService } from '../../service/staff.service';
import { Constants } from '../../constants';
import { StaffListContext } from '../../data/staff-list-context';
import { MessageService } from '../../message/message.service';
import { SkillService } from '../../service/skill.service';
import { Skill } from '../../data/skill';

@Injectable({
    providedIn: 'root'
})
export class TabsStaffListService {

    /**
     * List of criterias dispplay on the tab staff lists dashboard.
     */
    public staffListContext: Map<String, StaffListContext> = new Map<String, StaffListContext>();

    /**
     * Index of the active tab.
     */
    public activeTab = 0;

    /**
     * Key of the active tab.
     */
    public activeKey: string;

    /**
     * The tab content is used in master/detail way. The prev and the next button are visible.
     */
    public inMasterDetail = false;

    /**
     * New search is made by the developer.
     */
    public search$ = new Subject<StaffListCriteria>();

    constructor(
        private staffService: StaffService,
        private skillService: SkillService,
        private messageService: MessageService) {

        this.search$.subscribe(criterias => {
            if (Constants.DEBUG) {
                console.log ('Adding criterias ' + criterias.criteria);
            }

            if (!this.staffListContext.has(this.key(criterias))) {
                this.staffListContext.set (
                    this.key(criterias),
                    new StaffListContext(criterias)
                );
           }
        });
     }

     /**
      * Construct the key of the criterias.
      */
     key (criterias: StaffListCriteria): string {
        return criterias.criteria + '-' + (criterias.activeOnly ? '1' : '0');
     }

    /**
     * Add a tab of results in the tab containter.
     */
    public addTabResult(criteria: string, activeOnly: boolean) {

        const myCriteria = new StaffListCriteria(criteria, activeOnly);
        if (this.staffListContext.has(this.key(myCriteria))) {
            this.messageService.info('This criteria is already present!');
            return;
        }
        this.search$.next(myCriteria);
    }

    /**
     * Searching staff members corresponding to the 2 passed criterias.
     * @param criteria the criteria
     * @param activeOnly active only Yes/No
     */
    public search(criteria: string, activeOnly: boolean): Subject<Collaborator[]> {
        return this._search(criteria, activeOnly, this.skillService.skills);
    }

    /**
     * Searching staff members corresponding to the 2 passed criterias.
     * @param criteria the criteria
     * @param activeOnly active only Yes/No
     * @param allSkills list of ALL skills registered inside the application
     */
    public _search(criteria: string, activeOnly: boolean, allSkills: Skill[]): Subject<Collaborator[]> {
        const collaborator = [];

        const collaborator$ = new Subject<Collaborator[]>();

        /**
         * Cache of the skills filter.
         */
        const skillsFilter: number[] = [];

        const key = this.key(new StaffListCriteria(criteria, activeOnly));

        if (this.staffListContext.has(key)) {
            const context = this.staffListContext.get(key);
            if (context.staffSelected.length > 0) {
                if (Constants.DEBUG) {
                    console.log('Using cache for key ' + key + ' ' + context.staffSelected.length + ' records');
                }
                setTimeout(() => {
                    context.staffSelected.forEach(c => collaborator.push(c));
                    collaborator$.next(collaborator);
                }, 0);
                return collaborator$;
            }
        }

        function extractCriteriaSkills(): string {
            const pos_start_skills = criteria.toLowerCase().indexOf('skill:');
            if (pos_start_skills > -1) {
                const work = criteria.toLowerCase().substring (pos_start_skills + 'skill:'.length);
                let text_skills: string;
                if (work.length > 0) {
                    const pos_end_skills = work.toLowerCase().indexOf(';');
                    // Assuming that until the end of string, we have skills
                    if (pos_end_skills === -1) {
                        text_skills = work;
                    } else {
                        text_skills = work.substring(0, pos_end_skills);
                    }
                    return text_skills;
                }
            }
            return '';
        }

        function reminderCriteria(): string {
            const pos_start_skills = criteria.toLowerCase().indexOf('skill:');
            if (pos_start_skills === -1) { return criteria; }
            const pos_end_skills = criteria.indexOf(';', pos_start_skills);
            const reminder = (pos_end_skills === -1) ?
                    criteria.substr(0, pos_start_skills) :
                    criteria.substr(0, pos_start_skills - 1) + criteria.substr(pos_end_skills + 1);
            if (Constants.DEBUG) {
                console.log ('reminderCriteria(' + criteria + ') = ' + reminder);
            }
            return reminder;
        }

        function getSkillsFilter(): number[] {

            // We cache the array of skills id. We don need to parse the criteria for each entry.
            if (skillsFilter.length > 0) {
                return skillsFilter;
            }

            const criteriaSkills = extractCriteriaSkills();
            if (criteriaSkills.length > 0) {
                const skills = criteriaSkills.split(',');
                if (Constants.DEBUG) {
                    console.groupCollapsed ('Skills candidate ');
                    skills.forEach(skill => console.log (skill));
                    console.groupEnd();
                }
                skills.forEach(skill => {
                    allSkills.forEach(sk => {
                        if (sk.title.toLocaleLowerCase() === skill.toLocaleLowerCase()) {
                            skillsFilter.push(sk.id);
                        }
                    });
                });
                if (Constants.DEBUG) {
                    console.groupCollapsed ('id of skills candidate ');
                    skillsFilter.forEach(id => console.log (id));
                    console.groupEnd();
                }
            }

            return skillsFilter;
        }

        function testCriteria(collab: Collaborator): boolean {

            const skills = getSkillsFilter();

            const experiences = collab.experiences.map(exp => exp.id);
            if (skills.every(id => experiences.includes(id))) {
                if (Constants.DEBUG) {
                    console.log (collab.firstName + ' ' + collab.lastName + ' meets the criterias');
                }
            } else {
                // If we do not find each skill in the selected array of skills, we reject this collaborator.
                return false;
            }

            const reminder = reminderCriteria().trim().toLowerCase();
            if (reminder.length === 0) {
                return (activeOnly ? collab.isActive : true);
            }

            const firstname = (typeof collab.firstName !== 'undefined') ? collab.firstName : '';
            const lastname = (typeof collab.lastName !== 'undefined') ? collab.lastName : '';
            return (
                ((firstname.toLowerCase().indexOf(reminder) > -1)
                    || (lastname.toLowerCase().indexOf(reminder) > -1))
                && (activeOnly ? collab.isActive : true)
            );
        }

        this.staffService.getAll().subscribe((staffs: Collaborator[]) => {
            staffs.forEach(staff => {
                if (testCriteria(staff)) {
                    collaborator.push(staff);
                }});
            },
            error => console.log(error),
            () => {
                if (Constants.DEBUG) {
                    console.log('The staff collection is containing now ' + collaborator.length + ' records');
                    console.groupCollapsed('Staff members found : ');
                    collaborator.forEach(collab => console.log(collab.firstName + ' ' + collab.lastName));
                    console.groupEnd();
                }
                if (this.staffListContext.has(key)) {
                    if (Constants.DEBUG) {
                        console.log('Saving collaborators for key ' + key);
                    }
                    this.staffListContext.get(key).store(collaborator);
                }
                collaborator$.next(collaborator);
            });
        return collaborator$;
    }

    /**
     * Remove from history the search entry corresponding to this key.
     */
    public removeHistory (key: string) {
        this.staffListContext.delete(key);
        if (Constants.DEBUG) {
            console.groupCollapsed ('Remaining tabs');
            this.staffListContext.forEach(criterias => console.log (criterias.criteria));
            console.groupEnd();
        }
    }

    /**
     * @param current identifier
     * @returns the NEXT collaborator's id associated with this id in the staff list.
     */
    nextCollaboratorId(id: number): number {

        const context = this.staffListContext.get(this.activeKey);

        const index = context.staffSelected.findIndex(collab => collab.idStaff === id);
        if (Constants.DEBUG) {
            console.log('Current index : ' + index + ' for ' + this.activeKey);
            console.log('List size : ' + context.staffSelected.length);
        }
        if (index < context.staffSelected.length - 1) {
            return context.staffSelected[index + 1].idStaff;
        } else {
            return undefined;
        }
    }

    /**
     * @param current identifier
     * @returns the PREVIOUS collaborator's id associated with this id in the staff list.
     */
    previousCollaboratorId(id: number): number {

        const context = this.staffListContext.get(this.activeKey);

        const index = context.staffSelected.findIndex(collab => collab.idStaff === id);
        if (index > 0) {
            return context.staffSelected[index - 1].idStaff;
        } else {
            return undefined;
        }
    }

    /**
     * Retrieve the context associated to the key.
     * @param key searched key
     * @returns the context associated to this list or null if none exists (which is suspected to be an internal error)
     */
    getContext (key: string): StaffListContext  {
        if (!this.staffListContext.has(key)) {
            console.error('Cannot retrieve key ' + key);
            return null;
        } else {
            return this.staffListContext.get(key);
        }
    }
}
