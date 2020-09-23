package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.emfatic.core.EmfaticResourceFactory;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;
import org.eclipse.uml2.types.TypesPackage;
import org.eclipse.uml2.uml.UMLPackage;

public class MeasureElementCount {

	public static void main(String[] args) throws Exception {

		initRegistry();

		PrintWriter elementCountCSV = new PrintWriter("plotScripts/ecoregithub_elementCount.csv");
		String header = "Model,XMI,PlainFlexmi,TemplatesFlexmi,Emfatic";
		elementCountCSV.println(header);

		measureElementCount(elementCountCSV);

		elementCountCSV.close();
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

	private static void measureElementCount(PrintWriter loadTimesCSV) {
		try (Stream<Path> walk = Files.walk(Paths.get("models/downloaded/"))) {

			List<String> files = walk.filter(Files::isRegularFile).map(x -> x.toString())
					.filter(x -> x.endsWith("ecore")).collect(Collectors.toList());

			// for constrained set of models
			//			files = Arrays.asList("models/downloaded/odata/odata.ecore");

			for (String ecoreFile : files) {
				String line = getElementsCountLine(ecoreFile);
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

	private static String getElementsCountLine(String ecoreFile) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getModelName(ecoreFile));
		sb.append(",");

		sb.append(measureEcoreCount(ecoreFile));
		sb.append(",");

		String plainFlexmiFile =
				String.format(TransformGithubEcoreModels.PLAIN_FLEXMI_PATTERN, ecoreFile);
		sb.append(measureFlexmiCount(plainFlexmiFile));
		sb.append(",");

		String templateFlexmiFile =
				String.format(TransformGithubEcoreModels.TEMPLATE_FLEXMI_PATTERN, ecoreFile);
		sb.append(measureFlexmiCount(templateFlexmiFile));
		sb.append(",");

		sb.append(measureEmfaticCount(ecoreFile.replaceAll("ecore$", "emf")));

		return sb.toString();
	}

	private static long measureEcoreCount(String ecoreFile) throws IOException {
		Resource resource = createResource(new XMIResourceFactoryImpl(),
				URI.createFileURI(new File(ecoreFile).getAbsolutePath()));
		resource.load(null);
		EcoreUtil.resolveAll(resource);
		return countElements(resource);
	}

	private static long measureEmfaticCount(String emfaticFile) throws IOException {
		Resource resource = createResource(new EmfaticResourceFactory(),
				URI.createFileURI(new File(emfaticFile).getAbsolutePath()));
		resource.load(null);
		EcoreUtil.resolveAll(resource);
		return countElements(resource);
	}

	private static long measureFlexmiCount(String flexmiFile) throws IOException {
		Resource resource = createResource(new FlexmiResourceFactory(),
				URI.createFileURI(new File(flexmiFile).getAbsolutePath()));

		resource.load(null);
		EcoreUtil.resolveAll(resource);
		return countElements(resource);
	}

	private static String getModelName(String ecoreFile) {
		String[] segments = ecoreFile.split("/");
		return segments[segments.length - 1];
	}

	private static Resource createResource(Resource.Factory resourceFactory, URI uri) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);

		return resourceSet.createResource(uri);
	}

	private static long countElements(Resource resource) {
		System.out.println(resource.getURI());
		int numElements = 0;
		Iterator<EObject> it = resource.getAllContents();
		Map<String, Integer> typeCounter = new HashMap<>();
		boolean firstPackage = true;
		while (it.hasNext()) {
			EObject next = it.next();
			countByType(typeCounter, next);
			if (next instanceof EPackage) {
				if (firstPackage) {
					firstPackage = false;
				}
				else {
					System.out.println("Elements by type:");
					System.out.println(typeCounter);
					System.out.println();
				}
				System.out.println(next);
				System.out.println("Elements start: " + numElements);
				typeCounter = new HashMap<>();
			}
			numElements++;
		}
		System.out.println("Elements end:" + numElements);
		System.out.println("Elements by type:");
		System.out.println(typeCounter);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n\n");

		return numElements;
	}

	private static void countByType(Map<String, Integer> typeCounter, EObject next) {
		if (typeCounter.containsKey(next.eClass().getName())) {
			typeCounter.put(next.eClass().getName(), typeCounter.get(next.eClass().getName()) + 1);
		}
		else {
			typeCounter.put(next.eClass().getName(), 1);
		}
	}
}
