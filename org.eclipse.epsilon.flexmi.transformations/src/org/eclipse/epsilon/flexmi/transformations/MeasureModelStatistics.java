package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
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
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.uml2.types.TypesPackage;
import org.eclipse.uml2.uml.UMLPackage;

public class MeasureModelStatistics {

	public static void main(String[] args) throws Exception {

		initRegistry();

		PrintWriter elementCountCSV = new PrintWriter("plotScripts/modelStatistics.csv");
		String header = "Model,classes,attributes,references,enums,annotations";
		elementCountCSV.println(header);

		ArrayList<String> excludedFiles = new ArrayList<String>();
		Scanner s = new Scanner(new File(TransformAmmoreModels.EXCLUDED_METAMODELS_FILE));
		while (s.hasNext()) {
			excludedFiles.add(s.next());
		}
		s.close();

		measureElementCount(elementCountCSV, "models/ammore2020-barriga", excludedFiles);

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

	private static void measureElementCount(PrintWriter loadTimesCSV,
			String path, List<String> excludedFiles) {

		try (Stream<Path> walk = Files.walk(Paths.get(path))) {

			List<String> files = walk.filter(Files::isRegularFile)
					.map(f -> f.toString())
					.filter(f -> f.endsWith("ecore"))
					.filter(f -> !excludedFiles.contains(f))
					.collect(Collectors.toList());

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

		Resource resource = createResource(new XMIResourceFactoryImpl(),
				URI.createFileURI(new File(ecoreFile).getAbsolutePath()));
		resource.load(null);

		int numClasses = 0, numAttributes = 0, numReferences = 0, numEnums = 0, numAnnotations = 0;
		Iterator<EObject> it = resource.getAllContents();
		while (it.hasNext()) {
			switch (it.next().eClass().getName()) {
			case "EClass":
				numClasses++;
				break;
			case "EAttribute":
				numAttributes++;
				break;
			case "EReference":
				numReferences++;
				break;
			case "EEnum":
				numEnums++;
			case "EAnnotation":
				numAnnotations++;
			default:
			}
		}

		sb.append(numClasses);
		sb.append(",");

		sb.append(numAttributes);
		sb.append(",");

		sb.append(numReferences);
		sb.append(",");

		sb.append(numEnums);
		sb.append(",");

		sb.append(numAnnotations);

		return sb.toString();
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
}
