package org.eclipse.epsilon.flexmi.workshopBenchmark;

import java.io.PrintWriter;
import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;
import org.eclipse.epsilon.profiling.Stopwatch;

/**
 * Benchmark of one of the initial workshop papers
 */
public class WorkshopBenchmark {
	
	public static void main(String[] args) throws Exception {
		new WorkshopBenchmark().run();
	}
	
	public void run() throws Exception {
		PrintWriter loadTimesCSV = new PrintWriter("plotScripts/workshop_loadtimes.csv");
		loadTimesCSV.println("Flexmi,XMI,ModelElements");
		int numReps = 15;
		int warmReps = 5;
		
		for (int rep = 0; rep < warmReps; rep++) {
			System.out.println("Warm " + rep);
			runRep(null);
		}

		for (int rep = 0; rep < numReps; rep++) {
			System.out.println("Rep " + rep);
			runRep(loadTimesCSV);
		}
		loadTimesCSV.close();
		System.out.println("done");
	}

	private void runRep(PrintWriter loadTimesCSV) throws Exception {
		String filesPath = "/home/fonso/workspaces/flexmi-models/models-and-generator/generated-";
		String[] extensions = new String[] { "10", "100", "1000", "2000", "4000", "5000", "6000", "8000", "10000" };
		//		String[] extensions = new String[] { "5000" };
		for (String extension : extensions) {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.resume();
			createResource(new FlexmiResourceFactory(),
					URI.createFileURI(filesPath + extension + ".flexmi")).load(null);
			stopwatch.pause();
			if (loadTimesCSV != null) {
				loadTimesCSV.print(stopwatch.getElapsed() + ", ");
			}
			stopwatch = new Stopwatch();
			stopwatch.resume();
			Resource resource = createResource(new XMIResourceFactoryImpl(), URI.createFileURI(filesPath + extension + ".xmi"));
			resource.load(null);
			stopwatch.pause();
			if (loadTimesCSV != null) {
				loadTimesCSV.print(stopwatch.getElapsed() + ", ");
				int numElements = 0;
				Iterator<EObject> it = resource.getAllContents();
				while (it.hasNext()) {
					it.next();
					numElements++;
				}
				loadTimesCSV.println(numElements);
			}
		}
	}

	public Resource createResource(Resource.Factory resourceFactory, URI uri) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory);
		return resourceSet.createResource(uri);
	}
	
}
