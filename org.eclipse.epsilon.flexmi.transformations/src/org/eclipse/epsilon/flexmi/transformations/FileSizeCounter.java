package org.eclipse.epsilon.flexmi.transformations;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSizeCounter {

	public static void main(String[] args) throws Exception {
		PrintWriter locCSV = new PrintWriter("models/0-LOC.csv");
		PrintWriter bytesCSV = new PrintWriter("models/0-bytes.csv");

		String header = "Model,XMI,PlainFlexmi,TemplatesFlexmi,Emfatic";
		locCSV.println(header);
		bytesCSV.println(header);

		try (Stream<Path> walk = Files.walk(Paths.get("models/downloaded/"))) {

			List<String> files = walk.filter(Files::isRegularFile).map(x -> x.toString())
					.filter(x -> x.endsWith("ecore")).collect(Collectors.toList());

			for (String ecoreFile : files) {
				locCSV.println(getLOCline(ecoreFile));
				bytesCSV.println(getBytesline(ecoreFile));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		locCSV.close();
		bytesCSV.close();
		System.out.println("Done");
	}

	private static String getLOCline(String ecoreFile) throws Exception {

		List<String> files = getFiles(ecoreFile);
		List<String> lineSegments = new ArrayList<>();
		lineSegments.add(getModelName(ecoreFile));
		
		for (String file : files) {
			String fileContents = new String(Files.readAllBytes(Paths.get(file)));
			lineSegments.add("" + countLOC(fileContents));
		}
		return String.join(",", lineSegments);
	}

	private static String getBytesline(String ecoreFile) throws Exception {

		List<String> files = getFiles(ecoreFile);
		List<String> lineSegments = new ArrayList<>();
		lineSegments.add(getModelName(ecoreFile));

		for (String file : files) {
			String fileContents = new String(Files.readAllBytes(Paths.get(file)));
			lineSegments.add("" + countBytes(fileContents));
		}
		return String.join(",", lineSegments);
	}

	private static String getModelName(String ecoreFile) {
		String[] segments = ecoreFile.split("/");
		return segments[segments.length - 1];
	}

	private static List<String> getFiles(String ecoreFile) {
		String emfaticFile = ecoreFile.replaceAll("ecore$", "emf");
		String plainFlexmiFile =
				String.format(TransformGithubEcoreModels.PLAIN_FLEXMI_PATTERN, ecoreFile);
		String templateFlexmiFile =
				String.format(TransformGithubEcoreModels.TEMPLATE_FLEXMI_PATTERN, ecoreFile);
		return Arrays.asList(ecoreFile, plainFlexmiFile, templateFlexmiFile, emfaticFile);
	}

	private static long countLOC(String fileContents) {
		return Arrays.asList(fileContents.split("\\r?\\n")).stream()
				.filter(line -> !line.trim().equals(""))
				.collect(Collectors.counting());
	}

	private static int countBytes(String fileContents) {
		return fileContents.replaceAll("\\s+", " ").length();
	}
}
