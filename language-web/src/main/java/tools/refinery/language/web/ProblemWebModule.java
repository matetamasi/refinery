/*
 * generated by Xtext 2.25.0
 */
package tools.refinery.language.web;

import org.eclipse.xtext.web.server.XtextServiceDispatcher;

import tools.refinery.language.web.xtext.server.NoPrecomputedServicesXtextServiceDispatcher;

/**
 * Use this class to register additional components to be used within the web application.
 */
public class ProblemWebModule extends AbstractProblemWebModule {
	public Class<? extends XtextServiceDispatcher> bindXtextServiceDispatcher() {
		return NoPrecomputedServicesXtextServiceDispatcher.class;
	}
}
