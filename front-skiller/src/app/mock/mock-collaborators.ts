/**
 * New typescript file
 */

import {Collaborator} from '../data/collaborator';
import {Attribution} from '../data/attribution';


export const MOCK_COLLABORATORS: Collaborator[] = [
  {
    id: 1,
    firstName: 'Frederic',
    lastName: 'VIDAL',
    nickName: 'altF4',
    login: 'frvidal',
    email: 'frvidal@sqli.com',
    level: 'ET2',
    active: 1,
    projects: null,
    experiences: null
  },
  {
    id: 2,
    firstName: 'Olivier',
    lastName: 'MANFE',
    nickName: 'la Mouf',
    login: 'omanfe',
    email: 'omanfe@sqli.com',
    level: 'ICD 3',
    active: 1,
    projects: null,
    experiences: [{ id: 1, title: 'Java', level: 2} ]
  },
  {
    id: 3,
    firstName: 'Alexandre',
    lastName: 'JOURDES',
    nickName: 'Jose',
    login: 'ajourdes',
    email: 'ajourdes@sqli.com',
    level: 'ICD 2',
    active: 1,
    projects: null,
    experiences: null
  },
  {
    id: 4,
    firstName: 'Christophe',
    lastName: 'OPOIX',
    nickName: 'Copo',
    login: 'copoix',
    email: 'ocopoix@sqli.com',
    level: 'ET 2',
    active: 1,
    projects: null,
    experiences: null
  }
  ];

