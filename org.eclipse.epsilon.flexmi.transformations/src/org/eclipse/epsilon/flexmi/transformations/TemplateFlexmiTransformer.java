package org.eclipse.epsilon.flexmi.transformations;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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

	static final String GENMODEL_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";

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
		model.getIncludes().add("../templates/ecoreTemplates.flexmi");
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
		else if (element instanceof EAnnotation) {
			populateAnnotation(tag, (EAnnotation) element);
		}
		else {
			// plain flexmi
			super.populateTags(tag, element);
		}
	}

	@Override
	protected void populateAnnotation(Tag tag, EAnnotation annotation) {
		if (annotation.getSource() != null
				&& annotation.getSource().equals(GENMODEL_SOURCE)
				&& annotation.getDetails().size() == 1
				&& annotation.getDetails().get(0).getKey().equals("documentation")
				&& annotation.getContents().isEmpty()
				&& annotation.getReferences().isEmpty()) {

			tag.setName("genmodel");
			Attribute docAttribute = flexmiFactory.createAttribute();
			docAttribute.setName("doc");
			setValue(docAttribute, annotation.getDetails().get(0).getValue());
			tag.getAttributes().add(docAttribute);
		}
		else {
			super.populateAnnotation(tag, annotation);
		}
	}

	protected void populateAttribute(Tag tag, EAttribute attribute) {
		String templateName = getAttributeTemplate(attribute);
		if (templateName != null) {
			tag.setName(templateName);
		}
		else {
			tag.setName(getTagName(attribute));
			addTypeTagAttribute(tag, attribute);
		}
		addTagAttributes(tag, attribute, Collections.emptyList());
	}

	protected String getAttributeTemplate(EAttribute attribute) {
		String type = attribute.getEType().getName();
		if (type != null) {
			switch (type) {
			case "EString":
				return "string";
			case "EBoolean":
				return "boolean";
			case "EInt":
				return "int";
			case "EDouble":
				return "double";
			}
		}
		return null;
	}

	protected void populateReference(Tag tag, EReference reference) {
		// four possibilities (containment X upper)
		// only case not managed by a template is !containment && upper == 1
		if (reference.isContainment()) {
			if (reference.getUpperBound() != 1) {
				tag.setName("vals");
				List<String> omitAttributes = new ArrayList<>(Arrays.asList("containment"));
				// it might be a constrained multi-bounded (so the template value
				//   must be overriden)
				if (reference.getUpperBound() < 0) {
					omitAttributes.add("upperBound");
				}
				addTagAttributes(tag, reference, omitAttributes);
			}
			else {
				tag.setName("val");
				addTagAttributes(tag, reference, Arrays.asList("containment"));
			}
			addTypeTagAttribute(tag, reference);
			addChildren(tag, reference);
		}
		else {
			if (reference.getUpperBound() != 1) {
				tag.setName("refs");
				List<String> omitAttributes = new ArrayList<>(1);
				// it might be a constrained multi-bounded (so the template value
				//   must be overriden)
				if (reference.getUpperBound() == -1) {
					omitAttributes.add("upperBound");
				}
				addTagAttributes(tag, reference, omitAttributes);
				addTypeTagAttribute(tag, reference);
				addChildren(tag, reference);
			}
			else {
				super.populateTags(tag, reference);
			}
		}
	}
}
