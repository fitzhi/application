package com.fitzhi.bean.impl.DataHandler;

import java.time.LocalDate;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.source.crawler.git.SourceChange;
import com.fitzhi.source.crawler.git.SourceFileHistory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Testing the mehod {@link DataHandler#loadChanges(com.fitzhi.data.internal.Project)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataHandlerLoadChangesTest {
	
	@Autowired
	DataHandler dataHandler;

	private Project project;

	@Before
	public void before() {
		project = new Project(1789, "French revolution");
	}

	@Test
	public void loadChanges() throws ApplicationException {
		SourceControlChanges scc = this.dataHandler.loadChanges(project);
		Assert.assertEquals(3, scc.getChanges().size());
		SourceFileHistory scf = scc.getChanges().get("package/one.java");
		Assert.assertNotNull(scf);
		SourceChange sc = scf.getChanges().get(0);
		Assert.assertNotNull(sc);
		Assert.assertEquals("one", sc.getCommitId());
		Assert.assertEquals(LocalDate.of(2019,6,20), sc.getDateCommit());
		Assert.assertEquals("frvidal", sc.getAuthor().getName());
		Assert.assertEquals("frederic.vidal.perso@gmail.com", sc.getAuthor().getEmail());
		Assert.assertEquals(-1, sc.getIdStaff());
		Assert.assertEquals(1, sc.lines());

		scf = scc.getChanges().get("package/three.java");
		Assert.assertNotNull(scf);
		sc = scf.getChanges().get(0);
		Assert.assertNotNull(sc);
		Assert.assertEquals("three", sc.getCommitId());
		Assert.assertEquals(LocalDate.of(2019,6,22), sc.getDateCommit());
		Assert.assertEquals("averell", sc.getAuthor().getName());
		Assert.assertEquals("averell.dalton@gmail.com", sc.getAuthor().getEmail());
		Assert.assertEquals(-1, sc.getIdStaff());
		Assert.assertEquals(3, sc.lines());
	   
	}

	/**
	 * loadChanges throws a NotFoundException if no changes file does exist
	 */
	@Test (expected = NotFoundException.class)
	public void LoadChangesReturnNull() throws ApplicationException {
		final Project p = new Project(1939, "Bad year");
		dataHandler.loadChanges(p);
	}
}
