package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * Commit statistics for a file involved in a Git commit.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Data
public class SourceCodeDiffChange {
    private final String filename;
    private int linesDeleted;
    private int linesAdded;

    /**
     * Empty constructor.
     */
    public SourceCodeDiffChange() {
        this.filename = "EMPTY";
    }
    
    /**
     * Public construction of the SourceCodeDiffChange object.
     * @param filename the filebame concerned.
     * @param linesDeleted the number of deleted lines
     * @param linesAdded the number of added lines.
     */
    public SourceCodeDiffChange(final String filename, final int linesDeleted, final int linesAdded) {
        this.filename = filename;
        this.linesDeleted = linesDeleted;
        this.linesAdded = linesAdded;
    }

}