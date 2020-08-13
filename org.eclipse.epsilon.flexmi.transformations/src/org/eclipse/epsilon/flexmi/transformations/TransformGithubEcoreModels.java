package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.flexmi.FlexmiResource;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;

public class TransformGithubEcoreModels {

	public static final String PLAIN_FLEXMI_PATTERN = "%s-plain.flexmi";
	public static final String TEMPLATE_FLEXMI_PATTERN = "%s-template.flexmi";

	public static void main(String[] args) throws Exception {

		EcorePackage.eINSTANCE.eClass();
		FlexmiModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("*", new XMIResourceFactoryImpl());

		boolean debug = false; // true -> creates intermediate flexmi models

		try (Stream<Path> walk = Files.walk(Paths.get("models/downloaded/"))) {

			List<String> files = walk.filter(Files::isRegularFile).map(x -> x.toString())
					.filter(x -> x.endsWith("ecore")).collect(Collectors.toList());

			// uncomment for testing on a single ecore
			//			files = Arrays.asList("models/downloaded/Unbalanced/Unbalanced.ecore");

			int currentFile = 1;
			int totalFiles = files.size();

			for (String path : files) {

				String ecoreModel = path;
				System.out.println(String.format("(%d/%d) %s",
						currentFile, totalFiles, ecoreModel));
				currentFile++;

				PlainFlexmiTransformer plainTransformer = new PlainFlexmiTransformer();
				FlexmiModel plainModel = plainTransformer.getFlexmiModel(ecoreModel);
				if (debug) {
					String plainFlexmiModel = String.format("%s-plain.model", path);
					saveFlexmiModel(plainFlexmiModel, plainModel);
				}
				String plainFlexmiFile = String.format(PLAIN_FLEXMI_PATTERN, path);
				String plainFlexmiFileContents = plainTransformer.getFlexmiFile(plainModel);

				saveFlexmiFile(plainFlexmiFile, plainFlexmiFileContents);
				showWarnings(plainFlexmiFile);

				TemplateFlexmiTransformer templateTransformer = new TemplateFlexmiTransformer();
				FlexmiModel templateModel = templateTransformer.getFlexmiModel(ecoreModel);
				templateModel.getIncludes().add("../../../templates/ecoreTemplates.flexmi");
				if (debug) {
					String templateFlexmiModel = String.format("%s-template.model", path);
					saveFlexmiModel(templateFlexmiModel, templateModel);
				}
				String templateFlexmiFile = String.format(TEMPLATE_FLEXMI_PATTERN, path);
				String templateFlexmiFileContents = templateTransformer.getFlexmiFile(templateModel);

				saveFlexmiFile(templateFlexmiFile, templateFlexmiFileContents);
				showWarnings(templateFlexmiFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}


	private static void saveFlexmiModel(String modelPath, FlexmiModel model) {
		// save flexmi model
		ResourceSet resSet = new ResourceSetImpl();
		Resource flexmiModelResource = resSet.createResource(URI.createURI(modelPath));
		flexmiModelResource.getContents().add(model);
		try {
			flexmiModelResource.save(Collections.EMPTY_MAP);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveFlexmiFile(String filePath, String fileContents) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filePath);
		out.println(fileContents);
		out.close();
	}

	private static FlexmiResource loadFlexmiResource(String filename) throws Exception {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("flexmi", new FlexmiResourceFactory());
		resourceSet.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		FlexmiResource resource = (FlexmiResource) resourceSet.createResource(URI.createFileURI(new File(filename).getAbsolutePath()));
		resource.load(null);
		return resource;
	}

	private static void showWarnings(String flexmiFile) throws Exception {
		List<Diagnostic> warnings = getWarnings(flexmiFile);
		if (warnings != null && !warnings.isEmpty()) {
			System.out.println("Warnings found in " + flexmiFile);
			for (Diagnostic warning : warnings) {
				System.out.println(String.format("\t(line %d) %s",
						warning.getLine(), warning.getMessage()));
			}
			System.out.println();
		}
	}

	private static List<Diagnostic> getWarnings(String flexmiFile) throws Exception {
		FlexmiResource resource = loadFlexmiResource(flexmiFile);
		if (resource != null) {
			return resource.getWarnings();
		}
		return null;
	}
}
