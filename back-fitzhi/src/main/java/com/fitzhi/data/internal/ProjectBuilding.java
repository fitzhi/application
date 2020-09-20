package com.fitzhi.data.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class represents a project building ready to be integrated in the skyline.
 * </p> 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectBuilding {
    
    /**
     * The building which is a timestamped layering of development activities.
     */
    public List<ProjectFloor> building = new ArrayList<>();
}
