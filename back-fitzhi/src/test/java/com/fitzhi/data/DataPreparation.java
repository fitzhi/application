package com.fitzhi.data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.data.source.CommitRepository;

/**
 * <p>
 * Shuffle the project Sunburst-data file for anonymous purpose.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "cache.working.dir=./src/test/resources/cacheDirRepository/", "cache_duration=100000" }) 
public class DataPreparation {

	@Autowired
	ShuffleService shuffleService;

	@Autowired
	CacheDataHandler cacheDataHandler;
	
	private int ID_PROJECT = 3;
	
	private String LIB_PROJECT = "VEGEO";

	@Test
	public void testAnonymizeInputFile() throws IOException {
		Project project = new Project(ID_PROJECT, LIB_PROJECT);
		CommitRepository repository = cacheDataHandler.getRepository(project);	
		Assert.assertFalse(repository.getRepository().isEmpty());
		String[] keys = repository.getRepository().keySet().toArray(new String[0]);
		for (String key : keys) {
			String shuffleKey = shuffleService.scramble(key);
			CommitHistory history = repository.getRepository().get(key);
			System.out.println(key + " -> " + shuffleKey);
			history.setSourcePath(shuffleKey);
			history.operations.forEach(action -> {
				action.setAuthorName(shuffleService.scramble(action.getAuthorName()));
			});
			repository.getRepository().remove(key);
			repository.getRepository().put(shuffleKey, history);
		}
		
		Set<String> scrambledGhosts = new HashSet<>();
		repository.unknownContributors().stream().forEach(ghost -> {
			scrambledGhosts.add(shuffleService.scramble(ghost));
		});
		repository.setUnknownContributors(scrambledGhosts);
		
		cacheDataHandler.saveRepository(project, repository);
	}
}
