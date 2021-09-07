/**
 * 
 */
package com.fitzhi.data.external;



import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import com.fitzhi.data.internal.Committer;
import com.google.gson.Gson;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
public class GhostDTOTest {

	@Test
	public void testSimple() {
		Committer pseudo = new Committer();
		pseudo.setIdStaff(1);
		pseudo.setPseudo("test");
		pseudo.setLogin("loginTest");
		pseudo.setAction(Action.A);
		pseudo.setFirstCommit(LocalDate.of(2021, 1, 1));
		pseudo.setLastCommit(LocalDate.of(2021, 1, 2));
		pseudo.setNumberOfCommits(1);		
		pseudo.setNumberOfFiles(2);		
		Gson g = new Gson();

		String res = g.toJson(pseudo);
		if (log.isDebugEnabled()) {
			log.debug(res); 
		}

		Committer committer = g.fromJson(res, Committer.class);
		Assert.assertEquals(1, committer.getIdStaff());
		Assert.assertEquals(LocalDate.of(2021, 1, 1), committer.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2021, 1, 2), committer.getLastCommit());
		Assert.assertEquals(1, committer.getNumberOfCommits());
		Assert.assertEquals(2, committer.getNumberOfFiles());
		Assert.assertEquals("loginTest", committer.getLogin());
		Assert.assertEquals("test", committer.getPseudo());
	}

}
