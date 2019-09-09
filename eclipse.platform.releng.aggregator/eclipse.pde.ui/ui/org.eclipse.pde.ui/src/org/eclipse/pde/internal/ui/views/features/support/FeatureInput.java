/*******************************************************************************
 * Copyright (c) 2019 Ed Scadding.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Ed Scadding <edscadding@secondfiddle.org.uk> - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.views.features.support;

import org.eclipse.pde.internal.core.FeatureModelManager;

public class FeatureInput {

	private final FeatureModelManager fFeatureModelManager;

	private boolean fIncludePlugins;

	public FeatureInput(FeatureModelManager featureModelManager) {
		fFeatureModelManager = featureModelManager;
	}

	public FeatureModelManager getFeatureModelManager() {
		return fFeatureModelManager;
	}

	public boolean isIncludePlugins() {
		return fIncludePlugins;
	}

	public void setIncludePlugins(boolean includePlugins) {
		fIncludePlugins = includePlugins;
	}

}
