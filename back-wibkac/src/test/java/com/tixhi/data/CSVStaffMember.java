/**
 * 
 */
package com.tixhi.data;

import com.opencsv.bean.CsvBindByPosition;


/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class CSVStaffMember {
	
    @CsvBindByPosition(position=31)
    String skill;

    @CsvBindByPosition(position=4)
    String firstName;

    @CsvBindByPosition(position=3)
    String lastName;
	
    @CsvBindByPosition(position=6)
    String email;

    @CsvBindByPosition(position=7)
    String login;

    @CsvBindByPosition(position=8)
    String actif;

    @CsvBindByPosition(position=18)
    String poste;

    @CsvBindByPosition(position=31)
    String skill_java;

    @CsvBindByPosition(position=32)
    String skill_dotNet;


	@Override
	public String toString() {
		return "CSVStaffMember [skill=" + skill + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
				+ email + ", login=" + login + ", actif=" + actif + ", skill_java=" + skill_java + ", skill_dotNet="
				+ skill_dotNet + ", poste=" + poste + "]";
	}
    
    
    
}
