/**
 */
package org.eclipse.epsilon.flexmi.transformations.flexmiModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Flexmi Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getNsuri <em>Nsuri</em>}</li>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getIncludes <em>Includes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getTags <em>Tags</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getFlexmiModel()
 * @model
 * @generated
 */
public interface FlexmiModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Nsuri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nsuri</em>' attribute.
	 * @see #setNsuri(String)
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getFlexmiModel_Nsuri()
	 * @model
	 * @generated
	 */
	String getNsuri();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel#getNsuri <em>Nsuri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Nsuri</em>' attribute.
	 * @see #getNsuri()
	 * @generated
	 */
	void setNsuri(String value);

	/**
	 * Returns the value of the '<em><b>Imports</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Imports</em>' attribute list.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getFlexmiModel_Imports()
	 * @model
	 * @generated
	 */
	EList<String> getImports();

	/**
	 * Returns the value of the '<em><b>Includes</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Includes</em>' attribute list.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getFlexmiModel_Includes()
	 * @model
	 * @generated
	 */
	EList<String> getIncludes();

	/**
	 * Returns the value of the '<em><b>Tags</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' containment reference list.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getFlexmiModel_Tags()
	 * @model containment="true"
	 * @generated
	 */
	EList<Tag> getTags();

} // FlexmiModel
