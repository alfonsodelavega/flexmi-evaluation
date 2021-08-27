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

public class MeasureLoadTimes {

	public static void main(String[] args) throws Exception {

		initRegistry();

		PrintWriter loadTimesCSV = new PrintWriter("plotScripts/loadtimes.csv");
		String header = "Model,XMI,PlainFlexmi,TemplatesFlexmi,Emfatic";
		loadTimesCSV.println(header);

		ArrayList<String> excludedFiles = new ArrayList<String>();
		Scanner s = new Scanner(new File(TransformAmmoreModels.EXCLUDED_METAMODELS_FILE));
		while (s.hasNext()) {
			excludedFiles.add(s.next());
		}
		s.close();

		int warmReps = 20;
		for (int rep = 0; rep < warmReps; rep++) {
			measureLoadTimes(null, "models/ammore2020-barriga", excludedFiles);
		}

		int numReps = 20;
		for (int rep = 0; rep < numReps; rep++) {
			System.out.println("Rep " + rep);
			measureLoadTimes(loadTimesCSV, "models/ammore2020-barriga", excludedFiles);
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

	private static void measureLoadTimes(PrintWriter loadTimesCSV, String path,
			List<String> excludedFiles) {

		try (Stream<Path> walk = Files.walk(Paths.get(path))) {

			List<String> files = walk.filter(Files::isRegularFile)
					.map(f -> f.toString())
					.filter(f -> f.endsWith("ecore"))
					.filter(f -> !excludedFiles.contains(f))
					.collect(Collectors.toList());

			// for constrained set of models
			//			files = Arrays.asList("models/downloaded/esb/esb.ecore");

			for (String ecoreFile : files) {
				String line = getLoadTimesLine(ecoreFile);
				if (loadTimesCSV != null) {
					loadTimesCSV.println(line);
				}
				System.out.println("\t" + line);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getLoadTimesLine(String ecoreFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getModelName(ecoreFile));
		sb.append(",");

		sb.append(measureEcoreLoad(ecoreFile));
		sb.append(",");

		String plainFlexmiFile =
				String.format(TransformAmmoreModels.PLAIN_FLEXMI_XML_PATTERN, ecoreFile);
		sb.append(measureFlexmiLoad(plainFlexmiFile));
		sb.append(",");

		String templateFlexmiFile =
				String.format(TransformAmmoreModels.TEMPLATE_FLEXMI_XML_PATTERN, ecoreFile);
		sb.append(measureFlexmiLoad(templateFlexmiFile));
		sb.append(",");

		String emfaticFile =
				String.format(TransformAmmoreModels.EMFATIC_PATTERN, ecoreFile);
		sb.append(measureEmfaticLoad(emfaticFile));

		return sb.toString();
	}

	private static long measureEcoreLoad(String ecoreFile) throws IOException {
		Stopwatch stopwatch = new Stopwatch();
		Resource resource = createResource(new XMIResourceFactoryImpl(),
				URI.createFileURI(new File(ecoreFile).getAbsolutePath()), stopwatch);
		resource.load(null);
		stopwatch.pause();
		return stopwatch.getElapsed(TimeUnit.NANOSECONDS);
	}

	private static long measureEmfaticLoad(String emfaticFile) throws IOException {
		Stopwatch stopwatch = new Stopwatch();
		Resource resource = createResource(new EmfaticResourceFactory(),
				URI.createFileURI(new File(emfaticFile).getAbsolutePath()), stopwatch);
		resource.load(null);
		stopwatch.pause();
		return stopwatch.getElapsed(TimeUnit.NANOSECONDS);
	}

	private static long measureFlexmiLoad(String flexmiFile) throws IOException {
		Stopwatch stopwatch = new Stopwatch();
		Resource resource = createResource(new FlexmiResourceFactory(),
				URI.createFileURI(new File(flexmiFile).getAbsolutePath()), stopwatch);

		resource.load(null);
		stopwatch.pause();
		return stopwatch.getElapsed(TimeUnit.NANOSECONDS);
	}

	private static String getModelName(String ecoreFile) {
		String[] segments = ecoreFile.split("/");
		return segments[segments.length - 1];
	}

	private static Resource createResource(Resource.Factory resourceFactory, URI uri, Stopwatch stopwatch) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);

		stopwatch.resume();

		return resourceSet.createResource(uri);
	}
}
