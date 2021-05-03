package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.emfatic.core.EmfaticResourceFactory;
import org.eclipse.epsilon.flexmi.FlexmiResource;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;

public class TransformAmmoreModels {

	public static final String PLAIN_FLEXMI_PATTERN = "%s-plain.flexmi";
	public static final String TEMPLATE_FLEXMI_PATTERN = "%s-template.flexmi";
	public static final String EMFATIC_PATTERN = "%s.emf";

	public static Set<String> filesWithFlexmiWarnings = new HashSet<>();
	public static Set<String> emfaticError = new HashSet<>();
	public static Set<String> ecoreLoadError = new HashSet<>();

	public static void main(String[] args) throws Exception {

		EcorePackage.eINSTANCE.eClass();
		FlexmiModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("*", new XMIResourceFactoryImpl());

		boolean debug = false; // true -> creates intermediate flexmi models

		try (Stream<Path> walk = Files.walk(Paths.get("models/ammore2020-barriga/"))) {

			List<String> files = walk.filter(Files::isRegularFile).map(x -> x.toString())
					.filter(x -> x.endsWith("ecore")).collect(Collectors.toList());

			// uncomment for testing on a single ecore
			//			files = Arrays.asList("models/ammore2020-barriga/books.ecore");

			int currentFile = 1;
			int totalFiles = files.size();

			for (String ecorePath : files) {

				System.out.println(String.format("(%d/%d) %s",
						currentFile, totalFiles, ecorePath));
				currentFile++;

				// load ecore and check for issues
				Resource ecoreResource = createResource(new XMIResourceFactoryImpl(),
						URI.createFileURI(new File(ecorePath).getAbsolutePath()));
				try {
					ecoreResource.load(null);
					if (!ecoreResource.getErrors().isEmpty()) {
						ecoreLoadError.add(ecorePath);
						System.out.println(ecoreResource.getErrors());
					}
				}
				catch (RuntimeException e) {
					ecoreLoadError.add(ecorePath);
					e.printStackTrace();
					continue;
				}

				String emfaticPath = String.format(EMFATIC_PATTERN, ecorePath);
				Resource emfaticResource = createResource(new EmfaticResourceFactory(),
						URI.createFileURI(new File(emfaticPath).getAbsolutePath()));

				emfaticResource.getContents().addAll(ecoreResource.getContents());
				try {
					emfaticResource.save(null);
					
					emfaticResource = createResource(new EmfaticResourceFactory(),
							URI.createFileURI(new File(emfaticPath).getAbsolutePath()));
					emfaticResource.load(null);
				}
				catch (RuntimeException e) {
					System.out.println(emfaticPath);
					e.printStackTrace();
					emfaticError.add(ecorePath);
				}

				PlainFlexmiTransformer plainTransformer = new PlainFlexmiTransformer();
				FlexmiModel plainModel = null;
				try {
					plainModel = plainTransformer.getFlexmiModel(ecorePath);
				}
				catch (RuntimeException e) {
					e.printStackTrace();
					filesWithFlexmiWarnings.add(ecorePath);
					continue;
				}

				if (debug) {
					String plainFlexmiModel = String.format("%s-plain.model", ecorePath);
					saveFlexmiModel(plainFlexmiModel, plainModel);
				}
				String plainFlexmiFile = String.format(PLAIN_FLEXMI_PATTERN, ecorePath);
				String plainFlexmiFileContents = plainTransformer.getFlexmiFile(plainModel);

				saveFlexmiFile(plainFlexmiFile, plainFlexmiFileContents);
				showWarnings(ecorePath, plainFlexmiFile);

				TemplateFlexmiTransformer templateTransformer = new TemplateFlexmiTransformer();
				FlexmiModel templateModel = templateTransformer.getFlexmiModel(ecorePath);
				templateModel.getIncludes().add("../../templates/ecoreTemplates.flexmi");
				if (debug) {
					String templateFlexmiModel = String.format("%s-template.model", ecorePath);
					saveFlexmiModel(templateFlexmiModel, templateModel);
				}
				String templateFlexmiFile = String.format(TEMPLATE_FLEXMI_PATTERN, ecorePath);
				String templateFlexmiFileContents = templateTransformer.getFlexmiFile(templateModel);

				saveFlexmiFile(templateFlexmiFile, templateFlexmiFileContents);
				showWarnings(ecorePath, templateFlexmiFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeCollection(ecoreLoadError, "output/ecoreLoadError.txt");
		writeCollection(emfaticError, "output/emfaticError.txt");
		writeCollection(filesWithFlexmiWarnings, "output/filesWithFlexmiWarnings.txt");
		
		Set<String> metamodelsWithErrors = new HashSet<>();
		metamodelsWithErrors.addAll(ecoreLoadError);
		metamodelsWithErrors.addAll(emfaticError);
		metamodelsWithErrors.addAll(filesWithFlexmiWarnings);
		writeCollection(metamodelsWithErrors, "output/metamodelsWithErrors.txt");

		System.out.println("Done");
	}


	private static void writeCollection(Collection<String> items, String outputFile) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(outputFile);
		PrintWriter pw = new PrintWriter(fos);

		for (String item : items) {
			pw.println(item);
		}
		pw.close();
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

	private static void showWarnings(String ecorePath, String flexmiFile) throws Exception {
		List<Diagnostic> warnings = getWarnings(flexmiFile);
		if (warnings != null && !warnings.isEmpty()) {
			filesWithFlexmiWarnings.add(ecorePath);
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

	private static Resource createResource(Resource.Factory resourceFactory, URI uri) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);

		return resourceSet.createResource(uri);
	}
}
