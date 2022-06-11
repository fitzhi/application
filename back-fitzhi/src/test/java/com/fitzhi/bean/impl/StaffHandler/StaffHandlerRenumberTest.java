package com.fitzhi.bean.impl.StaffHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.StaffHandlerImpl;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.DataCommitRepository;

/**
 * <p>
 * Test the method {@link StaffHandlerImpl#renumber(com.fitzhi.data.internal.Staff, int)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerRenumberTest {
	
	@Autowired
	StaffHandler staffHandler;
	
	@MockBean
	DataHandler dataHandler;

	@MockBean
	CacheDataHandler cacheDataHandler;

	@Autowired
	ObjectMapper objectMapper;

	private Project project = new Project(1789, "The revolution");

	@Test
	public void renumberStaff() throws Exception {
		File file = new File("./src/test/resources/slave-save-data/staff.json");
		final BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder realStaff = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		br.close();

		List<Staff> list = objectMapper.readValue(realStaff.toString(), new TypeReference<List<Staff>>(){});
		Assert.assertEquals(2, list.size());
		Staff staff = list.get(1);
		Assert.assertEquals(1000001, staff.getIdStaff());
		Assert.assertTrue(staff.getMissions().stream().anyMatch(m -> m.getIdStaff() == 1000001));
		Assert.assertTrue(staff.getMissions().stream().flatMap(m -> m.getStaffActivitySkill().values().stream()).anyMatch(sas -> sas.getIdStaff() == 1000001));

		staffHandler.renumber(staff, 1789);
		Assert.assertEquals(1789, staff.getIdStaff());
		Assert.assertFalse(staff.getMissions().stream().anyMatch(m -> m.getIdStaff() == 1000001));
		Assert.assertFalse(staff.getMissions().stream().flatMap(m -> m.getStaffActivitySkill().values().stream()).anyMatch(sas -> sas.getIdStaff() == 1000001));
		Assert.assertTrue(staff.getMissions().stream().anyMatch(m -> m.getIdStaff() == 1789));
		Assert.assertTrue(staff.getMissions().stream().flatMap(m -> m.getStaffActivitySkill().values().stream()).anyMatch(sas -> sas.getIdStaff() == 1789));

	}

	private ProjectLayers projectLayers() {
		ProjectLayers projectLayers = new ProjectLayers(project);
		projectLayers.getLayers().add(new ProjectLayer(1790, 2022, 10, 1000, 1789));
		projectLayers.getLayers().add(new ProjectLayer(1790, 2022, 10, 1000, 1000));
		projectLayers.getLayers().add(new ProjectLayer(1790, 2022, 11, 1000, 1789));
		return projectLayers;
	}
	
	@Test
	public void renumberLayers() throws Exception {

		ProjectLayers projectLayers = projectLayers();
		doNothing().when(dataHandler).saveSkylineLayers(any(Project.class), any(ProjectLayers.class));
		when(dataHandler.loadSkylineLayers(any(Project.class))).thenReturn(projectLayers);

		doNothing().when(cacheDataHandler).saveRepository(any(Project.class), any(CommitRepository.class));
		when(cacheDataHandler.getRepository(any(Project.class))).thenReturn(new BasicCommitRepository());

		staffHandler.renumber(project, 1789, 1790);
		Assert.assertFalse(projectLayers.getLayers().stream().anyMatch(layer -> layer.getIdStaff() == 1789));
		Assert.assertEquals(2, projectLayers.getLayers().stream().filter(layer -> layer.getIdStaff() == 1790).count());
		Assert.assertTrue(projectLayers.getLayers().stream().anyMatch(layer -> layer.getIdStaff() == 1000));
		Assert.assertEquals(1, projectLayers.getLayers().stream().filter(layer -> layer.getIdStaff() == 1000).count());
		verify(dataHandler, times(1)).saveSkylineLayers(any(Project.class), any(ProjectLayers.class));
	}

	@Test
	public void renumberRepositoryAnalysis() throws Exception {

		ProjectLayers projectLayers = new ProjectLayers(project);

		doNothing().when(dataHandler).saveSkylineLayers(any(Project.class), any(ProjectLayers.class));
		when(dataHandler.loadSkylineLayers(any(Project.class))).thenReturn(projectLayers);

		File file = new File("./src/test/resources/slave-save-data/source-control-changes.json");
		final BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder content = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		br.close();

		BasicCommitRepository bcr = new BasicCommitRepository();
		DataCommitRepository dcc = objectMapper.readValue(content.toString(), DataCommitRepository.class);
		bcr.setRepository(dcc.getRepo());
		bcr.setUnknownContributors(dcc.getUnknownContributors());
		
		doNothing().when(cacheDataHandler).saveRepository(any(Project.class), any(CommitRepository.class));
		when(cacheDataHandler.getRepository(any(Project.class))).thenReturn(bcr);

		Assert.assertEquals(1215, bcr.numberOfFiles(1000001));
		Assert.assertEquals(0, bcr.numberOfFiles(1789));

		staffHandler.renumber(project, 1000001, 1789);
		
		Assert.assertEquals(0, bcr.numberOfFiles(1000001));
		Assert.assertEquals(1215, bcr.numberOfFiles(1789));

		verify(cacheDataHandler, times(1)).saveRepository(any(Project.class), any(CommitRepository.class));
	}

}
