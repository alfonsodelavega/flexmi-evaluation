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
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.epsilon.egl.EglTemplate;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.egl.formatter.language.XmlFormatter;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelFactory;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag;
import org.eclipse.uml2.types.TypesPackage;
import org.eclipse.uml2.uml.UMLPackage;

public class PlainFlexmiTransformer {

	protected static final String FLEXMI_MODEL_TEMPLATE =
			"src/org/eclipse/epsilon/flexmi/transformations/flexmiModel2File.egl";
	protected static final String FLEXMI_MODEL_VARIABLE = "fmodel";

	protected static final String ECORE_NSURI = "http://www.eclipse.org/emf/2002/Ecore";

	protected static List<String> REGISTRY_NSURIS =
			new ArrayList<>(Arrays.asList(
					ECORE_NSURI,
					"http://www.eclipse.org/emf/2003/XMLType",
					"http://www.eclipse.org/uml2/5.0.0/UML",
					"http://www.eclipse.org/uml2/5.0.0/Types",
					"http://www.w3.org/XML/1998/namespace",
					"http://www.eclipse.org/emf/2002/GenModel"));

	protected FlexmiModelFactory flexmiFactory;

	/*
	 * Useful for adding global variables to point to elements without name or iD
	 */
	protected Map<EObject, Tag> objectToTag;
	protected int globalVariableCounter;

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
	}

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

		objectToTag = new HashMap<>();
		globalVariableCounter = 0;

		nameCounters = new HashMap<>();
		crossReferences = new ArrayList<>();

		ResourceSet resSet = new ResourceSetImpl();
		resSet.getPackageRegistry().put(ECORE_NSURI, EcorePackage.eINSTANCE);
		resSet.getPackageRegistry().put("http://www.eclipse.org/emf/2002/GenModel", GenModelPackage.eINSTANCE);

		resSet.getPackageRegistry().put("http://www.eclipse.org/emf/2003/XMLType", XMLTypePackage.eINSTANCE);
		resSet.getPackageRegistry().put("http://www.w3.org/XML/1998/namespace", XMLNamespacePackage.eINSTANCE);

		resSet.getPackageRegistry().put("http://www.eclipse.org/uml2/5.0.0/UML", UMLPackage.eINSTANCE);
		resSet.getPackageRegistry().put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		resSet.getPackageRegistry().put("http://www.eclipse.org/uml2/5.0.0/Types", TypesPackage.eINSTANCE);
		resSet.getPackageRegistry().put("http://www.eclipse.org/uml2/4.0.0/Types", TypesPackage.eINSTANCE);


		Resource ecoreModelResource = resSet.getResource(URI.createURI(ecoreModel), true);

		EcoreUtil.resolveAll(resSet);

		FlexmiModel model = flexmiFactory.createFlexmiModel();
		model.setNsuri(ECORE_NSURI);
		model.getImports().add(ECORE_NSURI);

		for (EObject root : ecoreModelResource.getContents()) {
			Tag rootTag = flexmiFactory.createTag();
			model.getTags().add(rootTag);
			populateTags(rootTag, root);
		}

		applyQualifiedNames(); // needed if there are simple name collisions
		
		// print out some extra data (to try find out causes of large load times)
		//		System.out.printf("\t%s\n", this.getClass().getSimpleName());
		//		System.out.printf("\t# Global variables: %d\n", globalVariableCounter);
		//		System.out.printf("\t# Qualified names: %d\n\n",
		//				nameCounters.values().stream()
		//						.filter(i -> i > 1)
		//						.mapToInt(Integer::intValue)
		//						.sum());

		return model;
	}

	protected void applyQualifiedNames() {
		for (CrossReference crossRef : crossReferences) {

			if (crossRef.referencingElement instanceof ETypedElement) {

				if (crossRef.referencingElement instanceof EAttribute) {
					Attribute typeAttr = findAttribute(crossRef.tag, "type");

					if (typeAttr != null && typeAttr.getValue().startsWith("//")) {
						// attribute with imported type: do not qualify
						continue;
					}
				}

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
						boolean importURI = addTypePackage(crossRef.tag, supertype);
						String supertypeName = supertype.getName();
						if (importURI) {
							supertypeName = "//" + supertypeName;
						}
						supertypeNames.add(supertypeName);
					}
				}
				Attribute supertypesAttr = flexmiFactory.createAttribute();
				setName(supertypesAttr, "supertypes");
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

				// we need a firstLoop to see if any of the referenced element
				//   is non-referentiable (i.e. in ecore, it has no name attribute)
				// if that happens, we need to use global references for all
				//   elements, as the expression used for the reference needs to
				//   return a collection
				boolean needsGlobalReferences = false;
				for (EObject element : annotation.getReferences()) {
					if (!(element instanceof ENamedElement)) {
						needsGlobalReferences = true;
						break;
					}
				}
				if (needsGlobalReferences) {
					for (EObject element : annotation.getReferences()) {
						// use a global variable to refer to the element
						Tag referencedTag = objectToTag.get(element);
						Attribute refAttribute = findAttribute(referencedTag, ":global");
						if (refAttribute == null) {
							refAttribute = flexmiFactory.createAttribute();
							setName(refAttribute, ":global");
							setValue(refAttribute, "ref" + globalVariableCounter);
							referencedTag.getAttributes().add(refAttribute);
							globalVariableCounter++;
						}
						annotationReferences.add(refAttribute.getValue());
					}
					Attribute referencesAttr = flexmiFactory.createAttribute();
					setName(referencesAttr, ":references");
					setValue(referencesAttr, String.format("Sequence{%s}",
							String.join(",", annotationReferences)));
					crossRef.tag.getAttributes().add(referencesAttr);
				}
				else {
					for (EObject element : annotation.getReferences()) {
						ENamedElement namedElement = (ENamedElement) element;
						if (isNameRepeated(namedElement.getName())) {
							annotationReferences.add(getQualifiedName(namedElement));
						}
						else {
							annotationReferences.add(namedElement.getName());
						}
					}
					Attribute referencesAttr = flexmiFactory.createAttribute();
					setName(referencesAttr, "references");
					setValue(referencesAttr, String.join(",", annotationReferences));
					crossRef.tag.getAttributes().add(referencesAttr);
				}
			}
		}
	}

	protected void setName(Tag tag, EObject element) {
		tag.setName(getTagName(element));
	}

	protected void setName(Attribute attr, String name) {
		attr.setName(getAttributeName(name));
	}

	protected void setValue(Attribute attr, String value) {
		attr.setValue(StringEscapeUtils.escapeXml(value));
	}

	protected Attribute findAttribute(Tag tag, String attributeName) {
		for (Attribute attr : tag.getAttributes()) {
			if (attr.getName().equals(attributeName)) {
				return attr;
			}
		}
		return null;
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
		case "EEnum":
			return "enum";
		case "EEnumLiteral":
			return "lit";
		default:
			return obj.eClass().getName();
		}
	}

	protected String getAttributeName(String attrName) {
		switch (attrName) {
		case "upperBound":
			return "upper";
		case "lowerBound":
			return "lower";
		case "defaultValueLiteral":
			return "literal";
		default:
			return attrName;
		}
	}

	protected void addTypeTagAttribute(Tag tag, EObject element) {
		if (element instanceof ETypedElement) {
			EClassifier type = ((ETypedElement) element).getEType();
			if (type != null && type.eIsProxy()) {
				System.err.println("Proxy type: " + type);
			}
			// void EOperations have null type (and some ecore github attributes too)
			if (type != null
					&& type.getName() != null
					&& !type.getName().equals("")) {

				Attribute typeAttr = flexmiFactory.createAttribute();
				setName(typeAttr, "type");
				String typeName = type.getName();

				if (type instanceof EClassifier && type.eContainer() instanceof EPackage) {

					boolean importURI = addTypePackage(tag, type);
					if (importURI) {
						// prefix needed to find imported data types (e.g. EString, EInt)
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
		String uri = typeURI;
		if (uri.contains("uml2")) {
			uri = uri.replace("4.0.0", "5.0.0");
		}
		if (!model.getImports().contains(uri)) {
			model.getImports().add(uri);
		}
	}

	protected boolean addTypePackage(Tag tag, EClassifier type) {
		boolean importURI = false;
		if (type.eContainer() instanceof EPackage) {
			String typeURI = ((EPackage) type.eContainer()).getNsURI();

			if (REGISTRY_NSURIS.contains(typeURI)) {
				importURI = true;
				importURI(tag, typeURI);
			}
		}
		return importURI;
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
				setName(auxAttr, attribute.getName());
				setValue(auxAttr, "" + element.eGet(attribute));
				tag.getAttributes().add(auxAttr);
			}
		}

		if (element instanceof EReference) {
			EReference ref = (EReference) element;
			if (ref.getEOpposite() != null) {
				Attribute eOppositeAttr = flexmiFactory.createAttribute();
				setName(eOppositeAttr, "eOpposite");
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

		// for cross-references to no-referenceable elements
		objectToTag.put(element, tag);

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
			setName(tag, element);
			addTagAttributes(tag, element, Collections.emptyList());
			addTypeTagAttribute(tag, element);
			addChildren(tag, element);
		}
	}

	/**
	 * Manual annotation transformation to ensure correctness
	 */
	protected void populateAnnotation(Tag tag, EAnnotation annotation) {
		setName(tag, annotation);
		addTagAttributes(tag, annotation, Collections.emptyList());
		for (Entry<String, String> entry : annotation.getDetails()) {
			Tag detailTag = flexmiFactory.createTag();
			setName(detailTag, (EObject) entry);
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
