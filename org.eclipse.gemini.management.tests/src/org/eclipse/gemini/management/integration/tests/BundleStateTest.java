/*******************************************************************************
 * Copyright (c) 2010 SAP.
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
 *     SAP employees 
 ******************************************************************************/
package org.eclipse.gemini.management.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.eclipse.gemini.management.framework.BundleState;
import org.eclipse.gemini.management.framework.internal.OSGiBundle;
import org.eclipse.gemini.management.internal.BundleUtil;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.jmx.framework.BundleStateMBean;

/**
 * Integration tests for the {@link BundleState} implementation of {@link BundleStateMBean}
 *
 */
public final class BundleStateTest extends AbstractOSGiMBeanTest{
	
	private CompositeData bundleInfo;
	private Long identifier;
	private String location;
	private String symbolicName;
	private String version;
	private int startLevel;
	private String state;
	private long lastModified;
	private boolean persistenlyStarted;
	private boolean activationPolicyUsed;
	private boolean removalPending;
	private boolean required;
	private boolean fragment;
	private Long[] registeredServices;
	private Long[] servicesInUse;
	private Map<String, CompositeData> headers;
	private String[] exportedPackages;
	private String[] importedPackages;
	private Long[] fragments;
	private Long[] hosts;
	private Long[] requiringBundles;
	private Long[] requiredBundles;
	private Object key;
	private Object[] keysArray;
	private Bundle bundle;
	
	public BundleStateTest() {
		super.mBeanObjectName = BundleStateMBean.OBJECTNAME;
		super.addFrameworkAndUUID = true;
	}
	
	@Before
	public void before(){
		this.bundleInfo = null;
		this.identifier = null;
		this.location = null;
		this.symbolicName = null;
		this.version = null;
		this.startLevel = 0;
		this.state = null;
		this.lastModified = 0L;
		this.persistenlyStarted = false;
		this.activationPolicyUsed = false;
		this.removalPending = false;
		this.required = false;
		this.fragment = false;
		this.registeredServices = null;
		this.servicesInUse = null;
		this.headers = null;
		this.exportedPackages = null;
		this.importedPackages = null;
		this.fragments = null;
		this.hosts = null;
		this.requiringBundles = null;
		this.requiredBundles = null;
		this.key = null;
		this.keysArray = null;
		this.bundle = null;
	}
	
