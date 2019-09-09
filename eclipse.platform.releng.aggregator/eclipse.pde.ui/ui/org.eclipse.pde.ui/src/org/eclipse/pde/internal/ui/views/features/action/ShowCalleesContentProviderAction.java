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
import org.eclipse.pde.internal.ui.views.features.viewer.FeatureTreeCalleesContentProvider;
import org.eclipse.pde.internal.ui.views.features.viewer.FeatureViewerComparator;

public class ShowCalleesContentProviderAction extends ContentProviderAction {

	public ShowCalleesContentProviderAction(FeaturesView featuresView,
			FeatureModelManager featureModelManager) {
		super(featuresView, featureModelManager);

		setDescription(PDEUIMessages.FeaturesView_ShowCalleesContentProviderAction_description);
		setToolTipText(PDEUIMessages.FeaturesView_ShowCalleesContentProviderAction_tooltip);
		setImageDescriptor(PDEPluginImages.DESC_CALLEES);
	}

	@Override
	public ViewerComparator createViewerComparator() {
		return new FeatureViewerComparator();
	}

	@Override
	public IContentProvider createContentProvider() {
		return new FeatureTreeCalleesContentProvider(fFeatureModelManager);
	}

	@Override
	public boolean isSupportsFilters() {
		return true;
	}

	@Override
	public boolean isSupportsPlugins() {
		return true;
	}

}