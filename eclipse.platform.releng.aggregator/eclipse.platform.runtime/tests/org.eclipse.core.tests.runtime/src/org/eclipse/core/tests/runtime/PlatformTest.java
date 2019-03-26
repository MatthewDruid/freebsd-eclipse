/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
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
package org.eclipse.core.tests.runtime;

import java.io.*;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.osgi.framework.ServiceReference;

/**
 * Test cases for the Platform API
 */
public class PlatformTest extends RuntimeTest {

	private FrameworkLog logService;
	private ServiceReference<FrameworkLog> logRef;
	private java.io.File originalLocation;

	/**
	 * Need a zero argument constructor to satisfy the test harness.
	 * This constructor should not do any real work nor should it be
	 * called by user code.
	 */
	public PlatformTest() {
		super(null);
	}

	public PlatformTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		//ensure platform locations are initialized
		Platform.getLogFileLocation();

		//setup reference to log service, and remember original log location
		logRef = RuntimeTestsPlugin.getContext().getServiceReference(FrameworkLog.class);
		logService = RuntimeTestsPlugin.getContext().getService(logRef);
		originalLocation = logService.getFile();
	}

	@Override
	protected void tearDown() throws Exception {
		//undo any damage done by log location test
		super.tearDown();
		logService.setFile(originalLocation, true);
		RuntimeTestsPlugin.getContext().ungetService(logRef);
	}

	public void testGetCommandLine() {
		assertNotNull("1.0", Platform.getCommandLineArgs());
	}

	public void testGetLocation() {
		assertNotNull("1.0", Platform.getLocation());
	}

	/**
	 * API test for {@link Platform#getResourceBundle(org.osgi.framework.Bundle)}
	 */
	public void testGetResourceBundle() {
		//ensure it returns non-null for bundle with a resource bundle
		ResourceBundle bundle = Platform.getResourceBundle(Platform.getBundle("org.eclipse.core.runtime"));
		assertNotNull(bundle);
		//ensure it throws exception for bundle with no resource bundle
		boolean failed = false;
		try {
			bundle = Platform.getResourceBundle(Platform.getBundle("org.eclipse.core.tests.runtime"));
		} catch (MissingResourceException e) {
			//expected
			failed = true;
		}
		assertTrue(failed);
	}

	public void testGetLogLocation() throws IOException {
		IPath initialLocation = Platform.getLogFileLocation();
		System.out.println(Platform.getLogFileLocation());
		Platform.getStateLocation(Platform.getBundle("org.eclipse.equinox.common"));//causes DataArea to be initialzed
		System.out.println(Platform.getLogFileLocation());

		assertNotNull("1.0", initialLocation);

		//ensure result is same as log service
		IPath logPath = new Path(logService.getFile().getAbsolutePath());
		assertEquals("2.0", logPath, initialLocation);

		//changing log service location should change log location
		File newLocation = File.createTempFile("testGetLogLocation", null);
		logService.setFile(newLocation, true);
		assertEquals("3.0", new Path(newLocation.getAbsolutePath()), Platform.getLogFileLocation());

		//when log is non-local, should revert to default location
		logService.setWriter(new StringWriter(), true);
		assertEquals("4.0", initialLocation, Platform.getLogFileLocation());
	}

	public void testRunnable() {
		final Vector<Throwable> exceptions = new Vector<>();

		final List<IStatus> collected = new ArrayList<>();

		// add a log listener to ensure that we report using the right plug-in id
		ILogListener logListener = new ILogListener() {
			@Override
			public void logging(IStatus status, String plugin) {
				collected.add(status);
			}
		};
		Platform.addLogListener(logListener);

		final Exception exception = new Exception("PlatformTest.testRunnable: this exception is thrown on purpose as part of the test.");
		ISafeRunnable runnable = new ISafeRunnable() {
			@Override
			public void handleException(Throwable t) {
				exceptions.addElement(t);
			}

			@Override
			public void run() throws Exception {
				throw exception;
			}
		};

		SafeRunner.run(runnable);

		Platform.removeLogListener(logListener);

		assertEquals("1.0", exceptions.size(), 1);
		assertEquals("1.1", exception, exceptions.firstElement());

		// ensures the status object produced has the right plug-in id (bug 83614)
		assertEquals("2.0", collected.size(), 1);
		assertEquals("2.1", RuntimeTest.PI_RUNTIME_TESTS, collected.get(0).getPlugin());
	}

}
