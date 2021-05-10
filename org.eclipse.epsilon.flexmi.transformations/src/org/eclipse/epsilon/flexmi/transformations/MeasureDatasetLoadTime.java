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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.emfatic.core.EmfaticResourceFactory;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;
import org.eclipse.epsilon.profiling.Stopwatch;
import org.eclipse.uml2.types.TypesPackage;
import org.eclipse.uml2.uml.UMLPackage;

public class MeasureDatasetLoadTime {

	public static void main(String[] args) throws Exception {

		initRegistry();

		PrintWriter loadTimesCSV = new PrintWriter("plotScripts/datasetloadtimes.csv");
		String header = "XMI,PlainFlexmi,TemplatesFlexmi,Emfatic";
		loadTimesCSV.println(header);

		List<String> excludedMetamodels = new ArrayList<>();
		Scanner s = new Scanner(new File(TransformAmmoreModels.EXCLUDED_METAMODELS_FILE));
		while (s.hasNext()) {
			excludedMetamodels.add(s.next());
		}
		s.close();
		
		List<String> ecoreFiles = null;
		try (Stream<Path> walk = Files.walk(Paths.get("models/ammore2020-barriga"))) {

			ecoreFiles = walk.filter(Files::isRegularFile)
					.map(f -> f.toString())
					.filter(f -> f.endsWith("ecore"))
					.filter(f -> !excludedMetamodels.contains(f))
					.collect(Collectors.toList());

			// for a constrained set of models
			//			files = Arrays.asList("models/downloaded/esb/esb.ecore");
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		List<String> plainFlexmiFiles = ecoreFiles.stream()
				.map(ecoreFile -> String.format(TransformAmmoreModels.PLAIN_FLEXMI_PATTERN, ecoreFile))
				.collect(Collectors.toList());

		List<String> templateFlexmiFiles = ecoreFiles.stream()
				.map(ecoreFile -> String.format(TransformAmmoreModels.TEMPLATE_FLEXMI_PATTERN, ecoreFile))
				.collect(Collectors.toList());

		List<String> emfaticFiles = ecoreFiles.stream()
				.map(ecoreFile -> String.format(TransformAmmoreModels.EMFATIC_PATTERN, ecoreFile))
				.collect(Collectors.toList());

		int warmReps = 20;
		for (int rep = 0; rep < warmReps; rep++) {
			System.out.println("WarmRep " + rep);
			System.out.println(getLoadTimesLine(ecoreFiles, emfaticFiles, plainFlexmiFiles, templateFlexmiFiles));
		}

		int numReps = 20;
		for (int rep = 0; rep < numReps; rep++) {
			System.out.println("Rep " + rep);
			String line = getLoadTimesLine(ecoreFiles, emfaticFiles, plainFlexmiFiles, templateFlexmiFiles);
			loadTimesCSV.println(line);
			System.out.println(line);
		}

		loadTimesCSV.close();
		System.out.println("Done");
		Arrays.asList("");
	}

	private static void initRegistry() {
		EPackage.Registry globalRegistry = EPackage.Registry.INSTANCE;

		globalRegistry.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		globalRegistry.put(GenModelPackage.eINSTANCE.getNsURI(), GenModelPackage.eINSTANCE);

		globalRegistry.put(XMLNamespacePackage.eINSTANCE.getNsURI(), XMLNamespacePackage.eINSTANCE);
		globalRegistry.put(XMLTypePackage.eINSTANCE.getNsURI(), XMLTypePackage.eINSTANCE);

		globalRegistry.put("http://www.eclipse.org/uml2/5.0.0/UML", UMLPackage.eINSTANCE);
		globalRegistry.put("http://www.eclipse.org/uml2/5.0.0/Types", TypesPackage.eINSTANCE);
		globalRegistry.put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		globalRegistry.put("http://www.eclipse.org/uml2/4.0.0/Types", TypesPackage.eINSTANCE);
	}

	private static String getLoadTimesLine(List<String> ecoreFiles,
			List<String> emfaticFiles, List<String> plainFlexmiFiles,
			List<String> templateFlexmiFiles) throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append(measureDatasetLoad(ecoreFiles, new XMIResourceFactoryImpl()));
		sb.append(",");

		sb.append(measureDatasetLoad(plainFlexmiFiles, new FlexmiResourceFactory()));
		sb.append(",");

		sb.append(measureDatasetLoad(templateFlexmiFiles, new FlexmiResourceFactory()));
		sb.append(",");

		sb.append(measureDatasetLoad(emfaticFiles, new EmfaticResourceFactory()));

		return sb.toString();
	}

	private static long measureDatasetLoad(List<String> files, Resource.Factory resourceFactory) throws IOException {

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.resume();
		for (String file : files) {
			Resource resource = createResource(resourceFactory,
					URI.createFileURI(new File(file).getAbsolutePath()));
			resource.load(null);
		}
		stopwatch.pause();

		return stopwatch.getElapsed(TimeUnit.NANOSECONDS);
	}

	private static Resource createResource(Resource.Factory resourceFactory, URI uri) {
		return createResourceSet(resourceFactory).createResource(uri);
	}

	private static ResourceSet createResourceSet(Resource.Factory resourceFactory) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);
		return resourceSet;
	}
}
