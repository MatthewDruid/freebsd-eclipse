/*******************************************************************************
 * Copyright (c) 2007, 2018 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.common.tests.registry;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.tests.harness.BundleTestingHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

/**
 * Tests "new" registry event listener.
 * @since 3.4
 */
public class RegistryListenerTest extends TestCase {

	final private static int MAX_TIME_PER_BUNDLE = 10000; // maximum time to wait for bundle event in milliseconds

	private BundleContext fBundleContext;
	
	public RegistryListenerTest() {
		super();
	}

	public RegistryListenerTest(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fBundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
	}

	/**
	 * Producer and consumer bundles are installed and removed in a "normal" order
	 */
	public void testRegularOrder() throws IOException, BundleException {
		Bundle bundle01 = null;
		Bundle bundle02 = null;
		WaitingRegistryListener listener = new WaitingRegistryListener();
		listener.register("bundle01.xp1");
		try {
			bundle01 = BundleTestingHelper.installBundle("0.1", fBundleContext, "Plugin_Testing/registryListener/bundle01");
			bundle02 = BundleTestingHelper.installBundle("0.2", fBundleContext, "Plugin_Testing/registryListener/bundle02");

			Bundle[] testBundles = new Bundle[] {bundle01, bundle02};
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			String[] extPointIDs = listener.extPointsReceived(2 * MAX_TIME_PER_BUNDLE);
			String[] extensionsReceived = listener.extensionsReceived(2 * MAX_TIME_PER_BUNDLE);
			assertTrue(listener.isAdded());

			assertNotNull(extPointIDs);
			assertTrue(extPointIDs.length == 1);
			assertTrue("bundle01.xp1".equals(extPointIDs[0]));

			assertNotNull(extensionsReceived);
			assertTrue(extensionsReceived.length == 1);
			assertTrue("bundle02.ext1".equals(extensionsReceived[0]));

			listener.reset();

			bundle02.uninstall();
			bundle02 = null; // reset as early as possible in case of exception
			bundle01.uninstall();
			bundle01 = null; // reset as early as possible in case of exception
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			extPointIDs = listener.extPointsReceived(2 * MAX_TIME_PER_BUNDLE);
			extensionsReceived = listener.extensionsReceived(2 * MAX_TIME_PER_BUNDLE);
			assertTrue(listener.isRemoved());

			assertNotNull(extPointIDs);
			assertTrue(extPointIDs.length == 1);
			assertTrue("bundle01.xp1".equals(extPointIDs[0]));

			assertNotNull(extensionsReceived);
			assertTrue(extensionsReceived.length == 1);
			assertTrue("bundle02.ext1".equals(extensionsReceived[0]));

		} finally {
			listener.unregister();
			if (bundle01 != null) {
				bundle01.uninstall();
			}
			if (bundle02 != null) {
				bundle02.uninstall();
			}
		}
	}

	/**
	 * Producer and consumer bundles are installed and removed in an inverse order
	 */
	public void testInverseOrder() throws IOException, BundleException {
		Bundle bundle01 = null;
		Bundle bundle02 = null;
		WaitingRegistryListener listener = new WaitingRegistryListener();
		listener.register("bundle01.xp1");
		try {
			bundle02 = BundleTestingHelper.installBundle("0.2", fBundleContext, "Plugin_Testing/registryEvents/bundle02");
			bundle01 = BundleTestingHelper.installBundle("0.1", fBundleContext, "Plugin_Testing/registryEvents/bundle01");

			Bundle[] testBundles = new Bundle[] {bundle01, bundle02};
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			String[] extPointIDs = listener.extPointsReceived(2 * MAX_TIME_PER_BUNDLE);
			String[] extensionsReceived = listener.extensionsReceived(2 * MAX_TIME_PER_BUNDLE);
			assertTrue(listener.isAdded());

			assertNotNull(extPointIDs);
			assertTrue(extPointIDs.length == 1);
			assertTrue("bundle01.xp1".equals(extPointIDs[0]));

			assertNotNull(extensionsReceived);
			assertTrue(extensionsReceived.length == 1);
			assertTrue("bundle02.ext1".equals(extensionsReceived[0]));

			listener.reset();

			bundle01.uninstall();
			bundle01 = null; // reset as early as possible in case of exception
			bundle02.uninstall();
			bundle02 = null; // reset as early as possible in case of exception
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			extPointIDs = listener.extPointsReceived(2 * MAX_TIME_PER_BUNDLE);
			extensionsReceived = listener.extensionsReceived(2 * MAX_TIME_PER_BUNDLE);
			assertTrue(listener.isRemoved());

			assertNotNull(extPointIDs);
			assertTrue(extPointIDs.length == 1);
			assertTrue("bundle01.xp1".equals(extPointIDs[0]));

			assertNotNull(extensionsReceived);
			assertTrue(extensionsReceived.length == 1);
			assertTrue("bundle02.ext1".equals(extensionsReceived[0]));

		} finally {
			listener.unregister();
			if (bundle02 != null) {
				bundle02.uninstall();
			}
			if (bundle01 != null) {
				bundle01.uninstall();
			}
		}
	}

