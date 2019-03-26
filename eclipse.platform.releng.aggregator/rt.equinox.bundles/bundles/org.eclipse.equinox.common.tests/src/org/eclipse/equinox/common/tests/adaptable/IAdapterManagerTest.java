/*******************************************************************************
 *  Copyright (c) 2004, 2018 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.common.tests.adaptable;

import java.io.IOException;
import java.net.MalformedURLException;
import junit.framework.TestCase;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.runtime.*;
import org.eclipse.core.tests.harness.BundleTestingHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

/**
 * Tests API on the IAdapterManager class.
 */
public class IAdapterManagerTest extends TestCase {
	//following classes are for testComputeClassOrder
	static interface C {
	}

	static interface D {
	}

	static interface M {
	}

	static interface N {
	}

	static interface O {
	}

	interface A extends M, N {
	}

	interface B extends O {
	}

	class Y implements C, D {
	}

	class X extends Y implements A, B {
	}

	private static final String NON_EXISTING = "com.does.not.Exist";
	private static final String TEST_ADAPTER = "org.eclipse.equinox.common.tests.adaptable.TestAdapter";
	private static final String TEST_ADAPTER_CL = "testAdapter.testUnknown";
	private IAdapterManager manager;

	public IAdapterManagerTest(String name) {
		super(name);
	}

	public IAdapterManagerTest() {
		super("");
	}

	@Override
	protected void setUp() throws Exception {
		manager = AdapterManager.getDefault();
	}

	/**
	 * Tests API method IAdapterManager.hasAdapter.
	 */
	public void testHasAdapter() {
		TestAdaptable adaptable = new TestAdaptable();
		//request non-existing adaptable
		assertTrue("1.0", !manager.hasAdapter("", NON_EXISTING));

		//request adapter that is in XML but has no registered factory
		assertTrue("1.1", manager.hasAdapter(adaptable, TEST_ADAPTER));

		//request adapter that is not in XML
		assertTrue("1.2", !manager.hasAdapter(adaptable, "java.lang.String"));

		//register an adapter factory that maps adaptables to strings
		IAdapterFactory fac = new IAdapterFactory() {
			@Override
			public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
				if (adapterType == String.class) {
					return adapterType.cast(adaptableObject.toString());
				}
				return null;
			}

			@Override
			public Class<?>[] getAdapterList() {
				return new Class[] {String.class};
			}
		};
		manager.registerAdapters(fac, TestAdaptable.class);
		try {
			//request adapter for factory that we've just added
			assertTrue("1.3", manager.hasAdapter(adaptable, "java.lang.String"));
		} finally {
			manager.unregisterAdapters(fac, TestAdaptable.class);
		}