	@Test
	public void nameAndVersionTest() throws Exception {
		long start = System.currentTimeMillis();
		Object[] arguments = new Object[]{new String[]{BundleStateMBean.SYMBOLIC_NAME, BundleStateMBean.VERSION}};
		String[] types = new String[]{String[].class.getName()};
		TabularData table = jmxFetchData("listBundles", arguments, types, TabularData.class);
		long end = System.currentTimeMillis();
		assertTrue((end - start) < 1000);
		Set<?> keys = table.keySet();
		Iterator<?> iter = keys.iterator();
		BundleContext bc = FrameworkUtil.getBundle(BundleState.class).getBundleContext();
		while (iter.hasNext()) {
			key = iter.next();
			keysArray = ((Collection<?>) key).toArray();
			bundleInfo = table.get(keysArray);
			symbolicName = (String) bundleInfo.get(BundleStateMBean.SYMBOLIC_NAME);
			version = (String) bundleInfo.get(BundleStateMBean.VERSION);
			bundle = bc.getBundle((Long) keysArray[0]);
			assertEquals(symbolicName, bundle.getSymbolicName());
			assertEquals(version, bundle.getVersion().toString());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listTest() throws Exception {
		TabularData table = jmxFetchData("listBundles", new Object[]{}, new String[]{}, TabularData.class);
		Set<?> keys = table.keySet();
		Iterator<?> iter = keys.iterator();
		BundleContext bc = FrameworkUtil.getBundle(BundleState.class).getBundleContext();
		while (iter.hasNext()) {
			key = iter.next();
			keysArray = ((Collection<?>) key).toArray();
			bundleInfo = table.get(keysArray);

			identifier = (Long) bundleInfo.get(BundleStateMBean.IDENTIFIER);
			location = (String) bundleInfo.get(BundleStateMBean.LOCATION);
			symbolicName = (String) bundleInfo.get(BundleStateMBean.SYMBOLIC_NAME);
			version = (String) bundleInfo.get(BundleStateMBean.VERSION);
			startLevel = (Integer) bundleInfo.get(BundleStateMBean.START_LEVEL);
			state = (String) bundleInfo.get(BundleStateMBean.STATE);
			lastModified = (Long) bundleInfo.get(BundleStateMBean.LAST_MODIFIED);
			persistenlyStarted = (Boolean) bundleInfo.get(BundleStateMBean.PERSISTENTLY_STARTED);
			activationPolicyUsed = (Boolean) bundleInfo.get(BundleStateMBean.ACTIVATION_POLICY_USED);
			removalPending = (Boolean) bundleInfo.get(BundleStateMBean.REMOVAL_PENDING);
			required = (Boolean) bundleInfo.get(BundleStateMBean.REQUIRED);
			fragment = (Boolean) bundleInfo.get(BundleStateMBean.FRAGMENT);
			registeredServices = (Long[]) bundleInfo.get(BundleStateMBean.REGISTERED_SERVICES);
			servicesInUse = (Long[]) bundleInfo.get(BundleStateMBean.SERVICES_IN_USE);
			headers = (Map<String, CompositeData>) bundleInfo.get(BundleStateMBean.HEADERS);
			exportedPackages = (String[]) bundleInfo.get(BundleStateMBean.EXPORTED_PACKAGES);
			importedPackages = (String[]) bundleInfo.get(BundleStateMBean.IMPORTED_PACKAGES);
			fragments = (Long[]) bundleInfo.get(BundleStateMBean.FRAGMENTS);
			hosts = (Long[]) bundleInfo.get(BundleStateMBean.HOSTS);
			requiringBundles = (Long[]) bundleInfo.get(BundleStateMBean.REQUIRING_BUNDLES);
			requiredBundles = (Long[]) bundleInfo.get(BundleStateMBean.REQUIRED_BUNDLES);

			bundle = bc.getBundle((Long) keysArray[0]);
			assertEquals(identifier, Long.valueOf(bundle.getBundleId()));
			assertEquals(location, bundle.getLocation());
			assertEquals(symbolicName, bundle.getSymbolicName());
			assertEquals(version, bundle.getVersion().toString());
			assertEquals(startLevel, bundle.adapt(BundleStartLevel.class).getStartLevel());
			assertEquals(state, stateToString(bundle.getState()));
			assertEquals(lastModified, bundle.getLastModified());
			assertEquals(persistenlyStarted, BundleUtil.isBundlePersistentlyStarted(bundle));
			assertEquals(activationPolicyUsed, BundleUtil.isBundleActivationPolicyUsed(bundle));
			assertEquals(removalPending, BundleUtil.isRemovalPending(bundle));
			assertEquals(required, BundleUtil.isRequired(bundle));
			assertEquals(fragment, BundleUtil.isBundleFragment(bundle));

			Long[] rs2 = serviceIds(bundle.getRegisteredServices());
			Arrays.sort(registeredServices);
			Arrays.sort(rs2);
			assertTrue(Arrays.equals(registeredServices, rs2));

			Long[] siu2 = serviceIds(bundle.getServicesInUse());
			Arrays.sort(servicesInUse);
			Arrays.sort(siu2);
			assertTrue(Arrays.equals(servicesInUse, siu2));

			assertEquals((TabularData) headers,	OSGiBundle.headerTable(bundle.getHeaders()));

			String[] exportedPackages2 = BundleUtil.getBundleExportedPackages(bundle);
			Arrays.sort(exportedPackages);
			Arrays.sort(exportedPackages2);
			assertTrue(Arrays.equals(exportedPackages, exportedPackages2));

			String[] importedPackages2 = BundleUtil.getBundleImportedPackages(bundle);
			Arrays.sort(importedPackages);
			Arrays.sort(importedPackages2);
			assertTrue(Arrays.equals(importedPackages, importedPackages2));

			if (!BundleUtil.isBundleFragment(bundle)) {
				Long[] frags2 = getBundleFragments(bundle);
				Arrays.sort(fragments);
				Arrays.sort(frags2);
				assertTrue(Arrays.equals(fragments, frags2));
			}

			Long[] hst2 = getBundleHosts(bundle);
			Arrays.sort(hosts);
			Arrays.sort(hst2);
			assertTrue(Arrays.equals(hosts, hst2));

			Long[] reqB2 = getRequiringBundles(bundle);
			Arrays.sort(requiringBundles);
			Arrays.sort(reqB2);
			assertTrue(Arrays.equals(requiringBundles, reqB2));

			Long[] requiredB2 = getRequiredBundles(bundle);
			Arrays.sort(requiredBundles);
			Arrays.sort(requiredB2);
			assertTrue(Arrays.equals(requiredBundles, requiredB2));
		}
	}

	private String stateToString(int state) {
		switch (state) {
			case Bundle.ACTIVE:
				return "ACTIVE";
			case Bundle.INSTALLED:
				return "INSTALLED";
			case Bundle.RESOLVED:
				return "RESOLVED";
			case Bundle.STARTING:
				return "STARTING";
			case Bundle.STOPPING:
				return "STOPPING";
			case Bundle.UNINSTALLED:
				return "UNINSTALLED";
			default:
				return "UNKNOWN";
		}
	}

    private Long[] getRequiredBundles(Bundle bundle) {
        BundleWiring wiring = bundle.adapt(BundleWiring.class);
		if (wiring == null) {
			return new Long[0];
		}
        List<BundleWire> requiredWires = wiring.getRequiredWires(null);//BundleRevision.BUNDLE_NAMESPACE);
        return bundleWiresToProviderIds(requiredWires);
    }

    private Long[] getRequiringBundles(Bundle bundle) {
        BundleWiring wiring = bundle.adapt(BundleWiring.class);
		if (wiring == null) {
			return new Long[0];
		}
        List<BundleWire> providedWires = wiring.getProvidedWires(null);//BundleRevision.BUNDLE_NAMESPACE);
        return bundleWiresToRequirerIds(providedWires);
    }

	private Long[] getBundleFragments(Bundle bundle) {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);
		if (wiring == null) {
			return new Long[0];
		}
		List<BundleWire> requiredWires = wiring.getRequiredWires(BundleRevision.HOST_NAMESPACE);
        return bundleWiresToProviderIds(requiredWires);
	}

	private Long[] getBundleHosts(Bundle bundle) {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);
		if (wiring == null) {
			return new Long[0];
		}
		List<BundleWire> providedWires = wiring.getProvidedWires(BundleRevision.HOST_NAMESPACE);
        return bundleWiresToRequirerIds(providedWires);
	}

	private Long[] bundleWiresToRequirerIds(List<BundleWire> wires){
        Long[] requirerIds = new Long[wires.size()];
        int i = 0;
        for (BundleWire bundleWire : wires) {
            requirerIds[i] = bundleWire.getRequirerWiring().getBundle().getBundleId();
            i++;
        }
        return requirerIds;
	}

	private Long[] bundleWiresToProviderIds(List<BundleWire> wires){
        Long[] providerIds = new Long[wires.size()];
        int i = 0;
        for (BundleWire bundleWire : wires) {
            providerIds[i] = bundleWire.getProviderWiring().getBundle().getBundleId();
            i++;
        }
        return providerIds;
	}
	
	private Long[] serviceIds(ServiceReference<?>[] refs) {
		if (refs == null) {
			return new Long[0];
		}
		Long[] ids = new Long[refs.length];
		for (int i = 0; i < refs.length; i++) {
			ids[i] = (Long) refs[i].getProperty(Constants.SERVICE_ID);
		}
		return ids;
	}
}

