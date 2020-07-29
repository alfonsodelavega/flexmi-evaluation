/**
 */
package org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModel;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.FlexmiModelPackage;
import org.eclipse.epsilon.flexmi.transformations.flexmiModel.Tag;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Flexmi Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelImpl#getNsuri <em>Nsuri</em>}</li>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelImpl#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.epsilon.flexmi.transformations.flexmiModel.impl.FlexmiModelImpl#getTags <em>Tags</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FlexmiModelImpl extends MinimalEObjectImpl.Container implements FlexmiModel {
	/**
	 * The default value of the '{@link #getNsuri() <em>Nsuri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNsuri()
	 * @generated
	 * @ordered
	 */
	protected static final String NSURI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getNsuri() <em>Nsuri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNsuri()
	 * @generated
	 * @ordered
	 */
	protected String nsuri = NSURI_EDEFAULT;

	/**
	 * The cached value of the '{@link #getImports() <em>Imports</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImports()
	 * @generated
	 * @ordered
	 */
	protected EList<String> imports;

	/**
	 * The cached value of the '{@link #getTags() <em>Tags</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTags()
	 * @generated
	 * @ordered
	 */
	protected EList<Tag> tags;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FlexmiModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FlexmiModelPackage.Literals.FLEXMI_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getNsuri() {
		return nsuri;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNsuri(String newNsuri) {
		String oldNsuri = nsuri;
		nsuri = newNsuri;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FlexmiModelPackage.FLEXMI_MODEL__NSURI, oldNsuri, nsuri));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<String> getImports() {
		if (imports == null) {
			imports = new EDataTypeUniqueEList<String>(String.class, this, FlexmiModelPackage.FLEXMI_MODEL__IMPORTS);
		}
		return imports;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Tag> getTags() {
		if (tags == null) {
			tags = new EObjectContainmentEList<Tag>(Tag.class, this, FlexmiModelPackage.FLEXMI_MODEL__TAGS);
		}
		return tags;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case FlexmiModelPackage.FLEXMI_MODEL__TAGS:
				return ((InternalEList<?>)getTags()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FlexmiModelPackage.FLEXMI_MODEL__NSURI:
				return getNsuri();
			case FlexmiModelPackage.FLEXMI_MODEL__IMPORTS:
				return getImports();
			case FlexmiModelPackage.FLEXMI_MODEL__TAGS:
				return getTags();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case FlexmiModelPackage.FLEXMI_MODEL__NSURI:
				setNsuri((String)newValue);
				return;
			case FlexmiModelPackage.FLEXMI_MODEL__IMPORTS:
				getImports().clear();
				getImports().addAll((Collection<? extends String>)newValue);
				return;
			case FlexmiModelPackage.FLEXMI_MODEL__TAGS:
				getTags().clear();
				getTags().addAll((Collection<? extends Tag>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case FlexmiModelPackage.FLEXMI_MODEL__NSURI:
				setNsuri(NSURI_EDEFAULT);
				return;
			case FlexmiModelPackage.FLEXMI_MODEL__IMPORTS:
				getImports().clear();
				return;
			case FlexmiModelPackage.FLEXMI_MODEL__TAGS:
				getTags().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case FlexmiModelPackage.FLEXMI_MODEL__NSURI:
				return NSURI_EDEFAULT == null ? nsuri != null : !NSURI_EDEFAULT.equals(nsuri);
			case FlexmiModelPackage.FLEXMI_MODEL__IMPORTS:
				return imports != null && !imports.isEmpty();
			case FlexmiModelPackage.FLEXMI_MODEL__TAGS:
				return tags != null && !tags.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (nsuri: ");
		result.append(nsuri);
		result.append(", imports: ");
		result.append(imports);
		result.append(')');
		return result.toString();
	}

} //FlexmiModelImpl
