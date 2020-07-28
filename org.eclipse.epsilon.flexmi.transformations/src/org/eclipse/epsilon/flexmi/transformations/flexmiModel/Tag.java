/**
 */
package org.eclipse.epsilon.flexmi.transformations.flexmiModel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tag</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getTags <em>Tags</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getTag()
 * @model
 * @generated
 */
public interface Tag extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getTag_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Attribute}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' containment reference list.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getTag_Attributes()
	 * @model containment="true"
	 * @generated
	 */
	EList<Attribute> getAttributes();

	/**
	 * Returns the value of the '<em><b>Tags</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' containment reference list.
	 * @see org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage#getTag_Tags()
	 * @model containment="true"
	 * @generated
	 */
	EList<Tag> getTags();

} // Tag
