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
package org.eclipse.pde.internal.ui.views.features.action;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.pde.internal.core.FeatureModelManager;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.views.features.FeaturesView;
import org.eclipse.pde.internal.ui.views.features.support.FeatureIndex;
import org.eclipse.pde.internal.ui.views.features.viewer.FeatureTreeCallersContentProvider;

public class ShowCallersContentProviderAction extends ContentProviderAction {

	private final FeatureIndex fFeatureIndex;

	public ShowCallersContentProviderAction(FeaturesView featuresView,
			FeatureModelManager featureModelManager, FeatureIndex featureIndex) {
		super(featuresView, featureModelManager);
		fFeatureIndex = featureIndex;

		setDescription(PDEUIMessages.FeaturesView_ShowCallersContentProviderAction_description);
		setToolTipText(PDEUIMessages.FeaturesView_ShowCallersContentProviderAction_tooltip);
		setImageDescriptor(PDEPluginImages.DESC_CALLERS);
	}

	@Override
	public ViewerComparator createViewerComparator() {
		return new ViewerComparator();
	}

	@Override
	public IContentProvider createContentProvider() {
		return new FeatureTreeCallersContentProvider(fFeatureModelManager, fFeatureIndex);
	}

	@Override
	public boolean isSupportsFilters() {
		return false;
	}

	@Override
	public boolean isSupportsPlugins() {
		return false;
	}

}