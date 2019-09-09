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
package org.eclipse.pde.internal.ui.views.features.viewer;

import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.pde.internal.core.ifeature.*;
import org.eclipse.pde.internal.ui.views.features.support.FeatureSupport;

public class FeatureViewerComparator extends ViewerComparator {

	@Override
	public int category(Object element) {
		if (element instanceof IFeatureChild) {
			element = FeatureSupport.toFeatureModel(element);
			if (element == null) {
				return 4;
			}
		}

		if (element instanceof IFeatureModel) {
			IFeatureModel featureModel = (IFeatureModel) element;
			boolean editable = featureModel.isEditable();
			boolean hasChildren = featureModel.getFeature().getIncludedFeatures().length > 0;

			if (hasChildren && editable) {
				return 0;
			} else if (hasChildren) {
				return 1;
			} else if (editable) {
				return 2;
			} else {
				return 3;
			}
		} else if (element instanceof IFeaturePlugin) {
			return 5;
		}

		return 6;
	}

}
