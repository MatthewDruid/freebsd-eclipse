/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
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
 * Martin Oberhuber (Wind River) - [] add SymlinkTest tests
 *******************************************************************************/
package org.eclipse.core.tests.filesystem;

import junit.framework.*;

/**
 * Class for collecting all test classes that deal with the file system API.
 */
public class AllTests extends TestCase {
	public AllTests() {
		super(null);
	}

	public AllTests(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		suite.addTestSuite(CreateDirectoryTest.class);
		suite.addTestSuite(DeleteTest.class);
		suite.addTest(EFSTest.suite());
		suite.addTest(FileCacheTest.suite());
		suite.addTest(FileStoreTest.suite());
		suite.addTestSuite(OpenOutputStreamTest.class);
		suite.addTestSuite(PutInfoTest.class);
		suite.addTestSuite(SymlinkTest.class);
		suite.addTestSuite(URIUtilTest.class);
		return suite;
	}
}
