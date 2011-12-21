/*******************************************************************************
 * Copyright (c) 2010 Oracle.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution. 
 * The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 * and the Apache License v2.0 is available at 
 *     http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses.
 *
 * Contributors:
 *     Hal Hildebrand - Initial JMX support 
 ******************************************************************************/

package org.eclipse.gemini.mgmt.framework;

import java.io.IOException;
import java.util.ArrayList;

import javax.management.Notification;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.jmx.framework.BundleStateMBean;

import org.eclipse.gemini.mgmt.Monitor;
import org.eclipse.gemini.mgmt.framework.internal.OSGiBundle;
import org.eclipse.gemini.mgmt.framework.internal.OSGiBundleEvent;
import org.eclipse.gemini.mgmt.internal.BundleUtil;

/** 
 * 
 */
public final class BundleState extends Monitor implements CustomBundleStateMBean {
	
	private BundleListener bundleListener;
	
	private BundleContext bundleContext;
	
	/**
	 * 
	 * @param bundleContext
	 */
	public BundleState(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * {@inheritDoc}
	 */
	public TabularData listBundles() throws IOException {
		try {
			TabularDataSupport table = new TabularDataSupport(BundleStateMBean.BUNDLES_TYPE);
			for (Bundle bundle : bundleContext.getBundles()) {
				table.put(new OSGiBundle(bundle).asCompositeData());
			}
			return table;
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public TabularData listBundles(int mask) throws IOException {
		if (mask < 1 || mask > 1048575) {
			throw new IllegalArgumentException("Mask out of range!");
		}
		try {
			ArrayList<OSGiBundle> bundles = new ArrayList<OSGiBundle>();
			for (Bundle bundle : bundleContext.getBundles()) {
				bundles.add(new OSGiBundle(bundle));
			}
			TabularData table = OSGiBundle.tableFrom(bundles, mask);
			return table;
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getExportedPackages(long bundleId) throws IOException {
		return BundleUtil.getBundleExportedPackages(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getFragments(long bundleId) throws IOException {
		return BundleUtil.getBundleFragments(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public TabularData getHeaders(long bundleId) throws IOException {
		return OSGiBundle.headerTable(retrieveBundle(bundleId).getHeaders());
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getHosts(long fragment) throws IOException {
		return BundleUtil.getBundleHosts(retrieveBundle(fragment));
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getImportedPackages(long bundleId) throws IOException {
		return BundleUtil.getBundleImportedPackages(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public long getLastModified(long bundleId) throws IOException {
		return retrieveBundle(bundleId).getLastModified();
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getRegisteredServices(long bundleId) throws IOException {
		return BundleUtil.serviceIds(retrieveBundle(bundleId).getRegisteredServices());
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getRequiringBundles(long bundleId) throws IOException {
		return BundleUtil.getRequiringBundles(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getServicesInUse(long bundleIdentifier) throws IOException {
		return BundleUtil.serviceIds(retrieveBundle(bundleIdentifier).getServicesInUse());
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStartLevel(long bundleId) throws IOException {
		return BundleUtil.getBundleStartLevel(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getState(long bundleId) throws IOException {
		return BundleUtil.getBundleState(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSymbolicName(long bundleId) throws IOException {
		return retrieveBundle(bundleId).getSymbolicName();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLocation(long bundleId) throws IOException {
		return retrieveBundle(bundleId).getLocation();
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getRequiredBundles(long bundleIdentifier) throws IOException {
		return BundleUtil.getRequiredBundles(retrieveBundle(bundleIdentifier));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getVersion(long bundleId) throws IOException {
		return retrieveBundle(bundleId).getVersion().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPersistentlyStarted(long bundleId) throws IOException {
		return BundleUtil.isBundlePersistentlyStarted(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFragment(long bundleId) throws IOException {
		return BundleUtil.isBundleFragment(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRemovalPending(long bundleId) throws IOException {
		return BundleUtil.isRemovalPending(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequired(long bundleId) throws IOException {
		return BundleUtil.isRequired(retrieveBundle(bundleId));
	}

	//New methods from the JMX Update RFC 169
	
	/**
	 * {@inheritDoc}
	 */
	public CompositeData getBundle(long bundleId) throws IOException {
		return new OSGiBundle(retrieveBundle(bundleId)).asCompositeData();
	}

	/**
	 * {@inheritDoc}
	 */
	public TabularData listBundles(String... bundleTypeItems) throws IOException {
		try {
			ArrayList<OSGiBundle> bundles = new ArrayList<OSGiBundle>();
			for (Bundle bundle : bundleContext.getBundles()) {
				bundles.add(new OSGiBundle(bundle));
			}
			TabularData table = OSGiBundle.tableFrom(bundles, bundleTypeItems);
			return table;
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isActivationPolicyUsed(long bundleId) throws IOException {
		return BundleUtil.isBundleActivationPolicyUsed(retrieveBundle(bundleId));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHeader(long bundleId, String key) throws IOException {
		return retrieveBundle(bundleId).getHeaders().get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public TabularData getHeaders(long bundleId, String locale) throws IOException {
		return OSGiBundle.headerTable(retrieveBundle(bundleId).getHeaders(locale));
	}

	/**
	 * {@inheritDoc}
	 */
	public CompositeData getHeaders(long bundleId, String key, String locale) throws IOException {
		String value = retrieveBundle(bundleId).getHeaders(locale).get(key);
		if(value == null){
			return null;
		}
		return OSGiBundle.getHeaderCompositeData(key, value);
	}

	//End methods for the MBean
	
	private Bundle retrieveBundle(long bundleId) throws IOException {
		Bundle b = bundleContext.getBundle(bundleId);
		if (b == null) {
			throw new IOException("Bundle with id: " + bundleId + " does not exist");
		}
		return b;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void addListener() {
		bundleListener = getBundleListener();
		bundleContext.addBundleListener(bundleListener);
	}

	private BundleListener getBundleListener() {
		return new BundleListener() {
			public void bundleChanged(BundleEvent bundleEvent) {
				Notification notification = new Notification(BundleStateMBean.EVENT, objectName, sequenceNumber++);
				notification.setUserData(new OSGiBundleEvent(bundleEvent).asCompositeData());
				sendNotification(notification);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	protected void removeListener() {
		if (bundleListener != null) {
			bundleContext.removeBundleListener(bundleListener);
		}
	}

}
