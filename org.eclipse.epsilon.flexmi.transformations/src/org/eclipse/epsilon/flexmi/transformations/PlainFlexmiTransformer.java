package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
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

	protected static List<String> REGISTRY_NSURIS =
			new ArrayList<String>(Arrays.asList(
					"http://www.eclipse.org/emf/2003/XMLType"));

	protected FlexmiModelFactory flexmiFactory;

	/*
	 * Archive elements that might cross reference other ones in case the use
	 * of a qualified name is required (checked in a post-generation step)
	 */
	protected Map<String, Integer> nameCounters;
	protected List<CrossReference> crossReferences;

	public class CrossReference {
		protected EObject referencingElement;
		protected Tag tag;

		public CrossReference(EObject referencingElement, Tag tag) {
			this.referencingElement = referencingElement;
			this.tag = tag;
		}
	};

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

		nameCounters = new HashMap<>();
		crossReferences = new ArrayList<>();

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

		applyQualifiedNames(); // needed if there are simple name collisions

		return model;
	}

	protected void applyQualifiedNames() {
		for (CrossReference crossRef : crossReferences) {

			if (crossRef.referencingElement instanceof ETypedElement) {
				ETypedElement typedElem = (ETypedElement) crossRef.referencingElement;
				if (typedElem.getEType() != null
						&& typedElem.getEType().getName() != null
						&& isNameRepeated(typedElem.getEType().getName())) {

					for (Attribute attr : crossRef.tag.getAttributes()) {
						if (attr.getName().equals("type")) {
							setValue(attr, getQualifiedName(typedElem.getEType()));
						}
					}
				}
			}

			if (crossRef.referencingElement instanceof EClass
					&& !((EClass) crossRef.referencingElement).getESuperTypes().isEmpty()) {
				EClass clazz = (EClass) crossRef.referencingElement;
				List<String> supertypeNames = new ArrayList<>();
				for (EClass supertype : clazz.getESuperTypes()) {
					if (isNameRepeated(supertype.getName())) {
						supertypeNames.add(getQualifiedName(supertype));
					}
					else {
						supertypeNames.add(supertype.getName());
					}
				}
				Attribute supertypesAttr = flexmiFactory.createAttribute();
				supertypesAttr.setName("supertypes");
				setValue(supertypesAttr, String.join(",", supertypeNames));
				crossRef.tag.getAttributes().add(supertypesAttr);
			}
			else if (crossRef.referencingElement instanceof EReference) {
				EReference ref = (EReference) crossRef.referencingElement;

				if (ref.getEOpposite() != null
						&& isNameRepeated(ref.getEOpposite().getName())) {
					for (Attribute attr : crossRef.tag.getAttributes()) {
						if (attr.getName().equals("eOpposite")) {
							setValue(attr, getQualifiedName(ref.getEOpposite()));
						}
					}
				}
			}
			else if (crossRef.referencingElement instanceof EAnnotation
					&& !((EAnnotation) crossRef.referencingElement).getReferences().isEmpty()) {
				EAnnotation annotation = (EAnnotation) crossRef.referencingElement;
				List<String> annotationReferences = new ArrayList<>();
				for (EObject element : annotation.getReferences()) {
					if (element instanceof ENamedElement) {
						ENamedElement namedElement = (ENamedElement) element;
						if (isNameRepeated(namedElement.getName())) {
							annotationReferences.add(getQualifiedName(namedElement));
						}
						else {
							annotationReferences.add(namedElement.getName());
						}
					}
					else {
						System.out.println("Problem with annotation references: " + element);
					}
				}
				Attribute referencesAttr = flexmiFactory.createAttribute();
				referencesAttr.setName("references");
				setValue(referencesAttr, String.join(",", annotationReferences));
				crossRef.tag.getAttributes().add(referencesAttr);
			}
		}
	}

	protected void setValue(Attribute attr, String value) {
		attr.setValue(StringEscapeUtils.escapeXml(value));
	}

	protected boolean isNameRepeated(String name) {
		/*
		 * TODO: if the reference points to an Ecore type, then this has
		 * not been counted (but it will be referenced properly). Determine
		 * if this can be a problem (atm ignoring null name results)
		 */
		if (nameCounters.get(name) != null
				&& nameCounters.get(name) > 1) {
			return true;
		}
		return false;
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

				if (type instanceof EClassifier && type.eContainer() instanceof EPackage) {
					String typeURI = ((EPackage) type.eContainer()).getNsURI();

					boolean importURI = false;
					if (REGISTRY_NSURIS.contains(typeURI)) {
						importURI = true;
						importURI(tag, typeURI);
					}

					// prefix needed to find imported data types (e.g. EString, EInt)
					if (importURI || typeURI.equals(ECORE_NSURI)) {
						typeName = "//" + typeName;
					}
				}
				setValue(typeAttr, typeName);
				tag.getAttributes().add(typeAttr);
			}
		}
	}

	protected void importURI(Tag tag, String typeURI) {
		FlexmiModel model = getFlexmiModel(tag);
		if (!model.getImports().contains(typeURI)) {
			model.getImports().add(typeURI);
		}
	}

	protected FlexmiModel getFlexmiModel(Tag tag) {
		if (tag.eContainer() instanceof FlexmiModel) {
			return (FlexmiModel) tag.eContainer();
		}
		return getFlexmiModel((Tag) tag.eContainer());
	}

	protected void addTagAttributes(Tag tag, EObject element,
			List<String> omitAttributes) {

		for (EAttribute attribute : element.eClass().getEAllAttributes()) {
			if (!attribute.isDerived()
					&& element.eIsSet(attribute)
					&& !omitAttributes.contains(attribute.getName())) {
				Attribute auxAttr = flexmiFactory.createAttribute();
				auxAttr.setName(attribute.getName());
				setValue(auxAttr, "" + element.eGet(attribute));
				tag.getAttributes().add(auxAttr);
			}
		}

		if (element instanceof EReference) {
			EReference ref = (EReference) element;
			if (ref.getEOpposite() != null) {
				Attribute eOppositeAttr = flexmiFactory.createAttribute();
				eOppositeAttr.setName("eOpposite");
				setValue(eOppositeAttr, ref.getEOpposite().getName());
				tag.getAttributes().add(eOppositeAttr);
				if (!ref.getEKeys().isEmpty()) {
					System.out.println("Some EKeys found!!");
					System.out.println(ref.getEKeys());
				}
			}
		}
		else if (element instanceof EOperation) {
			EOperation op = (EOperation) element;
			if (!op.getEExceptions().isEmpty()) {
				System.out.println("Some EExceptions found!!");
				System.out.println(op.getEExceptions());
			}
		}

		// for reference qualifications
		if (element instanceof ENamedElement) {
			countName(((ENamedElement) element).getName());
		}
		if ((element instanceof ETypedElement)
				|| (element instanceof EClass)) {
			addCrossReferences(element, tag);
		}
	}

	protected void addCrossReferences(EObject elem, Tag tag) {
		crossReferences.add(new CrossReference(elem, tag));
	}

	protected void countName(String name) {
		if (nameCounters.get(name) == null) {
			nameCounters.put(name, 0);
		}
		nameCounters.put(name, nameCounters.get(name) + 1);
	}

	protected String getQualifiedName(ENamedElement elem) {
		List<String> nameSections = new ArrayList<>();
		ENamedElement current = elem;
		while (current != null) {
			nameSections.add(current.getName());
			current = (ENamedElement) current.eContainer();
		}
		Collections.reverse(nameSections);
		return String.join(".", nameSections);
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
		if (element instanceof EAnnotation) {
			populateAnnotation(tag, (EAnnotation) element);
		}
		else {
			tag.setName(getTagName(element));
			addTagAttributes(tag, element, Collections.emptyList());
			addTypeTagAttribute(tag, element);
			addChildren(tag, element);
		}
	}

	/**
	 * Manual annotation transformation to ensure correctness
	 */
	protected void populateAnnotation(Tag tag, EAnnotation annotation) {
		tag.setName(getTagName(annotation));
		addTagAttributes(tag, annotation, Collections.emptyList());
		for (Entry<String, String> entry : annotation.getDetails()) {
			Tag detailTag = flexmiFactory.createTag();
			detailTag.setName(getTagName((EObject) entry));
			tag.getTags().add(detailTag);
			addTagAttributes(detailTag, (EObject) entry, Collections.emptyList());
		}
		if (!annotation.getReferences().isEmpty()) {
			addCrossReferences(annotation, tag); // for later name qualification
			// references attribute added in the qualification step
		}
		if (!annotation.getContents().isEmpty()) {
			Tag contentsTag = flexmiFactory.createTag();
			contentsTag.setName("contents");
			tag.getTags().add(contentsTag);
			for (EObject element : annotation.getContents()) {
				Tag elementTag = flexmiFactory.createTag();
				contentsTag.getTags().add(elementTag);
				populateTags(elementTag, element);
			}
		}
	}
}
