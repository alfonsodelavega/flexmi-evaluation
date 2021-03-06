/**
 */
package org.eclipse.epsilon.flexmi.transformations.flexmiModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage
 * @generated
 */
public interface FlexmiModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	FlexmiModelFactory eINSTANCE = org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Flexmi Model</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Flexmi Model</em>'.
	 * @generated
	 */
	FlexmiModel createFlexmiModel();

	/**
	 * Returns a new object of class '<em>Tag</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Tag</em>'.
	 * @generated
	 */
	Tag createTag();

	/**
	 * Returns a new object of class '<em>Attribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attribute</em>'.
	 * @generated
	 */
	Attribute createAttribute();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	FlexmiModelPackage getFlexmiModelPackage();

} //FlexmiModelFactory
