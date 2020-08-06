package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
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

public class PlainFlexmiTransformer {

	protected static final String FLEXMI_MODEL_TEMPLATE =
			"src/org/eclipse/epsilon/flexmi/transformations/flexmiModel2File.egl";
	protected static final String FLEXMI_MODEL_VARIABLE = "fmodel";

	protected static final String ECORE_NSURI = "http://www.eclipse.org/emf/2002/Ecore";

	protected FlexmiModelFactory flexmiFactory;


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


	public PlainFlexmiTransformer() {
		flexmiFactory = FlexmiModelFactory.eINSTANCE;
	}

	public FlexmiModel getFlexmiModel(String ecoreModel) {

		ResourceSet resSet = new ResourceSetImpl();
		Resource ecoreModelResource = resSet.getResource(URI.createURI(ecoreModel), true);

		FlexmiModel model = flexmiFactory.createFlexmiModel();
		model.setNsuri(ECORE_NSURI);
		model.getImports().add(ECORE_NSURI);

		for (EObject root : ecoreModelResource.getContents()) {
			Tag rootTag = flexmiFactory.createTag();
			model.getTags().add(rootTag);
			populateTags(rootTag, root);
		}

		return model;
	}

	public String getFlexmiFile(FlexmiModel model) throws EglRuntimeException {
		EglTemplateFactory templateFactory = new EglTemplateFactory();
		EglTemplate template = templateFactory.load(new File(FLEXMI_MODEL_TEMPLATE));
		template.populate(FLEXMI_MODEL_VARIABLE, model);
		template.setFormatter(new XmlFormatter());
		return template.process();
	}

	public String transform(String ecoreModel) throws EglRuntimeException {
		return getFlexmiFile(getFlexmiModel(ecoreModel));
	}

	protected String getTagName(EObject obj) {
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
		case "EAnnotation":
			return "annotation";
		case "EStringToStringMapEntry":
			return "details";
		case "EOperation":
			return "op";
		case "EParameter":
			return "param";
		default:
			return obj.eClass().getName();
		}
	}

	protected void addTypeTagAttribute(Tag tag, EObject element) {
		if (element instanceof ETypedElement) {
			EClassifier type = ((ETypedElement) element).getEType();
			// void EOperations have null type (and some ecore github attributes too)
			if (type != null) {
				Attribute typeAttr = flexmiFactory.createAttribute();
				typeAttr.setName("type");
				String typeName = type.getName();
				if (type instanceof EDataType
						&& type.eContainer() instanceof EPackage
						&& ((EPackage)type.eContainer()).getNsURI().contentEquals(ECORE_NSURI)) {
					// prefix needed to find ecore data types (e.g. EString, EInt)
					typeName = "//" + typeName;
				}
				typeAttr.setValue(typeName);
				tag.getAttributes().add(typeAttr);
			}
		}
	}

	protected void addTagAttributes(Tag tag, EObject element,
			List<String> omitAttributes) {
		for (EAttribute attribute : element.eClass().getEAllAttributes()) {
			if (!attribute.isDerived()
					&& element.eIsSet(attribute)
					&& !omitAttributes.contains(attribute.getName())) {
				Attribute auxAttr = flexmiFactory.createAttribute();
				auxAttr.setName(attribute.getName());
				auxAttr.setValue(StringEscapeUtils.escapeXml("" + element.eGet(attribute)));
				tag.getAttributes().add(auxAttr);
			}
		}
		if (element instanceof EClass) {
			EClass clazz = (EClass) element;
			if (!clazz.getESuperTypes().isEmpty()) {
				Attribute supertypesAttr = flexmiFactory.createAttribute();
				supertypesAttr.setName("supertypes");
				String supertypes = clazz.getESuperTypes()
						.stream()
						.map(supertype -> supertype.getName())
						.collect(Collectors.joining(","));
				supertypesAttr.setValue(supertypes);
				tag.getAttributes().add(supertypesAttr);
			}
		}
		else if (element instanceof EReference) {
			EReference ref = (EReference) element;
			if (ref.getEOpposite() != null) {
				Attribute eOppositeAttr = flexmiFactory.createAttribute();
				eOppositeAttr.setName("eOpposite");
				eOppositeAttr.setValue(ref.getEOpposite().getName());
				tag.getAttributes().add(eOppositeAttr);
				if (!ref.getEKeys().isEmpty()) {
					System.out.println(ref.getEKeys());
				}
			}
		}
		else if (element instanceof EOperation) {
			EOperation op = (EOperation) element;
			if (!op.getEExceptions().isEmpty()) {
				System.out.println(op.getEExceptions());
			}
		}
	}

	protected void addChildren(Tag tag, EObject element) {
		for (EObject child : element.eContents()) {
			if (child instanceof EGenericType) {
				continue;
			}
			Tag childTag = flexmiFactory.createTag();
			tag.getTags().add(childTag);
			populateTags(childTag, child);
		}
	}

	protected void populateTags(Tag tag, EObject element) {
		tag.setName(getTagName(element));
		addTagAttributes(tag, element, Collections.emptyList());
		addTypeTagAttribute(tag, element);
		addChildren(tag, element);
	}
}
