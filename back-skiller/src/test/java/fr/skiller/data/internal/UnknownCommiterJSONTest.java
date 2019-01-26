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
		Assert.assertEquals("[{\"login\":\"B\"},{\"login\":\"A\"},{\"login\":\"C\"},{\"login\":\"a\"}]", gson.toJson(undefinedContributors));
	}
}
