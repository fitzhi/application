package com.fitzhi.data.external;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.data.internal.ResumeSkill;

import lombok.Data;

@Data
public class StaffResume implements Serializable {

	public List<ResumeSkill> experiences = new ArrayList<>();

	/**
	 * Empty constructor
	 */
	public StaffResume() {
	}
	
	/**
	 * Public construction with the list of experiences
	 * @param experiences the list fo experiences
	 */
	public StaffResume(List<ResumeSkill> experiences) {
		this.experiences = experiences;
	}

}
