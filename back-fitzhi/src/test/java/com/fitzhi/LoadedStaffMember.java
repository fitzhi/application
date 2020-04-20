/**
 * 
 */
package com.fitzhi;

import com.opencsv.bean.CsvBindByPosition;


/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class LoadedStaffMember {

    @CsvBindByPosition(position=0)
    String firstName;

    @CsvBindByPosition(position=1)
    String lastName;
    
    @CsvBindByPosition(position=2)
    String nickname;
	
    @CsvBindByPosition(position=3)
    String login;

    @CsvBindByPosition(position=4)
    String email;
    
    @CsvBindByPosition(position=5)
    String level;
    
    @CsvBindByPosition(position=6)
    String external;

	@Override
	public String toString() {
		return "LoadedStaffMember [firstName=" + firstName + ", lastName=" + lastName + ", nickname=" + nickname
				+ ", login=" + login + ", email=" + email + ", level=" + level + ", external=" + external + "]";
	}

    
}
