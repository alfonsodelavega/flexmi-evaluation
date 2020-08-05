package org.eclipse.epsilon.flexmi.transformations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag;

public class TemplateFlexmiTransformer extends PlainFlexmiTransformer {

	public static void main(String[] args) throws Exception {
		String ecoreModel = "models/carShop.ecore";
		String flexmiModel = String.format("%s-template.model", ecoreModel);
		String flexmiFile = String.format("%s.flexmi", flexmiModel);

		EcorePackage.eINSTANCE.eClass();
		FlexmiModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("*", new XMIResourceFactoryImpl());

		TemplateFlexmiTransformer transformer = new TemplateFlexmiTransformer();
		FlexmiModel model = transformer.getFlexmiModel(ecoreModel);
		String flexmiContents = transformer.getFlexmiFile(model);
		System.out.println(flexmiContents);

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
		out.println(flexmiContents);
		out.close();
	}

	@Override
	protected void populateTags(Tag tag, EObject element) {

		if (element instanceof EAttribute) {
			// use the template and omit the type attribute (if it's an ecore one)
			populateAttribute(tag, (EAttribute) element);
		}
		else if (element instanceof EReference) {
			// if multi-bounded, use vals or refs depending on the containment
			populateReference(tag, (EReference) element);
		}
		else {
			// plain flexmi
			super.populateTags(tag, element);
		}
	}

	protected void populateAttribute(Tag tag, EAttribute attribute) {
		super.populateTags(tag, attribute);
	}

	protected void populateReference(Tag tag, EReference reference) {
		super.populateTags(tag, reference);
	}
}
