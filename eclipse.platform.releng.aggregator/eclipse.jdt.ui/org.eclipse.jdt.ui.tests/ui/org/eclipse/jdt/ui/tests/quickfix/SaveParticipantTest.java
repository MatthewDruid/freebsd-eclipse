/*******************************************************************************
 * Copyright (c) 2007, 2020 IBM Corporation and others.
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
package org.eclipse.jdt.ui.tests.quickfix;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.ui.PartInitException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.CleanUpPostSaveListener;
import org.eclipse.jdt.internal.corext.fix.CleanUpPreferenceUtil;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.tests.core.rules.ProjectTestSetup;

import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

@RunWith(JUnit4.class)
public class SaveParticipantTest extends CleanUpTestCase {

	@Rule
    public ProjectTestSetup projectsetup = new ProjectTestSetup();

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		IEclipsePreferences node= InstanceScope.INSTANCE.getNode(JavaUI.ID_PLUGIN);
		node.putBoolean("editor_save_participant_" + CleanUpPostSaveListener.POSTSAVELISTENER_ID, true);
		node.put(CleanUpPreferenceUtil.SAVE_PARTICIPANT_KEY_PREFIX + CleanUpConstants.CLEANUP_ON_SAVE_ADDITIONAL_OPTIONS, CleanUpOptions.TRUE);
	}

	private static void editCUInEditor(ICompilationUnit cu, String newContent) throws JavaModelException, PartInitException {
		JavaEditor editor= (JavaEditor) EditorUtility.openInEditor(cu);

		cu.getBuffer().setContents(newContent);
		editor.doSave(null);
	}

	@Test
	public void testFormatAll01() throws Exception {
		IPackageFragment pack1= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        String s= (String)o;\n");
		buf.append("    }\n");
		buf.append("}");
		ICompilationUnit cu1= pack1.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        String s    = (String)o;\n");
		buf.append("    }\n");
		buf.append("}");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo(Object o) {\n");
		buf.append("        String s = (String) o;\n");
		buf.append("    }\n");
		buf.append("}");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChanges01() throws Exception {
		IPackageFragment pack1= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        String s= (String)o;\n");
		buf.append("    }\n");
		buf.append("}");
		ICompilationUnit cu1= pack1.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        String s    = (String)o;\n");
		buf.append("    }\n");
		buf.append("}");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        String s = (String) o;\n");
		buf.append("    }\n");
		buf.append("}");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChanges02() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        Object s= (String)o;\n");
		buf.append("}}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);
		enable(CleanUpConstants.REMOVE_UNNECESSARY_CASTS);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        Object s       = (String)o;\n");
		buf.append("}}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public void foo( Object o ) {\n");
		buf.append("        Object s = o;\n");
		buf.append("}}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChangesBug205177() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    int        a= 1;\n");
		buf.append("    int        b= 2;\n");
		buf.append("    int        c= 3;\n");
		buf.append("    int        d= 4;\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    int        a= 1;\n");
		buf.append("    int        b= 2;//\n");
		buf.append("    int        c= 3;\n");
		buf.append("    int        d= 4;\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    int        a= 1;\n");
		buf.append("    int b = 2;//\n");
		buf.append("    int        c= 3;\n");
		buf.append("    int        d= 4;\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChangesBug205308() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    int        a= 1;\n");
		buf.append("    int        b= 2;\n");
		buf.append("    int        c= 3;\n");
		buf.append("    int        d= 4;\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    int         a= 1;\n");
		buf.append("    int        b= 2;\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    int a = 1;\n");
		buf.append("    int        b= 2;\n");
		buf.append("}\n");

		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChangesBug205301() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("\n");
		buf.append("public class E1 {\n");
		buf.append("    /**\n");
		buf.append("     * adsfdas\n");
		buf.append("     * dafs\n");
		buf.append("     */\n");
		buf.append("    int a = 2;\n");
		buf.append("\n");
		buf.append("    /**\n");
		buf.append("     * adsfasd \n");
		buf.append("     * asd\n");
		buf.append("     */\n");
		buf.append("    int b = 2;\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("\n");
		buf.append("public class E1 {\n");
		buf.append("    /**\n");
		buf.append("     * adsfdas\n");
		buf.append("     * dafs \n");
		buf.append("     */\n");
		buf.append("    int a = 2;\n");
		buf.append("\n");
		buf.append("    /**\n");
		buf.append("     * adsfasd \n");
		buf.append("     * asd\n");
		buf.append("     */\n");
		buf.append("    int b = 2;\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("\n");
		buf.append("public class E1 {\n");
		buf.append("    /**\n");
		buf.append("     * adsfdas dafs\n");
		buf.append("     */\n");
		buf.append("    int a = 2;\n");
		buf.append("\n");
		buf.append("    /**\n");
		buf.append("     * adsfasd \n");
		buf.append("     * asd\n");
		buf.append("     */\n");
		buf.append("    int b = 2;\n");
		buf.append("}\n");

		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChangesBug207965() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("       protected String foo(String string) {  \n");
		buf.append("          int i = 10;\n");
		buf.append("          return (\"\" + string + \"\") + \"\";  \n");
		buf.append("    }\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);
		enable(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES);
		enable(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_ALL);
		enable(CleanUpConstants.FORMAT_CORRECT_INDENTATION);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("       protected String foo(String string) {  \n");
		buf.append("          int i = 10;\n");
		buf.append("          return  (\"\" + string + \"\") + \"\";  \n");
		buf.append("    }\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    protected String foo(String string) {\n");
		buf.append("        int i = 10;\n");
		buf.append("        return (\"\" + string + \"\") + \"\";\n");
		buf.append("    }\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChangesBug207965_2() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public int i = 10;\n");
		buf.append("    \n");
		buf.append("    public int j = 10;\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);
		enable(CleanUpConstants.FORMAT_CORRECT_INDENTATION);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public int i= 10;\n");
		buf.append("    \n");
		buf.append("    public int j= 10;\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public int i = 10;\n");
		buf.append("\n");
		buf.append("    public int j = 10;\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testFormatChangesBug208568() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public int i = 10;    \n");
		buf.append("\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public  int i= 10;    \n");
		buf.append("\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("    public int i = 10;\n");
		buf.append("\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug213248_1() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_CORRECT_INDENTATION);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append(" \n");
		buf.append("    public int field;\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug213248_2() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES);
		enable(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_ALL);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append(" \n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug213248_3() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);
		enable(CleanUpConstants.FORMAT_CORRECT_INDENTATION);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append(" \n");
		buf.append("    public int field;\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug213248_4() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);
		enable(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES);
		enable(CleanUpConstants.FORMAT_REMOVE_TRAILING_WHITESPACES_ALL);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append(" \n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    public int field;\n");
		buf.append("\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug228659() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package a;\n");
		buf.append("public class Test {\n");
		buf.append("    /**\n");
		buf.append("     */\n");
		buf.append("    public void foo() {\n");
		buf.append("        String s1 = \"\";\n");
		buf.append("    }\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package a;\n");
		buf.append("public class Test {\n");
		buf.append("    /**\n");
		buf.append("     */\n");
		buf.append("    public void foo() {\n");
		buf.append("        String s1  = \"\";\n");
		buf.append("    }\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package a;\n");
		buf.append("public class Test {\n");
		buf.append("    /**\n");
		buf.append("     */\n");
		buf.append("    public void foo() {\n");
		buf.append("        String s1 = \"\";\n");
		buf.append("    }\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug232768_1() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    /**\n");
		buf.append("     * A Java comment on\n");
		buf.append("     * two lines\n");
		buf.append("     */\n");
		buf.append("\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    /**\n");
		buf.append("     * A Java comment on\n");
		buf.append("     *  two lines\n");
		buf.append("     */\n");
		buf.append("\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    /**\n");
		buf.append("     * A Java comment on two lines\n");
		buf.append("     */\n");
		buf.append("\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug232768_2() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    /*\n");
		buf.append("     * A block comment on\n");
		buf.append("     * two lines\n");
		buf.append("     */\n");
		buf.append("\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    /*\n");
		buf.append("     * A block comment on\n");
		buf.append("     *  two lines\n");
		buf.append("     */\n");
		buf.append("\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    /*\n");
		buf.append("     * A block comment on two lines\n");
		buf.append("     */\n");
		buf.append("\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}

	@Test
	public void testBug232768_3() throws Exception {
		IPackageFragment pack2= fSourceFolder.createPackageFragment("test1", false, null);
		StringBuffer buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    //long long long long long long long long long long long long long long long long\n");
		buf.append("\n");
		buf.append("}\n");
		ICompilationUnit cu1= pack2.createCompilationUnit("E1.java", buf.toString(), false, null);

		enable(CleanUpConstants.FORMAT_SOURCE_CODE);
		enable(CleanUpConstants.FORMAT_SOURCE_CODE_CHANGES_ONLY);

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    // long long long long long long long long long long long long long long long long\n");
		buf.append("\n");
		buf.append("}\n");

		editCUInEditor(cu1, buf.toString());

		buf= new StringBuffer();
		buf.append("package test1;\n");
		buf.append("public class E1 {\n");
		buf.append("\n");
		buf.append("    // long long long long long long long long long long long long long long\n");
		buf.append("    // long long\n");
		buf.append("\n");
		buf.append("}\n");
		String expected1= buf.toString();

		assertEquals(expected1, cu1.getBuffer().getContents());
	}
}
