package com.fitzhi.data.internal;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * This class contains the experiences detected in a project.
 */
public @Data class ProjectDetectedExperiences {

    private List<DetectedExperience> values = new ArrayList<>();
}