		//request adapter that was unloaded
		assertTrue("1.4", !manager.hasAdapter(adaptable, "java.lang.String"));
	}

	/**
	 * Tests API method IAdapterManager.getAdapter.
	 */
	public void testGetAdapter() {
		TestAdaptable adaptable = new TestAdaptable();
		//request non-existing adaptable
		assertNull("1.0", manager.getAdapter("", NON_EXISTING));

		//request adapter that is in XML but has no registered factory
		Object result = manager.getAdapter(adaptable, TEST_ADAPTER);
		assertTrue("1.1", result instanceof TestAdapter);

		//request adapter that is not in XML
		assertNull("1.2", manager.getAdapter(adaptable, "java.lang.String"));

		//register an adapter factory that maps adaptables to strings
		IAdapterFactory fac = new IAdapterFactory() {
			@Override
			public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
				if (adapterType == String.class) {
					return adapterType.cast(adaptableObject.toString());
				}
				return null;
			}

			@Override
			public Class<?>[] getAdapterList() {
				return new Class[] {String.class};
			}
		};
		manager.registerAdapters(fac, TestAdaptable.class);
		try {
			//request adapter for factory that we've just added
			result = manager.getAdapter(adaptable, "java.lang.String");
			assertTrue("1.3", result instanceof String);
		} finally {
			manager.unregisterAdapters(fac, TestAdaptable.class);
		}
		//request adapter that was unloaded
		assertNull("1.4", manager.getAdapter(adaptable, "java.lang.String"));
	}

	public void testGetAdapterNullArgs() {
		TestAdaptable adaptable = new TestAdaptable();
		try {
			manager.getAdapter(adaptable, (Class<?>) null);
			fail("1.0");
		} catch (RuntimeException e) {
			//expected
		}
		try {
			manager.getAdapter(null, NON_EXISTING);
			fail("1.0");
		} catch (RuntimeException e) {
			//expected
		}

	}

	/**
	 * Tests API method IAdapterManager.loadAdapter.
	 */
	public void testLoadAdapter() {
		TestAdaptable adaptable = new TestAdaptable();
		//request non-existing adaptable
		assertNull("1.0", manager.loadAdapter("", NON_EXISTING));

		//request adapter that is in XML but has no registered factory
		Object result = manager.loadAdapter(adaptable, TEST_ADAPTER);
		assertTrue("1.1", result instanceof TestAdapter);

		//request adapter that is not in XML
		assertNull("1.2", manager.loadAdapter(adaptable, "java.lang.String"));

		//register an adapter factory that maps adaptables to strings
		IAdapterFactory fac = new IAdapterFactory() {
			@Override
			public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
				if (adapterType == String.class) {
					return adapterType.cast(adaptableObject.toString());
				}
				return null;
			}

			@Override
			public Class<?>[] getAdapterList() {
				return new Class[] {String.class};
			}
		};
		manager.registerAdapters(fac, TestAdaptable.class);
		try {
			//request adapter for factory that we've just added
			result = manager.loadAdapter(adaptable, "java.lang.String");
			assertTrue("1.3", result instanceof String);
		} finally {
			manager.unregisterAdapters(fac, TestAdaptable.class);
		}
		//request adapter that was unloaded
		assertNull("1.4", manager.loadAdapter(adaptable, "java.lang.String"));
	}

	/**
	 * Test adapting to classes not reachable by the default bundle class loader
	 * (bug 200068).
	 * NOTE: This test uses .class file compiled with 1.4 JRE. As a result,
	 * the test can not be run on pre-1.4 JRE.
	 */
	public void testAdapterClassLoader() throws MalformedURLException, BundleException, IOException {
		TestAdaptable adaptable = new TestAdaptable();
		assertTrue(manager.hasAdapter(adaptable, TEST_ADAPTER_CL));
		assertNull(manager.loadAdapter(adaptable, TEST_ADAPTER_CL));
		Bundle bundle = null;
		try {
			BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
			bundle = BundleTestingHelper.installBundle("0.1", bundleContext, "Plugin_Testing/adapters/testAdapter_1.0.0");
			BundleTestingHelper.refreshPackages(bundleContext, new Bundle[] {bundle});

			assertTrue(manager.hasAdapter(adaptable, TEST_ADAPTER_CL));
			Object result = manager.loadAdapter(adaptable, TEST_ADAPTER_CL);
			assertNotNull(result);
			assertTrue(TEST_ADAPTER_CL.equals(result.getClass().getName()));
		} finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	/**
	 * Tests for {@link IAdapterManager#computeClassOrder(Class)}.
	 */
	public void testComputeClassOrder() {
		Class<?>[] expected = new Class[] { X.class, Y.class, Object.class, A.class, B.class, M.class, N.class, O.class,
				C.class, D.class };
		Class<?>[] actual = manager.computeClassOrder(X.class);
		assertEquals("1.0", expected.length, actual.length);
		for (int i = 0; i < actual.length; i++) {
			assertEquals("1.1." + i, expected[i], actual[i]);
		}
	}

	public void testFactoryViolatingContract() {
		class Private {
		}

		IAdapterFactory fac = new IAdapterFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
				if (adapterType == Private.class) {
					// Cast below violates the contract of the factory
					return (T) Boolean.FALSE;
				}
				return null;
			}

			@Override
			public Class<?>[] getAdapterList() {
				return new Class[] { Private.class };
			}
		};
		try {
			manager.registerAdapters(fac, Private.class);
			try {
				manager.getAdapter(new Private(), Private.class);
				fail("Should throw AssertionFailedException!");
			} catch (AssertionFailedException e) {
				assertTrue(e.getMessage().contains(fac.getClass().getName()));
				assertTrue(e.getMessage().contains(Boolean.class.getName()));
				assertTrue(e.getMessage().contains(Private.class.getName()));
			}
		} finally {
			manager.unregisterAdapters(fac, Private.class);
		}
	}
}
