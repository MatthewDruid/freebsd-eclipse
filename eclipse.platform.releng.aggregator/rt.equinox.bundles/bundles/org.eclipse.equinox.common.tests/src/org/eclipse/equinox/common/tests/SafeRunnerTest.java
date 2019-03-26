/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
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

import org.eclipse.core.runtime.*;
import org.eclipse.core.tests.harness.CoreTest;

/**
 * Tests for {@link SafeRunner}.
 */
public class SafeRunnerTest extends CoreTest {

	/**
	 * Ensures that cancelation exceptions are handled
	 */
	public void testCancel() {
		boolean caught = false;
		try {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable exception) {
				}

				@Override
				public void run() throws Exception {
					throw new OperationCanceledException();
				}
			});
		} catch (OperationCanceledException e) {
			caught = true;
		}
		assertFalse("1.0", caught);
	}

	/**
	 * Tests that SafeRunner catches the expected exception types.
	 */
	public void testCaughtExceptionTypes() {
		Throwable[] failures = new Throwable[] {new AssertionError(), new LinkageError(), new RuntimeException()};
		for (int i = 0; i < failures.length; i++) {
			final Throwable[] handled = new Throwable[1];
			final Throwable current = failures[i];
			try {
				SafeRunner.run(new ISafeRunnable() {
					@Override
					public void handleException(Throwable exception) {
						handled[0] = exception;
					}

					@Override
					public void run() throws Exception {
						if (current instanceof Exception) {
							throw (Exception) current;
						} else if (current instanceof Error) {
							throw (Error) current;
						}
					}
				});
			} catch (Throwable t) {
				fail("1." + i, t);
			}
			assertEquals("2." + i, current, handled[0]);
		}

	}

	/**
	 * Tests that SafeRunner re-throws expected exception types.
	 */
	public void testThrownExceptionTypes() {
		//thrown exceptions
		final Throwable[] thrown = new Throwable[] {new Error(), new OutOfMemoryError()};
		for (int i = 0; i < thrown.length; i++) {
			boolean caught = false;
			final Throwable current = thrown[i];
			try {
				SafeRunner.run(new ISafeRunnable() {
					@Override
					public void handleException(Throwable exception) {
					}

					@Override
					public void run() throws Exception {
						if (current instanceof Exception) {
							throw (Exception) current;
						} else if (current instanceof Error) {
							throw (Error) current;
						}
					}
				});
			} catch (Throwable t) {
				assertEquals("1." + i, current, t);
				caught = true;
			}
			assertTrue("2." + i, caught);
		}
	}

	public void testNull() {
		try {
			SafeRunner.run(null);
			fail("1.0");
		} catch (RuntimeException e) {
			//expected
		}
	}

	/**
	 * Ensures that exceptions are propagated when the safe runner re-throws it
	 */
	public void testRethrow() {
		boolean caught = false;
		try {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable exception) {
					if (exception instanceof IllegalArgumentException) {
						throw (IllegalArgumentException) exception;
					}
				}

				@Override
				public void run() throws Exception {
					throw new IllegalArgumentException();
				}
			});
		} catch (IllegalArgumentException e) {
			caught = true;
		}
		assertTrue("1.0", caught);

	}
}
