package com.fitzhi.source.crawler.git;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.Operation;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;
import com.itextpdf.text.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of the method {@link RepoScanner#upgradeRepository(com.fitzhi.data.internal.Project, com.fitzhi.data.source.CommitRepository)} 
 * with the GIT implementation.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerUpgradeRepositoryTest {
	
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@MockBean
	StaffHandler staffHandler;

	@MockBean
	ProjectHandler projectHandler;

	@Test
	public void noUpdateAtAll() throws ApplicationException {
		Project project = new Project(1789, "The revolutionary project");

		doNothing().when(projectHandler).integrateGhosts(anyInt(), any());
		when(staffHandler.lookup(new Author("author", "email"))).thenReturn(null);

		CommitRepository repository = new BasicCommitRepository();
		repository.addCommit("fr/test/test.java", -1, "author", "email", LocalDate.of(2020, 11, 1), 0);
		Assert.assertFalse("If no update is executed, then the method should return FALSE",  scanner.upgradeRepository(project, repository));
		Operation operation = repository.getRepository().get("fr/test/test.java").operations.get(0);
		Assert.assertEquals(-1, operation.getIdStaff());

		verify(staffHandler, times(1)).lookup(any(Author.class));
		verify(projectHandler, times(1)).integrateGhosts(anyInt(), any());
	}

	@Test
	public void updateOneStaffMember() throws ApplicationException {
		Project project = new Project(1789, "The revolutionary project");

		doNothing().when(projectHandler).integrateGhosts(anyInt(), any());
		when(staffHandler.lookup(new Author("author", "email"))).thenReturn(new Staff(14, "login", "password"));

		CommitRepository repository = new BasicCommitRepository();
		repository.addCommit("fr/test/test.java", -1, "author", "email", LocalDate.of(2020, 11, 1), 0);

		Assert.assertTrue("If an update is executed, then the method has to return TRUE", scanner.upgradeRepository(project, repository));
		Assert.assertEquals(1, repository.getRepository().get("fr/test/test.java").operations.size());
		Operation operation = repository.getRepository().get("fr/test/test.java").operations.get(0);
		Assert.assertEquals(14, operation.getIdStaff());

		verify(staffHandler, times(1)).lookup(any(Author.class));
		verify(projectHandler, times(1)).integrateGhosts(anyInt(), any());
	}

	@Test
	public void updateOneStaffMemberOn2Files() throws ApplicationException {
		Project project = new Project(1789, "The revolutionary project");

		doNothing().when(projectHandler).integrateGhosts(anyInt(), any());
		when(staffHandler.lookup(new Author("author", "email"))).thenReturn(new Staff(14, "login", "password"));
		when(staffHandler.lookup(new Author("anotherAuthorNameButSameEmail", "email"))).thenReturn(new Staff(14, "login", "password"));

		CommitRepository repository = new BasicCommitRepository();
		repository.addCommit("fr/test/test1.java", -1, "author", "email", LocalDate.of(2020, 11, 1), 0);
		repository.addCommit("fr/test/test2.java", -1, "anotherAuthorNameButSameEmail", "email", LocalDate.of(2020, 11, 2), 0);

		Assert.assertTrue("If an update is executed, then the method has to return TRUE", scanner.upgradeRepository(project, repository));
		
		Assert.assertEquals(1, repository.getRepository().get("fr/test/test1.java").operations.size());
		Assert.assertEquals(1, repository.getRepository().get("fr/test/test2.java").operations.size());

		Operation operation = repository.getRepository().get("fr/test/test1.java").operations.get(0);
		Assert.assertEquals(14, operation.getIdStaff());

		operation = repository.getRepository().get("fr/test/test2.java").operations.get(0);
		Assert.assertEquals(14, operation.getIdStaff());

		verify(staffHandler, times(2)).lookup(any(Author.class));
		verify(projectHandler, times(1)).integrateGhosts(anyInt(), any());
	}

	@Test
	public void updateOneAlreadyRegisteredStaffMember() throws ApplicationException {
		Project project = new Project(1789, "The revolutionary project");

		doNothing().when(projectHandler).integrateGhosts(anyInt(), any());
		when(staffHandler.lookup(new Author("author", "email"))).thenReturn(new Staff(14, "login", "password"));

		CommitRepository repository = new BasicCommitRepository();
		repository.addCommit("fr/test/test.java", -1, "author", "email", LocalDate.of(2020, 11, 1), 0);
		repository.addCommit("fr/test/test.java", 14, "nope", "nope@email", LocalDate.of(2020, 11, 2), 0);

		Assert.assertTrue("If an update is executed, then the method has to return TRUE", scanner.upgradeRepository(project, repository));
		
		Assert.assertEquals(2, repository.getRepository().get("fr/test/test.java").operations.size());
		Operation operation = repository.getRepository().get("fr/test/test.java").operations.get(0);
		Assert.assertEquals(14, operation.getIdStaff());

		verify(staffHandler, times(1)).lookup(any(Author.class));
		verify(projectHandler, times(1)).integrateGhosts(anyInt(), any());
	}

}
