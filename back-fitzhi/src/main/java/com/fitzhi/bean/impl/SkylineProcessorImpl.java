package com.fitzhi.bean.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Layer;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectBuilding.YearWeek;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.Skyline;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.git.SourceChange;
import com.fitzhi.source.crawler.git.SourceFileHistory;
import com.fitzhi.util.LayerFactory;
import com.fitzhi.util.ProjectBuildingFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Main (an currently single) implementation in charge of the skyline
 * processing.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Slf4j
public class SkylineProcessorImpl implements SkylineProcessor {

	@Autowired
	private StaffHandler staffHandler;

	@Autowired
	private DataHandler dataHandler;

	@Autowired
	private ProjectHandler projectHandler;

	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;

	interface LayerIdentifier {
		Layer processLayer(SourceChange sourceChange);
	}

	private boolean isExternalLibrary (String filePath, List<Library> externalLibraries) {


		String fileCleanPath = projectDashboardCustomizer.cleanupPath("/" + filePath);
		if (fileCleanPath.charAt(0) == '/') {
			fileCleanPath = fileCleanPath.substring(1);
		}

		for (Library lib : externalLibraries) {
			String libPath = lib.getExclusionDirectory();
			if (fileCleanPath.length() < libPath.length()) {
				continue;
			}
			if (libPath.equals(fileCleanPath.substring(0, libPath.length()))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ProjectLayers generateProjectLayers(Project project, SourceControlChanges changes) throws ApplicationException {

		final Function<SourceChange, Layer> layerIdentifier = (SourceChange sourceChange) -> {
			return LayerFactory.getInstance(sourceChange);
		};

		final List<ProjectLayer> layers = new ArrayList<>();
		changes.getChanges().entrySet().stream()
				
				.filter(entry -> !isExternalLibrary(entry.getKey(), project.getLibraries()))
				.map(entry -> entry.getValue())

				.flatMap(hist -> hist.getChanges().stream())
				
				.collect(Collectors.groupingBy(layerIdentifier, Collectors.summingInt(SourceChange::lines)))
				.forEach((layer, lines) -> {
					ProjectLayer projectLayer = new ProjectLayer(project.getId(), layer.getYear(), layer.getWeek(), lines,
							layer.getIdStaff());
					layers.add(projectLayer);
				});

		Collections.sort(layers);
		return new ProjectLayers(project, layers);
	}

	@Override
	public void actualizeStaff(Project project, SourceControlChanges changes) {
		final Map<String, Integer> cache = new HashMap<>();
		changes.getChanges().values().stream()
			.flatMap((SourceFileHistory sfh) -> sfh.getChanges().stream())
			.forEach((SourceChange sc) -> {
				final Staff staff = staffHandler.lookup(sc.getAuthor());
				if (cache.containsKey(sc.getAuthor().getName())) {
					sc.setIdStaff(cache.get(sc.getAuthor().getName()).intValue());
				} else {
					sc.setIdStaff((staff != null) ? staff.getIdStaff() : -1);
					cache.put(sc.getAuthor().getName(), sc.getIdStaff());
				}
			});
	}

	@Override
	public ProjectBuilding generateProjectBuilding(Project project, ProjectLayers layers) {

		ProjectBuilding building = ProjectBuildingFactory.getInstance(project, layers);

		if (log.isDebugEnabled()) {
			log.debug(String.format("the building has %d floors", building.getBuilding().size()));
		}

		// This temporalField is used to retrieve the week number of the date into the year
		final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

		// This one retrieves the associated year.
		final TemporalField yowoy = WeekFields.of(Locale.getDefault()).weekBasedYear();

		for (ProjectLayer layer : layers.getLayers()) {

			if (log.isDebugEnabled()) {
				log.debug (String.format("Project %d layer week/year %d/%d", project.getId(), layer.getWeek(), layer.getYear()));
			}

			if (layer.getIdStaff() <= 0) {
				building.addInactiveLines(layer.getLines(), layer.getYear(), layer.getWeek());
			} else {
				Staff staff = staffHandler.getStaff(layer.getIdStaff());
				if (staff == null) {
					building.addInactiveLines(layer.getLines(), layer.getYear(), layer.getWeek());
					if (log.isWarnEnabled()) {
						log.warn(
							String.format("Identifier %d is not found in the staff members, but present in Project %s. This staff member has probably been removed", layer.getIdStaff(), project.getName()));
					}
					building.addInactiveLines(layer.getLines(), layer.getYear(), layer.getWeek());
				} else {
					if (staff.isActive()) {
						building.addActiveOrInactiveLines(layer.getLines(), layer.getYear(), layer.getWeek(),
								Integer.MAX_VALUE, Integer.MAX_VALUE);
					} else {
						building.addActiveOrInactiveLines(layer.getLines(), layer.getYear(), layer.getWeek(),
								staff.getDateInactive().get(yowoy), staff.getDateInactive().get(woy));
					}
				}
			}
		}
		return building;
	}

	@Override
	public ProjectBuilding generateProjectBuilding(Project project) throws ApplicationException {

		// If no skyline have been already saved, we return an empty building.
		if (!dataHandler.hasSavedSkylineLayers(project)) {
			return new ProjectBuilding();
		}

		ProjectLayers layers = dataHandler.loadSkylineLayers(project);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading %d layers for the project %s", layers.getLayers().size(),
					project.getName()));
		}

		return generateProjectBuilding(project, layers);
	}

	@Override
	public void completeProjectLayers(ProjectLayers projectLayers) {

		final YearWeek latestWeek = projectLayers.LatestWeek();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Last week for project %s : %d/%d", projectLayers.getProject().getName(),
					latestWeek.getWeek(), latestWeek.getYear()));
		}

		// This temporalField is used to retrieve the week number of the date into the year.
		final TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
		// This one retrieves the associated year.
		final TemporalField yowoy = WeekFields.of(Locale.getDefault()).weekBasedYear();

		final List<ProjectLayer> latestLayers = projectLayers.filterOnWeek(latestWeek);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, latestWeek.getYear());
		calendar.set(Calendar.WEEK_OF_YEAR, latestWeek.getWeek());
		LocalDate date = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		date = date.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
		date = date.plusDays(7);

		final LocalDate dateCurrentWeek = LocalDate.now();
		while (date.isBefore(dateCurrentWeek) || date.equals(dateCurrentWeek)) {
			for (ProjectLayer layer : latestLayers) {
				final int year = date.get(yowoy);
				final int week = date.get(woy);
				projectLayers.getLayers()
						.add(new ProjectLayer(layer.getIdProject(), year, week, 0, layer.getIdStaff()));
				if (log.isDebugEnabled()) {
					log.debug(String.format(
							"Adding an empty activity for the staff id %d in the project %s for week %d/%d",
							layer.getIdStaff(), projectLayers.getProject().getName(), week, year));
				}
			}
			date = date.plusDays(7);
		}
	}

	@Override
	public Skyline generateSkyline() throws ApplicationException {

		List<Project> projects = projectHandler.getProjects().values().stream().filter(Project::isActive).collect(Collectors.toList());
		return generateSkyline(projects);
	 }

	@Override
	public Skyline generateSkyline(List<Project> projects) throws ApplicationException  {
		final Skyline skyline = new Skyline();

		for (Project project : projects) {
			if (dataHandler.hasSavedSkylineLayers(project)) {
				ProjectBuilding pb = generateProjectBuilding(project);
				skyline.addBuilding(pb);
			}
		}
		return skyline;
	}    
}
