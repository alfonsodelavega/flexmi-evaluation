package org.eclipse.epsilon.flexmi.transformations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag;

public class PlainFlexmiTransformer extends Ecore2FlexmiTransformer {

	public static void main(String[] args) throws Exception {
		String ecoreModel = "models/carShop.ecore";
		String flexmiModel = String.format("%s-plain.model", ecoreModel);
		String flexmiFile = String.format("%s.flexmi", flexmiModel);

		EcorePackage.eINSTANCE.eClass();
		FlexmiModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("*", new XMIResourceFactoryImpl());

		PlainFlexmiTransformer transformer = new PlainFlexmiTransformer();
		FlexmiModel model = transformer.getFlexmiModel(ecoreModel);
		String plainFlexmi = transformer.getFlexmiFile(model);
		System.out.println(plainFlexmi);

		// save flexmi model
		ResourceSet resSet = new ResourceSetImpl();
		Resource flexmiModelResource = resSet.createResource(URI.createURI(flexmiModel));
		flexmiModelResource.getContents().add(model);
		try {
			flexmiModelResource.save(Collections.EMPTY_MAP);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// save flexmi file
		PrintWriter out = new PrintWriter(flexmiFile);
		out.println(plainFlexmi);
		out.close();
	}

	@Override
	protected void populateTags(Tag tag, EObject element) {

		tag.setName(getTagName(element));
		addTagAttributes(tag, element, Collections.emptyList());
		addTypeTagAttribute(tag, element);

		for (EObject child : element.eContents()) {
			if (child instanceof EGenericType) {
				continue;
			}
			Tag childTag = flexmiFactory.createTag();
			tag.getTags().add(childTag);
			populateTags(childTag, child);
		}
	}
}
