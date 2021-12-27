package com.fitzhi;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.data.internal.Project;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class GenerateSonarCliLauncher {
	
	public static void main(String[] args) {
		System.out.println("Projects file " + args[0]);
		System.out.println("Destination location " + args[1]);
		File file = new File(args[0]);
		if (!file.exists()) {
			System.out.println(args[0] + " does not exist.");
		}

		Gson gson = new Gson();
		Map<Integer, Project> projects = new HashMap<>();
		try (FileReader fr = new FileReader(file)) {
			Type listProjectsType = new TypeToken<HashMap<Integer, Project>>() {
			}.getType();
			projects = gson.fromJson(fr, listProjectsType);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		for (Project project : projects.values()) {
			if (project.getLocationRepository() != null) {
				sb.append("~/work/projects/application/docker/run-sonar-cli.sh").append(" ")
						.append(project.getLocationRepository()).append(" ").append(project.getName().replace(" ", "-"))
						.append("\n");
			}
		}
		try (FileWriter fw = new FileWriter(new File(args[1] + "/all-run-sonar-cli.sh"))) {
			fw.write(sb.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
