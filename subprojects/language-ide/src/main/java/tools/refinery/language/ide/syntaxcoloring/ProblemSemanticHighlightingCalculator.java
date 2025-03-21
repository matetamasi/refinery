/*
 * SPDX-FileCopyrightText: 2021-2024 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package tools.refinery.language.ide.syntaxcoloring;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.service.OperationCanceledManager;
import org.eclipse.xtext.util.CancelIndicator;
import org.jetbrains.annotations.NotNull;
import tools.refinery.language.documentation.TypeHashProvider;
import tools.refinery.language.model.problem.*;
import tools.refinery.language.utils.ProblemUtil;

import java.util.List;

public class ProblemSemanticHighlightingCalculator extends DefaultSemanticHighlightingCalculator {
	private static final String BUILTIN_CLASS = "builtin";
	private static final String ABSTRACT_CLASS = "abstract";
	private static final String DATATYPE_CLASS = "datatype";
	private static final String AGGREGATOR_CLASS = "aggregator";
	private static final String CONTAINMENT_CLASS = "containment";
	private static final String ERROR_CLASS = "error";
	private static final String NODE_CLASS = "node";
	private static final String ATOM_NODE_CLASS = "atom";
	private static final String NEW_NODE_CLASS = "new";
	private static final String RELATION_CLASS = "relation";

	@Inject
	private OperationCanceledManager operationCanceledManager;

	@Inject
	private TypeHashProvider typeHashProvider;

	@Override
	protected boolean highlightElement(EObject object, IHighlightedPositionAcceptor acceptor,
									   CancelIndicator cancelIndicator) {
		highlightName(object, acceptor);
		highlightCrossReferences(object, acceptor, cancelIndicator);
		return false;
	}

	protected void highlightName(EObject object, IHighlightedPositionAcceptor acceptor) {
		if (!(object instanceof NamedElement)) {
			return;
		}
		String[] highlightClass = getHighlightClass(object, null);
		if (highlightClass.length > 0) {
			highlightFeature(acceptor, object, ProblemPackage.Literals.NAMED_ELEMENT__NAME, highlightClass);
		}
	}

	protected void highlightCrossReferences(EObject object, IHighlightedPositionAcceptor acceptor,
											CancelIndicator cancelIndicator) {
		for (EReference reference : object.eClass().getEAllReferences()) {
			if (reference.isContainment()) {
				continue;
			}
			operationCanceledManager.checkCanceled(cancelIndicator);
			if (reference.isMany()) {
				highlightManyValues(object, reference, acceptor);
			} else {
				highlightSingleValue(object, reference, acceptor);
			}
		}
	}

	protected void highlightSingleValue(EObject owner, EReference reference, IHighlightedPositionAcceptor acceptor) {
		EObject valueObj = (EObject) owner.eGet(reference);
		String[] highlightClass = getHighlightClass(valueObj, reference);
		if (highlightClass.length > 0) {
			highlightFeature(acceptor, owner, reference, highlightClass);
		}
	}

	protected void highlightManyValues(EObject owner, EReference reference, IHighlightedPositionAcceptor acceptor) {
		@SuppressWarnings("unchecked")
		EList<? extends EObject> values = (EList<? extends EObject>) owner.eGet(reference);
		List<INode> nodes = NodeModelUtils.findNodesForFeature(owner, reference);
		int size = Math.min(values.size(), nodes.size());
		for (var i = 0; i < size; i++) {
			EObject valueInList = values.get(i);
			INode node = nodes.get(i);
			String[] highlightClass = getHighlightClass(valueInList, reference);
			if (highlightClass.length > 0) {
				highlightNode(acceptor, node, highlightClass);
			}
		}
	}

	protected String[] getHighlightClass(EObject eObject, EReference reference) {
		// References to error patterns should be highlighted as errors, but error pattern definitions and
		// references to the computed values of error patterns shouldn't.
		boolean isError = ProblemUtil.isError(eObject) && reference != null;
		if (ProblemUtil.isBuiltIn(eObject) && !(eObject instanceof Problem)) {
			var className = isError ? ERROR_CLASS : BUILTIN_CLASS;
			return new String[]{className};
		}
		return getUserDefinedElementHighlightClass(eObject, reference, isError);
	}

	@NotNull
	private String[] getUserDefinedElementHighlightClass(EObject eObject, EReference reference, boolean isError) {
		ImmutableList.Builder<String> classesBuilder = ImmutableList.builder();
		if (eObject instanceof ClassDeclaration classDeclaration && classDeclaration.isAbstract()) {
			classesBuilder.add(ABSTRACT_CLASS);
		}
		if (eObject instanceof DatatypeDeclaration) {
			classesBuilder.add(DATATYPE_CLASS);
		}
		if (eObject instanceof AggregatorDeclaration) {
			classesBuilder.add(AGGREGATOR_CLASS);
		}
		if (eObject instanceof ReferenceDeclaration referenceDeclaration
				&& ProblemUtil.isContainmentReference(referenceDeclaration)) {
			classesBuilder.add(CONTAINMENT_CLASS);
		}
		if (isError) {
			classesBuilder.add(ERROR_CLASS);
		}
		if (eObject instanceof Node node) {
			highlightNode(node, classesBuilder);
		}
		if (eObject instanceof Relation relation) {
			highlightRelation(reference, relation, classesBuilder);
		}
		List<String> classes = classesBuilder.build();
		return classes.toArray(new String[0]);
	}

	private static void highlightNode(Node node, ImmutableList.Builder<String> classesBuilder) {
		classesBuilder.add(NODE_CLASS);
		if (ProblemUtil.isAtomNode(node)) {
			classesBuilder.add(ATOM_NODE_CLASS);
		}
		if (ProblemUtil.isMultiNode(node)) {
			classesBuilder.add(NEW_NODE_CLASS);
		}
	}

	private void highlightRelation(EReference reference, Relation relation,
								   ImmutableList.Builder<String> classesBuilder) {
		if (ProblemPackage.Literals.VARIABLE_OR_NODE_EXPR__ELEMENT.equals(reference)) {
			// References to relations in annotation arguments should be highlighted as relations, not variables.
			classesBuilder.add(RELATION_CLASS);
		}
		var typeHash = typeHashProvider.getTypeHash(relation);
		if (typeHash != null) {
			classesBuilder.add("typeHash-" + typeHash);
		}
	}
}
