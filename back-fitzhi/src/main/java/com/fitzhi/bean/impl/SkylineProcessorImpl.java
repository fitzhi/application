package com.fitzhi.bean.impl;

import java.util.List;

import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.SkillerException;

/**
 * <p>
 * Main (an currently single) implementation in charge of the skyline
 * processing.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class SkylineProcessorImpl implements SkylineProcessor {

    @Override
    public List<ProjectLayer> generateProjectLayers(Project project, SourceControlChanges changes)
            throws SkillerException {
        return null;
    }
    
}
