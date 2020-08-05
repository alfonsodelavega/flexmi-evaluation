package org.eclipse.epsilon.flexmi.transformations;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.egl.EglTemplate;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.egl.formatter.language.XmlFormatter;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelFactory;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag;

public abstract class Ecore2FlexmiTransformer {

	protected static final String FLEXMI_MODEL_TEMPLATE =
			"src/org/eclipse/epsilon/flexmi/transformations/flexmiModel2File.egl";
	protected static final String FLEXMI_MODEL_VARIABLE = "fmodel";

	protected static final String ECORE_NSURI = "http://www.eclipse.org/emf/2002/Ecore";

	protected FlexmiModelFactory flexmiFactory;

	public Ecore2FlexmiTransformer() {
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
		default:
			return obj.eClass().getName();
		}
	}

	protected abstract void populateTags(Tag tag, EObject element);
}