	/**
	 * Tests modifications to multiple extensions and extension points
	 * Three listeners are tested: global; on xp1 (two extensions); on xp2 (no extensions)
	 */
	public void testMultiplePoints() throws IOException, BundleException {
		Bundle bundle = null;
		WaitingRegistryListener listenerGlobal = new WaitingRegistryListener();
		listenerGlobal.register(null);
		WaitingRegistryListener listener1 = new WaitingRegistryListener();
		listener1.register("bundleMultiple.xp1");
		WaitingRegistryListener listener2 = new WaitingRegistryListener();
		listener2.register("bundleMultiple.xp2");
		try {
			bundle = BundleTestingHelper.installBundle("0.1", fBundleContext, "Plugin_Testing/registryListener/bundleMultiple");

			Bundle[] testBundles = new Bundle[] {bundle};
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			// test additions on global listener
			String[] extPointIDs = listenerGlobal.extPointsReceived(MAX_TIME_PER_BUNDLE);
			String[] extensionsReceived = listenerGlobal.extensionsReceived(MAX_TIME_PER_BUNDLE);
			assertTrue(listenerGlobal.isAdded());
			checkIDs(extPointIDs, new String[] {"bundleMultiple.xp1", "bundleMultiple.xp2"});
			checkIDs(extensionsReceived, new String[] {"bundleMultiple.ext11", "bundleMultiple.ext12"});

			// test additions on listener on extension point with extensions
			String[] extPointIDs1 = listener1.extPointsReceived(20000);
			String[] extensionsReceived1 = listener1.extensionsReceived(20000);
			assertTrue(listener1.isAdded());
			checkIDs(extPointIDs1, new String[] {"bundleMultiple.xp1"});
			checkIDs(extensionsReceived1, new String[] {"bundleMultiple.ext11", "bundleMultiple.ext12"});

			// test additions on listener on extension point with no extensions
			String[] extPointIDs2 = listener2.extPointsReceived(MAX_TIME_PER_BUNDLE);
			String[] extensionsReceived2 = listener2.extensionsReceived(50);
			assertTrue(listener2.isAdded());
			checkIDs(extPointIDs2, new String[] {"bundleMultiple.xp2"});
			assertNull(extensionsReceived2);

			// removal
			listenerGlobal.reset();
			listener1.reset();
			listener2.reset();
			bundle.uninstall();
			bundle = null; // reset as early as possible in case of exception
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			// test removals on global listener
			extPointIDs = listenerGlobal.extPointsReceived(MAX_TIME_PER_BUNDLE);
			extensionsReceived = listenerGlobal.extensionsReceived(MAX_TIME_PER_BUNDLE);
			assertTrue(listenerGlobal.isRemoved());
			checkIDs(extPointIDs, new String[] {"bundleMultiple.xp1", "bundleMultiple.xp2"});
			checkIDs(extensionsReceived, new String[] {"bundleMultiple.ext11", "bundleMultiple.ext12"});

			// test removals on listener on extension point with extensions
			extPointIDs1 = listener1.extPointsReceived(MAX_TIME_PER_BUNDLE);
			extensionsReceived1 = listener1.extensionsReceived(MAX_TIME_PER_BUNDLE);
			assertTrue(listener1.isRemoved());
			checkIDs(extPointIDs1, new String[] {"bundleMultiple.xp1"});
			checkIDs(extensionsReceived1, new String[] {"bundleMultiple.ext11", "bundleMultiple.ext12"});

			// test removals on listener on extension point with no extensions
			extPointIDs2 = listener2.extPointsReceived(MAX_TIME_PER_BUNDLE);
			extensionsReceived2 = listener2.extensionsReceived(50);
			assertTrue(listener2.isRemoved());
			checkIDs(extPointIDs2, new String[] {"bundleMultiple.xp2"});
			assertNull(extensionsReceived2);

		} finally {
			listenerGlobal.unregister();
			listener1.unregister();
			listener2.unregister();
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	/**
	 * Tests listener registered multiple times: once on xp1, once on xp2
	 */
	public void testMultipleRegistrations() throws IOException, BundleException {
		Bundle bundle = null;
		WaitingRegistryListener listener = new WaitingRegistryListener();
		RegistryFactory.getRegistry().addListener(listener, "bundleMultiple.xp1");
		RegistryFactory.getRegistry().addListener(listener, "bundleMultiple.xp1");
		try {
			bundle = BundleTestingHelper.installBundle("0.1", fBundleContext, "Plugin_Testing/registryListener/bundleMultiple");

			Bundle[] testBundles = new Bundle[] {bundle};
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			// 1st registration: extension point; extension		=> 2 callbacks
			// 2nd registration should be ignored: extension	=> 0 callbacks
			// total: 2 callbacks
			assertTrue(listener.waitFor(2, MAX_TIME_PER_BUNDLE) == 2);

			// test additions on listener on extension point with extensions
			String[] extPointIDs = listener.extPointsReceived(50);
			String[] extensionsReceived = listener.extensionsReceived(50);
			assertTrue(listener.isAdded());
			checkIDs(extPointIDs, new String[] {"bundleMultiple.xp1"});
			checkIDs(extensionsReceived, new String[] {"bundleMultiple.ext11", "bundleMultiple.ext12"});

			// removal: unregistering listener once should remove both registrations
			listener.reset();
			listener.unregister();
			bundle.uninstall();
			bundle = null; // reset as early as possible in case of exception
			BundleTestingHelper.refreshPackages(fBundleContext, testBundles);

			// test removals on listener on extension point with extensions
			assertTrue(listener.waitFor(3, 200) == 0);
			extPointIDs = listener.extPointsReceived(50);
			extensionsReceived = listener.extensionsReceived(50);
			assertNull(extPointIDs);
			assertNull(extensionsReceived);
		} finally {
			// second unregistration should have no effect
			listener.unregister();
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	// For simplicity, this method does not expect duplicate IDs in either array
	private void checkIDs(String[] receivedIDs, String[] expectedIDs) {
		assertNotNull(receivedIDs);
		assertNotNull(expectedIDs);
		assertTrue(receivedIDs.length == expectedIDs.length);
		for (String expected : expectedIDs) {
			boolean found = false;
			for (String receivedID : receivedIDs) {
				if (expected.equals(receivedID)) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}
}
