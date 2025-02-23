/*
 * SPDX-FileCopyrightText: 2024 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package tools.refinery.language.utils;

import com.google.inject.Inject;
import tools.refinery.language.annotations.AnnotationContext;
import tools.refinery.language.annotations.BuiltinAnnotations;
import tools.refinery.language.model.problem.Parameter;
import tools.refinery.language.model.problem.Relation;

public class BuiltinAnnotationContext {
	@Inject
	private AnnotationContext annotationContext;

	public ParameterBinding getParameterBinding(Parameter parameter) {
		var annotations = annotationContext.annotationsFor(parameter);
		if (annotations.hasAnnotation(BuiltinAnnotations.FOCUS)) {
			return ParameterBinding.FOCUS;
		}
		if (annotations.hasAnnotation(BuiltinAnnotations.LONE)) {
			return ParameterBinding.LONE;
		}
		if (annotations.hasAnnotation(BuiltinAnnotations.MULTI)) {
			return ParameterBinding.MULTI;
		}
		return ParameterBinding.SINGLE;
	}

	public ConcretizationSettings getConcretizationSettings(Relation relation) {
		var annotations = annotationContext.annotationsFor(relation);
		var concretize = annotations.getAnnotation(BuiltinAnnotations.CONCRETIZE)
				.flatMap(annotation -> annotation.getBoolean(BuiltinAnnotations.CONCRETIZE_AUTO));
		var decide = annotations.getAnnotation(BuiltinAnnotations.DECIDE)
				.flatMap(annotation -> annotation.getBoolean(BuiltinAnnotations.DECIDE_AUTO));
		return new ConcretizationSettings(concretize.orElseGet(() -> ProblemUtil.isConcretizeByDefault(relation)),
				decide.orElseGet(() -> {
					if (concretize.isPresent() && Boolean.FALSE.equals(concretize.get())) {
						return false;
					}
					return ProblemUtil.isDecideByDefault(relation);
				}));
	}
}
