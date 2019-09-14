package fr.skiller.controller.in;

import fr.skiller.data.internal.ResumeSkill;
import lombok.Data;

public @Data class BodyParamResumeSkills {
	
	private int idStaff;
	private ResumeSkill[] skills;

	public BodyParamResumeSkills () {
		// Empty constructor declared for serialization / deserialization purpose 
	}
	
}
