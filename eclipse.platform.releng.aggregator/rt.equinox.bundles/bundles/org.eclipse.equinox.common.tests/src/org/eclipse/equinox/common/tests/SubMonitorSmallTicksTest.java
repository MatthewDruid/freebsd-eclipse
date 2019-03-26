/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
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
package org.eclipse.equinox.common.tests;

import junit.framework.TestCase;
import org.eclipse.core.runtime.SubMonitor;

/**
 * Ensures that creating a SubMonitor with a small number of
 * ticks will not prevent it from reporting accurate progress.
 */
public class SubMonitorSmallTicksTest extends TestCase {

	private TestProgressMonitor topmostMonitor;
	private SubMonitor smallTicksChild;
	private long startTime;

	private static int TOTAL_WORK = 1000;

	@Override
	protected void setUp() throws Exception {
		topmostMonitor = new TestProgressMonitor();
		smallTicksChild = SubMonitor.convert(topmostMonitor, 10);
		super.setUp();
		startTime = System.currentTimeMillis();
	}

	public void testWorked() {
		SubMonitor bigTicksChild = smallTicksChild.newChild(10).setWorkRemaining(TOTAL_WORK);
		for (int i = 0; i < TOTAL_WORK; i++) {
			bigTicksChild.worked(1);
		}
		bigTicksChild.done();
	}

	public void testInternalWorked() {
		double delta = 10.0d / TOTAL_WORK;

		for (int i = 0; i < TOTAL_WORK; i++) {
			smallTicksChild.internalWorked(delta);
		}
	}

	public void testSplit() {
		SubMonitor bigTicksChild = smallTicksChild.newChild(10).setWorkRemaining(TOTAL_WORK);
		for (int i = 0; i < TOTAL_WORK; i++) {
			bigTicksChild.split(1);
		}
		bigTicksChild.done();
	}

	@Override
	protected void tearDown() throws Exception {
		smallTicksChild.done();
		topmostMonitor.done();
		long endTime = System.currentTimeMillis();
		SubMonitorTest.reportPerformance(getClass().getName(), getName(), startTime, endTime);
		topmostMonitor.assertOptimal();
		super.tearDown();
	}

}
