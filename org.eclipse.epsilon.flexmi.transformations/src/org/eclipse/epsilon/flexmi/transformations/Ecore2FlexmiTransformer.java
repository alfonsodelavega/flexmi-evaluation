package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.egl.EglTemplate;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.egl.formatter.language.XmlFormatter;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelFactory;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag;

public class Ecore2FlexmiTransformer {

	private static final String FLEXMI_MODEL_TEMPLATE =
			"src/org/eclipse/epsilon/flexmi/transformations/flexmiModel2File.egl";
	private static final String FLEXMI_MODEL_VARIABLE = "fmodel";

	private static final String ECORE_NSURI = "http://www.eclipse.org/emf/2002/Ecore";

	public static void main(String[] args) throws Exception {
		String ecoreModel = "models/carShop.ecore";
		String flexmiModel = String.format("%s.model", ecoreModel);
		String flexmiFile = String.format("%s.flexmi", flexmiModel);

		EcorePackage.eINSTANCE.eClass();
		FlexmiModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("*", new XMIResourceFactoryImpl());
		
		FlexmiModel model = getPlainFlexmiModel(ecoreModel);
		String plainFlexmi = getFlexmiFile(model);
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

	public static FlexmiModel getPlainFlexmiModel(String ecoreModel) {

		ResourceSet resSet = new ResourceSetImpl();
		Resource ecoreModelResource = resSet.getResource(URI.createURI(ecoreModel), true);

		FlexmiModelFactory flexmiFactory = FlexmiModelFactory.eINSTANCE;
		FlexmiModel model = flexmiFactory.createFlexmiModel();

		model.setNsuri(ECORE_NSURI);
		model.getImports().add(ECORE_NSURI);

		for (EObject root : ecoreModelResource.getContents()) {
			Tag rootTag = flexmiFactory.createTag();
			model.getTags().add(rootTag);
			populateTags(flexmiFactory, rootTag, root);
		}

		return model;
	}

	// TODO
	public static FlexmiModel getTemplateBasedFlexmiModel(String ecoreModel) {
		return null;
	}

	private static void populateTags(FlexmiModelFactory flexmiFactory, 
			Tag tag, EObject element) {

		tag.setName(getTagName(element));
		for (EAttribute attribute : element.eClass().getEAllAttributes()) {
			if (!attribute.isDerived() && element.eIsSet(attribute)) {
				Attribute auxAttr = flexmiFactory.createAttribute();
				auxAttr.setName(attribute.getName());
				auxAttr.setValue("" + element.eGet(attribute));
				tag.getAttributes().add(auxAttr);
			}
		}
		if (element instanceof ETypedElement) {
			Attribute typeAttr = flexmiFactory.createAttribute();
			typeAttr.setName("type");
			EClassifier type = ((ETypedElement) element).getEType();
			// void EOperations have null type
			if (type != null) {
				String typeName = type.getName();
				if (element instanceof EAttribute) {
					// needed to find metamodel data types (e.g. EString, EInt)
					typeName = "//" + typeName;
				}
				typeAttr.setValue(typeName);
			}
			tag.getAttributes().add(typeAttr);
		}

		for (EObject child : element.eContents()) {
			if (child instanceof EGenericType) {
				continue;
			}
			Tag childTag = flexmiFactory.createTag();
			childTag.setName(getTagName(child));
			tag.getTags().add(childTag);
			populateTags(flexmiFactory, childTag, child);
		}
	}

	private static String getTagName(EObject obj) {
		switch(obj.eClass().getName()) {
		case "EPackage":
			return "package";
		case "EClass":
			return "class";
		case "EAttribute":
			return "attr";
		case "EReference":
			return "ref";
		case "EDataType":
			return "type";
		default:
			return obj.eClass().getName();
		}
	}

	public static String getFlexmiFile(FlexmiModel model) throws EglRuntimeException {
		EglTemplateFactory templateFactory = new EglTemplateFactory();
		EglTemplate template = templateFactory.load(new File(FLEXMI_MODEL_TEMPLATE));
		template.populate(FLEXMI_MODEL_VARIABLE, model);
		template.setFormatter(new XmlFormatter());
		return template.process();
	}
}
