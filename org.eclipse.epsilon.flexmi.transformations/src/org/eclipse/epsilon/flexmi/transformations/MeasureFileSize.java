package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MeasureFileSize {

	public static void main(String[] args) throws Exception {
		PrintWriter locCSV = new PrintWriter("plotScripts/LOC.csv");
		PrintWriter bytesCSV = new PrintWriter("plotScripts/bytes.csv");

		String header = "Model,XMI,HUTN,PlainXMLFlexmi,PlainYAMLFlexmi,TemplatesXMLFlexmi,TemplatesYAMLFlexmi,Emfatic";
		locCSV.println(header);
		bytesCSV.println(header);

		ArrayList<String> excludedFiles = new ArrayList<String>();
		Scanner s = new Scanner(new File("output/excludedMetamodels.txt"));
		while (s.hasNext()) {
			excludedFiles.add(s.next());
		}
		s.close();

		try (Stream<Path> walk = Files.walk(Paths.get("models/ammore2020-barriga"))) {

			List<String> files = walk.filter(Files::isRegularFile)
					.map(f -> f.toString())
					.filter(f -> f.endsWith("ecore"))
					.filter(f -> !excludedFiles.contains(f))
					.collect(Collectors.toList());

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
		String hutnFile = String.format(TransformAmmoreModels.HUTN_PATTERN, ecoreFile);
		String plainXMLFlexmiFile =
				String.format(TransformAmmoreModels.PLAIN_FLEXMI_XML_PATTERN, ecoreFile);
		String plainYAMLFile =
				String.format(TransformAmmoreModels.PLAIN_FLEXMI_YAML_PATTERN, ecoreFile);
		String templateXMLFlexmiFile =
				String.format(TransformAmmoreModels.TEMPLATE_FLEXMI_XML_PATTERN, ecoreFile);
		String templateYAMLFlexmiFile =
				String.format(TransformAmmoreModels.TEMPLATE_FLEXMI_YAML_PATTERN, ecoreFile);
		String emfaticFile = String.format(TransformAmmoreModels.EMFATIC_PATTERN, ecoreFile);

		return Arrays.asList(ecoreFile, hutnFile, plainXMLFlexmiFile, plainYAMLFile, templateXMLFlexmiFile, templateYAMLFlexmiFile, emfaticFile);
	}

	private static long countLOC(String fileContents) {
		return Arrays.asList(fileContents.split("\\r?\\n")).stream()
				.filter(line -> !(line.trim().length() == 0))
				.collect(Collectors.counting());
	}

	private static int countBytes(String fileContents) {
		return fileContents.replaceAll("\\s+", "").length();
	}
}
