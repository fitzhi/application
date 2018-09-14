/**
 * New typescript file
 */

import {Collaborator} from '../data/collaborator';
import {Attribution} from '../data/attribution';


export const MOCK_COLLABORATORS: Collaborator[] = [
  {
    idStaff: 1,
    firstName: 'Frederic',
    lastName: 'VIDAL',
    nickName: 'altF4',
    login: 'frvidal',
    email: 'frvidal@sqli.com',
    level: 'ET2',
    isActive: true,
    projects: null,
    experiences: null
  },
  {
    idStaff: 2,
    firstName: 'Olivier',
    lastName: 'MANFE',
    nickName: 'la Mouf',
    login: 'omanfe',
    email: 'omanfe@sqli.com',
    level: 'ICD 3',
    isActive: true,
    projects: null,
    experiences: [{ id: 1, title: 'Java', level: 2} ]
  },
  {
    idStaff: 3,
    firstName: 'Alexandre',
    lastName: 'JOURDES',
    nickName: 'Jose',
    login: 'ajourdes',
    email: 'ajourdes@sqli.com',
    level: 'ICD 2',
    isActive: true,
    projects: null,
    experiences: null
  },
  {
    idStaff: 4,
    firstName: 'Christophe',
    lastName: 'OPOIX',
    nickName: 'Copo',
    login: 'copoix',
    email: 'ocopoix@sqli.com',
    level: 'ET 2',
    isActive: true,
    projects: null,
    experiences: null
  }
  ];

