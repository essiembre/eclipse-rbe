/*******************************************************************************
 * Copyright (c) 2017 Wolfgang Schramm and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    The original code was implemented in org.eclipse.babel.editor and 
 *    transfered here to implement a resource filter.
 ******************************************************************************/
package com.essiembre.eclipse.rbe.ui.editor.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.internal.misc.StringMatcher;

import com.essiembre.eclipse.rbe.RBEPlugin;
import com.essiembre.eclipse.rbe.model.workbench.RBEPreferences;

/**
 * Some parts of this implementation are from org.eclipse.babel.editor.* files.
 */
@SuppressWarnings("restriction")
public class ResourceFilter implements IPropertyChangeListener {

	private static IPreferenceStore	prefs			= RBEPlugin.getDefault().getPreferenceStore();

	/**
	 * The root locale used for the original properties file. This constant is defined in
	 * java.util.Local starting with jdk6.
	 */
	private static final Locale		ROOT_LOCALE		= new Locale("");										//$NON-NLS-1$

	private static Pattern			COUNTRY_MATCHER	= Pattern.compile("_([a-z]{2,3})_([A-Z]{2})");			//$NON-NLS-1$
	private static Pattern			VARIANT_MATCHER	= Pattern.compile("_([a-z]{2,3})_([A-Z]{2})_(\\w*)");	//$NON-NLS-1$

	private static StringMatcher[]	cachedCompiledLocaleFilter;

	private static ResourceFilter	INSTANCE		= new ResourceFilter();

	public static IPropertyChangeListener getInstance() {
		return INSTANCE;
	}

	/**
	 * Called when the locales filter value is changed.
	 * <p>
	 * Takes care of reloading the opened editors and calling the full-build of the rbeBuilder on
	 * all project that use it.
	 * </p>
	 */
	private static void onLocalFilterChange() {

		cachedCompiledLocaleFilter = null;

// IN THE 3.x VERSION THIS CAUSED WIDGET DISPOSE EXCEPTIONS WHEN THE EDITOR WAS OPEN		

//		//first: refresh the editors.
//		//look at the opened editors and reload them if possible
//		//otherwise, save them, close them and re-open them.
//		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
//		for (int i = 0; i < pages.length; i++) {
//			IEditorReference[] edRefs = pages[i].getEditorReferences();
//			for (int j = 0; j < edRefs.length; j++) {
//				IEditorReference ref = edRefs[j];
//				IEditorPart edPart = ref.getEditor(false);
//				if (edPart != null && edPart instanceof MessagesEditor) {
//					//the editor was loaded. reload it:
//					MessagesEditor meToReload = (MessagesEditor) edPart;
//					meToReload.reloadDisplayedContents();
//				}
//			}
//		}
//
//		//second: clean and build all the projects that have the rbe builder.
//		//Calls the builder for a clean and build on all projects of the workspace.
//		try {
//			IProject[] projs = ResourcesPlugin.getWorkspace().getRoot().getProjects();
//			for (int i = 0; i < projs.length; i++) {
//				if (projs[i].isAccessible()) {
//					ICommand[] builders = projs[i].getDescription().getBuildSpec();
//					for (int j = 0; j < builders.length; j++) {
//						if (Builder.BUILDER_ID.equals(builders[j].getBuilderName())) {
//							projs[i].build(
//									IncrementalProjectBuilder.FULL_BUILD,
//									Builder.BUILDER_ID,
//									null,
//									new NullProgressMonitor());
//							break;
//						}
//					}
//				}
//			}
//		} catch (CoreException ce) {
//			IStatus status = new Status(IStatus.ERROR, MessagesEditorPlugin.PLUGIN_ID, IStatus.OK, ce.getMessage(), ce);
//			MessagesEditorPlugin.getDefault().getLog().log(status);
//		}
	}

