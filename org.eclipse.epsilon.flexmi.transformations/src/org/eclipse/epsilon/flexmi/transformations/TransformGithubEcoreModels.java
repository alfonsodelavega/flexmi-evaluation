package org.eclipse.epsilon.flexmi.transformations;

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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;

public class TransformGithubEcoreModels {
	public static void main(String[] args) throws Exception {

		EcorePackage.eINSTANCE.eClass();
		FlexmiModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("*", new XMIResourceFactoryImpl());

		boolean debug = true;

		try (Stream<Path> walk = Files.walk(Paths.get("models/downloaded/"))) {

			List<String> files = walk.filter(Files::isRegularFile).map(x -> x.toString())
					.filter(x -> x.endsWith("ecore")).collect(Collectors.toList());

			for (String path : files) {

				String ecoreModel = path;
				System.out.println(ecoreModel);

				PlainFlexmiTransformer plainTransformer = new PlainFlexmiTransformer();
				FlexmiModel plainModel = plainTransformer.getFlexmiModel(ecoreModel);
				if (debug) {
					String plainFlexmiModel = String.format("%s-plain.model", path);
					if (Files.exists(Paths.get(plainFlexmiModel))) {
						Files.delete(Paths.get(plainFlexmiModel));
					}
					saveFlexmiModel(plainFlexmiModel, plainModel);
				}
				String plainFlexmiFile = String.format("%s-plain.flexmi", path);
				if (Files.exists(Paths.get(plainFlexmiFile))) {
					Files.delete(Paths.get(plainFlexmiFile));
				}
				saveFlexmiFile(plainFlexmiFile, plainTransformer.getFlexmiFile(plainModel));


				TemplateFlexmiTransformer templateTransformer = new TemplateFlexmiTransformer();
				FlexmiModel templateModel = templateTransformer.getFlexmiModel(ecoreModel);
				templateModel.getIncludes().add("../../../templates/ecoreTemplates.flexmi");
				if (debug) {
					String templateFlexmiModel = String.format("%s-template.model", path);
					if (Files.exists(Paths.get(templateFlexmiModel))) {
						Files.delete(Paths.get(templateFlexmiModel));
					}
					saveFlexmiModel(templateFlexmiModel, templateModel);
				}
				String templateFlexmiFile = String.format("%s-template.flexmi", path);
				if (Files.exists(Paths.get(templateFlexmiFile))) {
					Files.delete(Paths.get(templateFlexmiFile));
				}
				saveFlexmiFile(templateFlexmiFile, templateTransformer.getFlexmiFile(templateModel));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
