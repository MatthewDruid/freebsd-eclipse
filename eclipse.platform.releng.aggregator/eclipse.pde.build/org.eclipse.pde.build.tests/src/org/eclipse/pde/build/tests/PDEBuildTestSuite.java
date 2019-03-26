/*******************************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which accompanies this distribution,
 * and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.pde.build.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.pde.build.internal.tests.*;
import org.eclipse.pde.build.internal.tests.p2.LicenseTests;

public class PDEBuildTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test Suite for org.eclipse.pde.build"); //$NON-NLS-1$
		suite.addTestSuite(SourceTests.class);
		suite.addTestSuite(ScriptGenerationTests.class);
		suite.addTestSuite(ProductTests.class);
		suite.addTestSuite(LicenseTests.class);
		suite.addTest(AssembleTests.suite());
		if (Boolean.valueOf(System.getProperty("pde.build.includeP2", "true")).booleanValue())
			suite.addTest(P2TestSuite.suite());
		if (System.getProperty("pde.build.includeFetch") != null)
			suite.addTestSuite(FetchTests.class);
		return suite;
	}

}
