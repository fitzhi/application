package com.fitzhi.data.internal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This class represents a project building ready to be integrated in the skyline.
 * </p> 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
public @Data class ProjectBuilding {

    /**
     * This class contains a key for the building.
     */
    public @Data class YearWeek {

        private int idProject;
        
        private int year;
        
        private int week;

        /**
         * Public construction
         * @param idProject the project identifier
         * @param year the year
         * @param week the week
         */
        public YearWeek(int idProject, int year, int week) {
            this.idProject = idProject;
            this.year = year;
            this.week = week;
        }
    
    }
    /**
     * The building which is a timestamped layering of development activities.
     */
    private Map<YearWeek, ProjectFloor> building = new HashMap<>();

    /**
     * Current project identifier.
     */
    private int idProject;

    /**
     * Initialize a week in the year.
     * @param idProject the project identifier
     * @param year the year
     * @param week the week
     */
    public void initWeek (int idProject, int year, int week) {
        this.idProject = idProject;
        building.put(new YearWeek(idProject, year, week), new ProjectFloor(idProject, year, week, 0, 0));
    }

    /**
     * <p>
     * This method might throw a {@link RuntimeException} if the project doest not exist.  
     * </p>
     * @param year the given year
     * @param week the given week
     * @return the floor for the given year and week
     */
    public ProjectFloor getProjectFloor(int year, int week) {
        
        ProjectFloor floor = building.get(yearWeek(year, week));
        if (floor == null) {
            throw new RuntimeException(String.format("Cannot retrieve the floor (%d;%d) in project %d", year, week, idProject));
        }
        return floor;
    }

    /**
     * <p>
     * Instanciate a new {@link YearWeek}
     * </p>
     * @param year the given year
     * @param week the given week
     * @return a new instance of {@link YearWeek}
     */
    public YearWeek yearWeek(int year, int week) {
        return new YearWeek(idProject, year, week);
    }

     /**
     * <p>
     * Take in account active lines, or inactive, depending on the date of inactivation for the staff member.
     * </p>
     * 
     * <p>
     * This method will update all entries inactive lines counter untill now.
     * </p>
     * 
     * @param lines the number of lines.
     * @param startingYear <i>starting year</i> for taking in account this number of lines
     * @param startingWeek <i>starting week</i> for taking in account this number of lines
     */
    public void addInactiveLines(int lines, int startingYear, int startingWeek) {

        LocalDate startingDate = LocalDate.of(startingYear, 2, 1)
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, startingWeek)
            .with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());

        if (log.isDebugEnabled()) {
            log.debug(String.format("Starting addInactiveLines starting from %s", startingDate));
        }

        // This temporalField is used to retrieve the week number of the date into the year
        final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

        for (LocalDate date = startingDate; date.isBefore(LocalDate.now()); date = date.plusWeeks(1)) {
            ProjectFloor floor = getProjectFloor(date.getYear(), date.get(woy));
            floor.addLinesInactiveDevelopers(lines);
        }
    }

    /**
     * <p>
     * Take in account active lines, or inactive, depending on the date of inactivation for the staff member.
     * </p>
     * <p>
     * if this method receives the two pairs of date : (2020; 20) -> (2020; 30) and a number of lines equal to 5.
     * The building will be enhanced of 5 <b>active</b> lines for each week from 2000/20 up to 2020/30, 5 <b>inactive</b> lines starting from week 2020/31.
     * </P>
     * @param lines the number of lines.
     * @param startingYear <i>starting year</i> for taking in account this number of lines
     * @param startingWeek <i>starting week</i> for taking in account this number of lines
     * @param endingYear <i>ending year</i> for taking in account this number of lines
     * @param endingWeek <i>starting week</i> for taking in account this number of lines
     */
    public void addActiveOrInactiveLines(int lines, int startingYear, int startingWeek, int endingYear, int endingWeek) {

        LocalDate startingDate = LocalDate.of(startingYear, 2, 1)
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, startingWeek)
            .with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());

        LocalDate endingDate = (endingYear == Integer.MAX_VALUE)  ?
            LocalDate.MAX :
            LocalDate.of(endingYear, 2, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, endingWeek)
                .with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());

        if (log.isDebugEnabled()) {
            log.debug(String.format("Starting addActiveOrInactiveLines from %s to %s", startingDate, endingDate));
        }

        // This temporalField is used to retrieve the week number of the date into the year
        final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

        for (LocalDate date = startingDate; date.isBefore(LocalDate.now()); date = date.plusWeeks(1)) {
            ProjectFloor floor = getProjectFloor(date.getYear(), date.get(woy));
            if (date.isAfter(endingDate)) {
                floor.addLinesInactiveDevelopers(lines);
            } else {
                floor.addLinesActiveDevelopers(lines);
            }
        }
    }
}
