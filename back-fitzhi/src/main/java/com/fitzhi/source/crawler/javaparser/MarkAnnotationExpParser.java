package com.fitzhi.source.crawler.javaparser;

import com.fitzhi.exception.ApplicationException;
import com.github.javaparser.ast.CompilationUnit;

import org.eclipse.jgit.api.Git;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkAnnotationExpParser implements ExperienceParser {

    @Override
    public void analyze(CompilationUnit compilationUnit, Git git) throws ApplicationException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Analyzing file %s", compilationUnit.getStorage().get().getFileName()));
        }
    }
    
}
