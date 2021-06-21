package com.fitzhi.data.internal;

import com.fitzhi.bean.ProjectHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This object is used to agregate the detected experiences in {@link ProjectHandler#processGlobalExperiences()}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public @Data class StaffExperienceTemplate {
    int idExperienceDetectionTemplate;
    int idStaff;
}