	/**
	 * Reads the filter of locales in the preferences and apply it to filter the passed locales.
	 * 
	 * @param locales
	 * @return The new collection of locales; removed the ones not selected by the preferences.
	 */
	private Locale[] filterLocales(Locale[] locales) {

		StringMatcher[] patterns = getFilterLocalesStringMatchers();
		Set<Locale> already = new HashSet<Locale>();

		//first look for the root locale:
		ArrayList<Locale> result = new ArrayList<Locale>();
		for (int j = 0; j < locales.length; j++) {
			Locale loc = locales[j];
			if (ROOT_LOCALE.equals(loc) || loc == null) {
				already.add(loc);
				result.add(loc);
				break;
			}
		}

		//now go through each pattern until already indexed locales found all locales
		//or we run out of locales.
		for (int pi = 0; pi < patterns.length; pi++) {
			StringMatcher pattern = patterns[pi];
			for (int j = 0; j < locales.length; j++) {
				Locale loc = locales[j];
				if (!already.contains(loc)) {
					if (pattern.match(loc.toString())) {
						already.add(loc);
						result.add(loc);
						if (already.size() == locales.length) {
							for (int k = 0; k < locales.length; k++) {
								locales[k] = (Locale) result.get(k);
							}
							return locales;
						}
					}
				}
			}
		}

		Locale[] filtered = new Locale[result.size()];
		for (int k = 0; k < filtered.length; k++) {
			filtered[k] = result.get(k);
		}

		return filtered;
	}

	/**
	 * @return The StringMatchers compiled from #getFilterLocalesStringMatcher()
	 */
	private static synchronized StringMatcher[] getFilterLocalesStringMatchers() {

		if (cachedCompiledLocaleFilter != null) {
			return cachedCompiledLocaleFilter;
		}

		String pref = prefs.getString(RBEPreferences.FILTER_LOCALES_STRING_MATCHERS);
		StringTokenizer tokenizer = new StringTokenizer(pref, ";, ", false);

		cachedCompiledLocaleFilter = new StringMatcher[tokenizer.countTokens()];
		int ii = 0;

		while (tokenizer.hasMoreTokens()) {
			StringMatcher pattern = new StringMatcher(tokenizer.nextToken().trim(), true, false);
			cachedCompiledLocaleFilter[ii] = pattern;
			ii++;
		}

		return cachedCompiledLocaleFilter;
	}

	/**
	 * @param resourceName
	 * @param regex
	 *            Regex example:
	 * 
	 *            <pre>
	 *            ^(messages)((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})|(_[a-z]{2,3}_[A-Z]{2}_\w*))?(\.properties)$
	 *            </pre>
	 * 
	 * @return Returns <code>true</code> when the resource passes the resource filter.
	 */
	public boolean isResourceDisplayed(String resourceName, String regex) {

		Matcher resourceFilenameMatcher = Pattern.compile(regex).matcher(resourceName);

		// start matcher, a check is not necessary because the calling method is doing it -> could be optimized
		resourceFilenameMatcher.matches();

		String localeLanguage = resourceFilenameMatcher.group(3);
		String localeWithCountry = resourceFilenameMatcher.group(4);
		String localeWithVariant = resourceFilenameMatcher.group(5);

		Locale resourceLocale = null;

		if (localeLanguage == null && localeWithCountry == null && localeWithVariant == null) {

			// this is the root locale

			resourceLocale = ROOT_LOCALE;

		} else if (localeLanguage != null) {

			// locale only with a language 

			// remove leading _
			String language = localeLanguage.substring(1);

			resourceLocale = new Locale(language);

		} else if (localeWithCountry != null) {

			// locale with language AND country

			Matcher countryMatcher = COUNTRY_MATCHER.matcher(localeWithCountry);
			countryMatcher.matches();

			String language = countryMatcher.group(1);
			String country = countryMatcher.group(2);

			resourceLocale = new Locale(language, country);

		} else if (localeWithVariant != null) {

			// locale with language AND country AND variant 

			Matcher countryMatcher = VARIANT_MATCHER.matcher(localeWithCountry);
			countryMatcher.matches();

			String language = countryMatcher.group(1);
			String country = countryMatcher.group(2);
			String variant = countryMatcher.group(3);

			resourceLocale = new Locale(language, country, variant);
		}

		if (resourceLocale == null) {
			return false;
		}

		Locale[] filteredLocales = filterLocales(new Locale[] { resourceLocale });
		boolean isDisplayed = filteredLocales.length > 0;

//		System.out.println(x);

		return isDisplayed;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (RBEPreferences.FILTER_LOCALES_STRING_MATCHERS.equals(event.getProperty())) {
			onLocalFilterChange();
		}
	}
}
