/*******************************************************************************
 * Copyright (c) 2004 Eric Merritt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Merritt
 *     Vlad Dumitrescu
 *******************************************************************************/
package org.erlide.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.erlide.core.erlang.ErlangCore;
import org.erlide.core.util.ErlideUtil;
import org.erlide.runtime.ErlLogger;
import org.erlide.runtime.backend.ICodeBundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * 
 * @author Eric Merritt [cyberlync at gmail dot com]
 * @author Vlad Dumitrescu [vladdu55 at gmail dot com]
 * @author jakob
 */

public class ErlangPlugin extends Plugin implements ICodeBundle {

	/**
	 * The plugin id
	 */
	public static final String PLUGIN_ID = "org.erlide.core";

	/**
	 * The builder id
	 */
	public static final String BUILDER_ID = PLUGIN_ID + ".erlbuilder";

	/**
	 * the nature id
	 */
	public static final String NATURE_ID = PLUGIN_ID + ".erlnature";

	private static final boolean TOUCH_ALL_ERLANG_PROJECTS_ON_LAUNCH = false;

	/**
	 * The shared instance.
	 */
	private static ErlangPlugin plugin;

	/**
	 * Resource bundle.
	 */
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public ErlangPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("org.erlide.core.ErlangPluginResources");
		} catch (final MissingResourceException x) {
			x.printStackTrace();
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return The plugin
	 */
	public static ErlangPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 * 
	 * @param key
	 *            The resource
	 * @return The identified string
	 */
	public static String getResourceString(final String key) {
		final ResourceBundle bundle = ErlangPlugin.getDefault()
				.getResourceBundle();
		try {
			return bundle != null ? bundle.getString(key) : key;
		} catch (final MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * 
	 * @return The requested bundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/*
	 * (non-Edoc) Shutdown the ErlangCore plug-in. <p> De-registers the
	 * ErlModelManager as a resource changed listener and save participant. <p>
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		ErlangCore.getBackendManager().removePlugin(this);

		try {
			try {
				// savePluginPreferences();
				// final IWorkspace workspace = ResourcesPlugin.getWorkspace();
				// workspace.removeResourceChangeListener(ErlModelManager.
				// getDefault().deltaState);
				// workspace.removeSaveParticipant(this);

				ErlangCore.getModelManager().shutdown();
			} finally {
				ErlangCore.getBackendManager().removePlugin(this);

				// ensure we call super.stop as the last thing
				super.stop(context);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		super.stop(context);
		plugin = null;
	}

	/*
	 * (non-Edoc) Startup the ErlangCore plug-in. <p> Registers the
	 * ErlModelManager as a resource changed listener and save participant.
	 * Starts the background indexing, and restore saved classpath variable
	 * values. <p> @throws Exception
	 * 
	 * @see org.eclipse.core.runtime.Plugin#start(BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		ErlLogger.init();
		ErlLogger.debug("Starting CORE " + Thread.currentThread());
		super.start(context);

		String dev = "";
		if (ErlideUtil.isDeveloper()) {
			dev = " erlide developer version ***";
		}
		if (ErlideUtil.isTest()) {
			dev += " test ***";
		}
		Object version = getBundle().getHeaders().get("Bundle-Version");
		ErlLogger.info("*** starting Erlide v" + version + " ***" + dev);

		ErlangCore.initializeRuntime();
		ErlangCore.getBackendManager().register(this);
		ErlangCore.registerOpenProjects();

		ErlLogger.debug("Started CORE");
	}

	public static void log(final IStatus status) {
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}

	public static void logErrorMessage(final String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID,
				ErlangStatusConstants.INTERNAL_ERROR, message, null));
	}

	public static void logErrorStatus(final String message, final IStatus status) {
		if (status == null) {
			logErrorMessage(message);
			return;
		}
		final MultiStatus multi = new MultiStatus(PLUGIN_ID,
				ErlangStatusConstants.INTERNAL_ERROR, message, null);
		multi.add(status);
		log(multi);
	}

	public static void log(final Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID,
				ErlangStatusConstants.INTERNAL_ERROR, "Erlide internal error",
				e));
	}

	public static void initializeAfterLoad(final IProgressMonitor monitor) {
		final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

			public void run(IProgressMonitor progressMonitor)
					throws CoreException {
				IProject[] projects = null;
				// projects = model.getJavaProjects();
				IWorkspace root = ResourcesPlugin.getWorkspace();
				projects = root.getRoot().getProjects();

				if (projects != null) {
					for (IProject project : projects) {
						try {
							if (project.hasNature(ErlangPlugin.NATURE_ID)) {
								project.touch(progressMonitor);
							}
						} catch (CoreException e) {
							// could not touch this project: ignore
						}
					}
				}
			}
		};
		try {
			if (TOUCH_ALL_ERLANG_PROJECTS_ON_LAUNCH) {
				final long millis0 = System.currentTimeMillis();
				ResourcesPlugin.getWorkspace().run(runnable, monitor);
				final long millis = System.currentTimeMillis() - millis0;
				ErlLogger.debug("Time for touch : " + millis);
			}
		} catch (final CoreException e) {
			// could not touch all projects
		}

	}

	public static void log(final String msg, final Throwable thr) {
		final String id = PLUGIN_ID;
		final Status status = new Status(IStatus.ERROR, id, IStatus.OK, msg,
				thr);
		getDefault().getLog().log(status);
	}

	public static void debug(final String message) {
		if (getDefault().isDebugging()) {
			ErlLogger.debug(message);
		}
	}

	public void start() {
	}

	public String getEbinDir() {
		return ErlideUtil.getEbinDir(getBundle());
	}

}
