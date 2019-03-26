/*******************************************************************************
 * Copyright (c) 2005, 2018 IBM Corporation and others.
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
package org.eclipse.equinox.common.tests.registry.simple;

import junit.framework.TestCase;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

/**
 * Tests registry token-based access rules.
 * @since 3.2
 */
public class TokenAccessTest extends TestCase {

	public TokenAccessTest() {
		super();
	}

	public TokenAccessTest(String name) {
		super(name);
	}

	/**
	 * Tests token access to sensetive registry methods
	 */
	public void testControlledAccess() {
		Object tokenGood = new Object();
		Object tokenBad = new Object();

		// registry created with no token
		IExtensionRegistry registry = RegistryFactory.createRegistry(null, null, null);
		assertNotNull(registry);
		// and stopped with no token - should be no exception
		registry.stop(null);

		// registry created with no token
		registry = RegistryFactory.createRegistry(null, null, null);
		assertNotNull(registry);
		// and stopped with a bad - should be no exception
		registry.stop(tokenBad);

		// registry created with a good token
		registry = RegistryFactory.createRegistry(null, tokenGood, null);
		assertNotNull(registry);
		// and stopped with a good token - should be no exception
		registry.stop(tokenGood);

		// registry created with a good token
		registry = RegistryFactory.createRegistry(null, tokenGood, null);
		assertNotNull(registry);
		// and stopped with a bad token - should be an exception
		boolean bException = false;
		try {
			registry.stop(tokenBad);
		} catch (IllegalArgumentException e) {
			// this is good; this is expected
			bException = true;
		}
		assertTrue(bException);
	}
}
