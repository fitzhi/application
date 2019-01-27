package fr.skiller.data.internal;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UnknownCommiterJSONTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	@Test
	public void test_toJson () {
		Set<Unknown> undefinedContributors = new HashSet<Unknown>();
		undefinedContributors.add(new Unknown("A"));
		undefinedContributors.add(new Unknown("C"));
		undefinedContributors.add(new Unknown("B"));
		undefinedContributors.add(new Unknown("a"));
		Assert.assertTrue( gson.toJson(undefinedContributors).indexOf("{\"login\":\"A\"}") != -1);
		Assert.assertTrue( gson.toJson(undefinedContributors).indexOf("{\"login\":\"C\"}") != -1);
		Assert.assertTrue( gson.toJson(undefinedContributors).indexOf("{\"login\":\"B\"}") != -1);
		Assert.assertTrue( gson.toJson(undefinedContributors).indexOf("{\"login\":\"a\"}") != -1);
	}
}
