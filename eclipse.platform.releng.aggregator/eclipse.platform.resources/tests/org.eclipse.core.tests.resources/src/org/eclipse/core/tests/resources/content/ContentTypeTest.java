/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
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
package org.eclipse.core.tests.resources.content;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.tests.harness.CoreTest;
import org.eclipse.core.tests.resources.AutomatedTests;
import org.osgi.framework.BundleContext;

/**
 * Common superclass for all content type tests.
 */
public abstract class ContentTypeTest extends CoreTest {
	public static final String PI_RESOURCES_TESTS = AutomatedTests.PI_RESOURCES_TESTS;
	public static final String TEST_FILES_ROOT = "Plugin_Testing/";

	public ContentTypeTest(String name) {
		super(name);
	}

	public BundleContext getContext() {
		return Platform.getBundle(PI_RESOURCES_TESTS).getBundleContext();
	}
}
