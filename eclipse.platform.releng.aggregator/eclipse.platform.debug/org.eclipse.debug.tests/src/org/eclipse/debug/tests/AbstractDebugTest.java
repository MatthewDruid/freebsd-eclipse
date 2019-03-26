/*******************************************************************************
 *  Copyright (c) 2017 Andrey Loskutov and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     Andrey Loskutov <loskutov@gmx.de> - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.tests;

import java.util.function.Function;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.progress.UIJob;

import junit.framework.TestCase;

public class AbstractDebugTest extends TestCase {

	private static boolean welcomeClosed;

	/**
	 * Default timeout in milliseconds to wait on some events
	 */
	protected long testTimeout = 30000;

	public AbstractDebugTest() {
		super();
	}

	public AbstractDebugTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.log(IStatus.INFO, getName(), "setUp");
		assertWelcomeScreenClosed();
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtil.log(IStatus.INFO, getName(), "tearDown");
		TestUtil.cleanUp(getName());
		super.tearDown();
	}

	/**
	 * Ensure the welcome screen is closed because in 4.x the debug perspective
	 * opens a giant fast-view causing issues
	 *
	 * @throws Exception
	 */
	protected final void assertWelcomeScreenClosed() throws Exception {
		if (!welcomeClosed && PlatformUI.isWorkbenchRunning()) {
			final IWorkbench wb = PlatformUI.getWorkbench();
			if (wb == null) {
				return;
			}
			// In UI thread we don't need to run a job
			if (Display.getCurrent() != null) {
				closeIntro(wb);
				return;
			}

			UIJob job = new UIJob("close welcome screen for debug test suite") {
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					closeIntro(wb);
					return Status.OK_STATUS;
				}

			};
			job.setPriority(Job.INTERACTIVE);
			job.setSystem(true);
			job.schedule();
		}
	}

	/**
	 * Waits while given condition is {@code true} for a given amount of
	 * milliseconds. If the actual wait time exceeds given timeout and condition
	 * will be still {@code true}, throws {@link AssertionFailedError} with
	 * given message.
	 * <p>
	 * Will process UI events while waiting in UI thread, if called from
	 * background thread, just waits.
	 *
	 * @param condition function which will be evaluated while waiting
	 * @param timeout max wait time in milliseconds to wait on given condition
	 * @param errorMessage message which will be used to construct the failure
	 *            exception in case the condition will still return {@code true}
	 *            after given timeout
	 */
	public void waitWhile(Function<AbstractDebugTest, Boolean> condition, long timeout, Function<AbstractDebugTest, String> errorMessage) throws Exception {
		TestUtil.waitWhile(condition, this, timeout, errorMessage);
	}

	/**
	 * Waits while given condition is {@code true} for some time. If the actual
	 * wait time exceeds {@link #testTimeout} and condition will be still
	 * {@code true}, throws {@link AssertionFailedError} with given message.
	 * <p>
	 * Will process UI events while waiting in UI thread, if called from
	 * background thread, just waits.
	 *
	 * @param condition function which will be evaluated while waiting
	 * @param errorMessage message which will be used to construct the failure
	 *            exception in case the condition will still return {@code true}
	 *            after given timeout
	 */
	public void waitWhile(Function<AbstractDebugTest, Boolean> condition, Function<AbstractDebugTest, String> errorMessage) throws Exception {
		TestUtil.waitWhile(condition, this, testTimeout, errorMessage);
	}

	private static void closeIntro(final IWorkbench wb) {
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		if (window != null) {
			IIntroManager im = wb.getIntroManager();
			IIntroPart intro = im.getIntro();
			if (intro != null) {
				welcomeClosed = im.closeIntro(intro);
			}
		}
	}
}
