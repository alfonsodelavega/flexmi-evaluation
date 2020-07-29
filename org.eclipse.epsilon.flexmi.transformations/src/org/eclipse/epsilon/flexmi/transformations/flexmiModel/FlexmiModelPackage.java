/**
 */
package org.eclipse.epsilon.flexmi.transformations.flexmiModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelFactory
 * @model kind="package"
 * @generated
 */
public interface FlexmiModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "flexmiModel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "flexmi";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	FlexmiModelPackage eINSTANCE = org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelImpl <em>Flexmi Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelImpl
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelPackageImpl#getFlexmiModel()
	 * @generated
	 */
	int FLEXMI_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Nsuri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLEXMI_MODEL__NSURI = 0;

	/**
	 * The feature id for the '<em><b>Imports</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLEXMI_MODEL__IMPORTS = 1;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLEXMI_MODEL__TAGS = 2;

	/**
	 * The number of structural features of the '<em>Flexmi Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLEXMI_MODEL_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Flexmi Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLEXMI_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.TagImpl <em>Tag</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.TagImpl
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelPackageImpl#getTag()
	 * @generated
	 */
	int TAG = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__NAME = 0;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__ATTRIBUTES = 1;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG__TAGS = 2;

	/**
	 * The number of structural features of the '<em>Tag</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Tag</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.AttributeImpl <em>Attribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.AttributeImpl
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelPackageImpl#getAttribute()
	 * @generated
	 */
	int ATTRIBUTE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Attribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Attribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel <em>Flexmi Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Flexmi Model</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel
	 * @generated
	 */
	EClass getFlexmiModel();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getNsuri <em>Nsuri</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Nsuri</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getNsuri()
	 * @see #getFlexmiModel()
	 * @generated
	 */
	EAttribute getFlexmiModel_Nsuri();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getImports <em>Imports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Imports</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getImports()
	 * @see #getFlexmiModel()
	 * @generated
	 */
	EAttribute getFlexmiModel_Imports();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tags</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getTags()
	 * @see #getFlexmiModel()
	 * @generated
	 */
	EReference getFlexmiModel_Tags();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag <em>Tag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tag</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag
	 * @generated
	 */
	EClass getTag();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getName()
	 * @see #getTag()
	 * @generated
	 */
	EAttribute getTag_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attributes</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getAttributes()
	 * @see #getTag()
	 * @generated
	 */
	EReference getTag_Attributes();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tags</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getTags()
	 * @see #getTag()
	 * @generated
	 */
	EReference getTag_Tags();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute <em>Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute
	 * @generated
	 */
	EClass getAttribute();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute#getName()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute#getValue()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Value();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	FlexmiModelFactory getFlexmiModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelImpl <em>Flexmi Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelImpl
		 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelPackageImpl#getFlexmiModel()
		 * @generated
		 */
		EClass FLEXMI_MODEL = eINSTANCE.getFlexmiModel();

		/**
		 * The meta object literal for the '<em><b>Nsuri</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLEXMI_MODEL__NSURI = eINSTANCE.getFlexmiModel_Nsuri();

		/**
		 * The meta object literal for the '<em><b>Imports</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLEXMI_MODEL__IMPORTS = eINSTANCE.getFlexmiModel_Imports();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FLEXMI_MODEL__TAGS = eINSTANCE.getFlexmiModel_Tags();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.TagImpl <em>Tag</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.TagImpl
		 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelPackageImpl#getTag()
		 * @generated
		 */
		EClass TAG = eINSTANCE.getTag();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAG__NAME = eINSTANCE.getTag_Name();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TAG__ATTRIBUTES = eINSTANCE.getTag_Attributes();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TAG__TAGS = eINSTANCE.getTag_Tags();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.AttributeImpl <em>Attribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.AttributeImpl
		 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelPackageImpl#getAttribute()
		 * @generated
		 */
		EClass ATTRIBUTE = eINSTANCE.getAttribute();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__NAME = eINSTANCE.getAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__VALUE = eINSTANCE.getAttribute_Value();

	}

} //FlexmiModelPackage
