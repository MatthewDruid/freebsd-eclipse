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

import java.io.File;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.spi.RegistryStrategy;
import org.eclipse.equinox.common.tests.registry.simple.utils.ExeExtensionStrategy;
import org.eclipse.equinox.common.tests.registry.simple.utils.ExecutableRegistryObject;

/**
 * Tests that executable extensions present in the simple registry actually
 * gets processed.
 * @since 3.2
 */
public class XMLExecutableExtensionTest extends BaseExtensionRegistryRun {

	public XMLExecutableExtensionTest() {
		super();
	}

	public XMLExecutableExtensionTest(String name) {
		super(name);
	}

	/**
	 * Provide own class loader to the registry executable element strategry
	 * @return - open extension registry
	 */
	@Override
	protected IExtensionRegistry startRegistry() {
		// use plugin's metadata directory to save cache data
		IPath userDataPath = getStateLocation();
		File[] registryLocations = new File[] {new File(userDataPath.toOSString())};
		boolean[] readOnly = new boolean[] {false};
		RegistryStrategy registryStrategy = new ExeExtensionStrategy(registryLocations, readOnly);
		return RegistryFactory.createRegistry(registryStrategy, masterToken, userToken);
	}

	public void testExecutableExtensionCreation() {
		// Test with non-bundle contributor
		IContributor nonBundleContributor = ContributorFactorySimple.createContributor("ABC"); //$NON-NLS-1$
		assertFalse(ExecutableRegistryObject.createCalled);

		fillRegistry(nonBundleContributor);
		assertFalse(ExecutableRegistryObject.createCalled);

		checkRegistry(nonBundleContributor.getName());
		assertTrue(ExecutableRegistryObject.createCalled);
	}

	private void fillRegistry(IContributor contributor) {
		processXMLContribution(contributor, getXML("ExecutableExtension.xml")); //$NON-NLS-1$
	}

	private void checkRegistry(String namespace) {
		IConfigurationElement[] elements = simpleRegistry.getConfigurationElementsFor(qualifiedName(namespace, "XMLExecutableExtPoint")); //$NON-NLS-1$
		assertTrue(elements.length == 1);
		for (IConfigurationElement element : elements) {
			try {
				Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
				assertNotNull(object);
			} catch (CoreException e) {
				assertTrue(false);
				e.printStackTrace();
			}
		}
	}
}
