package org.eclipse.epsilon.flexmi.transformations;

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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;

public class TransformGithubEcoreModels {
	public static void main(String[] args) throws Exception {

		try (Stream<Path> walk = Files.walk(Paths.get("models/downloaded/"))) {

			List<String> files = walk.filter(Files::isRegularFile).map(x -> x.toString())
					.filter(x -> x.endsWith("ecore")).collect(Collectors.toList());

			for (String path : files) {

				String ecoreModel = path;

				String flexmiModel = String.format("%s.model", path);
				if (Files.exists(Paths.get(flexmiModel))) {
					Files.delete(Paths.get(flexmiModel));
				}

				String flexmiFile = String.format("%s.flexmi", path);
				if (Files.exists(Paths.get(flexmiFile))) {
					Files.delete(Paths.get(flexmiFile));
				}

				EcorePackage.eINSTANCE.eClass();
				FlexmiModelPackage.eINSTANCE.eClass();

				Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
				Map<String, Object> m = reg.getExtensionToFactoryMap();
				m.put("*", new XMIResourceFactoryImpl());

				PlainFlexmiTransformer plainTransformer = new PlainFlexmiTransformer();
				FlexmiModel model = plainTransformer.getFlexmiModel(ecoreModel);
				String plainFlexmi = plainTransformer.getFlexmiFile(model);
				System.out.println(plainFlexmi);

				// save flexmi model
				ResourceSet resSet = new ResourceSetImpl();
				Resource flexmiModelResource = resSet.createResource(URI.createURI(flexmiModel));
				flexmiModelResource.getContents().add(model);
				try {
					flexmiModelResource.save(Collections.EMPTY_MAP);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// save flexmi file
				PrintWriter out = new PrintWriter(flexmiFile);
				out.println(plainFlexmi);
				out.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
