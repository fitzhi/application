/**
 * 
 */
package com.fitzhi.source.crawler.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.SourceChange;
import com.fitzhi.source.crawler.git.SourceFileHistory;

/**
 * <p>
 * Test of the method {@link AbstractScannerDataGenerator#gatherContributorsActivitySkill(java.util.List, java.util.Set)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AbstractScannerDataGeneratorGatherContributorsActivitySkillTest {
	
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	Map<String, SourceFileHistory> globalChanges = new HashMap<String, SourceFileHistory>();
		
	final int ID_1 = 1;
	final LocalDate MIN_ID_1 = LocalDate.of(2019, 1, 1);
	final LocalDate MAX_ID_1 = LocalDate.of(2019, 12, 23);
	
	final int ID_2 = 2;
	final LocalDate MIN_ID_2 = LocalDate.of(2019, 10, 1);
	final LocalDate MAX_ID_2 = MIN_ID_2;
	
	final int ID_3 = 3;
	final LocalDate MIN_ID_3 = LocalDate.of(2019, 4, 15);
	final LocalDate MAX_ID_3 = LocalDate.of(2019, 7, 14);

	final int ID_SKILL_JAVA = 1;
	final int ID_SKILL_TS = 2;
	
	SourceControlChanges sourceControlChanges = new SourceControlChanges();

	List<Contributor> contributors = new ArrayList<>();
	
	Set<String> pathSourceFileNames = new HashSet<>();
	
	@Before()
	public void before() {
		one();
		two();
		three();
		contributors();
		paths();
	}
	
	private void one() {
		sourceControlChanges.setChanges(globalChanges);
		
		SourceFileHistory history = new SourceFileHistory();
		globalChanges.put("one.java", history);
		List<SourceChange> changes = new ArrayList<>();
		history.setChanges(changes);
		
		changes.add(new SourceChange(MIN_ID_1, ID_1));
		changes.add(new SourceChange(LocalDate.of(2019, 4, 15), ID_1));
		changes.add(new SourceChange(MAX_ID_1, ID_1));
		
		changes.add(new SourceChange(MIN_ID_2, ID_2));
		changes.add(new SourceChange(MAX_ID_2, ID_2));
		
		changes.add(new SourceChange(MAX_ID_3, ID_3));		
	}
	
	private void two() {
		SourceFileHistory history = new SourceFileHistory();
		globalChanges.put("two.java", history);
		List<SourceChange> changes = new ArrayList<>();
		history.setChanges(changes);
		
		changes.add(new SourceChange(LocalDate.of(2019, 4, 15), ID_1));
		changes.add(new SourceChange(LocalDate.of(2019, 4, 16), ID_1));
		changes.add(new SourceChange(LocalDate.of(2019, 4, 17), ID_1));

		changes.add(new SourceChange(MIN_ID_3, ID_3));
		changes.add(new SourceChange(LocalDate.of(2019, 4, 16), ID_3));
		changes.add(new SourceChange(LocalDate.of(2019, 4, 17), ID_3));
	}

	private void three() {
		SourceFileHistory history = new SourceFileHistory();
		globalChanges.put("three.ts", history);
		List<SourceChange> changes = new ArrayList<>();
		history.setChanges(changes);
		changes.add(new SourceChange(LocalDate.of(2020, 1, 1), ID_1));
	}
	
	private void contributors() {
		contributors.add(new Contributor(ID_1, LocalDate.now(), LocalDate.now(), 0, 0));
		contributors.add(new Contributor(ID_2, LocalDate.now(), LocalDate.now(), 0, 0));
		contributors.add(new Contributor(ID_3, LocalDate.now(), LocalDate.now(), 0, 0));
	}
	
	private void paths() {
		pathSourceFileNames.add("one.java");
		pathSourceFileNames.add("two.java");
		pathSourceFileNames.add("three.ts");
	}
	
	@Test
	public void test() throws SkillerException {
		scanner.gatherContributorsActivitySkill(contributors, sourceControlChanges, pathSourceFileNames);
		Contributor contributor = contributors.get(0);
		Assert.assertEquals(ID_1, contributor.getIdStaff());

		
		// JAVA
		Assert.assertEquals(MIN_ID_1, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getFirstCommit());
		Assert.assertEquals(MAX_ID_1, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getLastCommit());
		Assert.assertEquals(6, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getNumberOfChanges());
		// TS
		Assert.assertEquals(LocalDate.of(2020, 1, 1), contributor.getStaffActivitySkill().get(ID_SKILL_TS).getFirstCommit());
		Assert.assertEquals(LocalDate.of(2020, 1, 1), contributor.getStaffActivitySkill().get(ID_SKILL_TS).getLastCommit());
		Assert.assertEquals(1, contributor.getStaffActivitySkill().get(ID_SKILL_TS).getNumberOfChanges());

		
		contributor = contributors.get(1);
		Assert.assertEquals(ID_2, contributor.getIdStaff());
		Assert.assertEquals(MIN_ID_2, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getFirstCommit());
		Assert.assertEquals(MAX_ID_2, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getLastCommit());
		Assert.assertEquals(2, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getNumberOfChanges());
		
		contributor = contributors.get(2);
		Assert.assertEquals(ID_3, contributor.getIdStaff());
		Assert.assertEquals(MIN_ID_3, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getFirstCommit());
		Assert.assertEquals(MAX_ID_3, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getLastCommit());
		Assert.assertEquals(4, contributor.getStaffActivitySkill().get(ID_SKILL_JAVA).getNumberOfChanges());
	}

}
