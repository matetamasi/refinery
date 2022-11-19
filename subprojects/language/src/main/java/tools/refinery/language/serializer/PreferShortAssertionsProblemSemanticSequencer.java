package tools.refinery.language.serializer;

import com.google.inject.Inject;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ListTransient;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;
import tools.refinery.language.model.problem.Assertion;
import tools.refinery.language.model.problem.LogicAssertionValue;
import tools.refinery.language.model.problem.LogicValue;
import tools.refinery.language.model.problem.ProblemPackage;
import tools.refinery.language.services.ProblemGrammarAccess;

public class PreferShortAssertionsProblemSemanticSequencer extends ProblemSemanticSequencer {
	@Inject
	private ProblemGrammarAccess grammarAccess;

	@Override
	protected void sequence_Assertion(ISerializationContext context, Assertion semanticObject) {
		if (semanticObject.isDefault() ||
				!(semanticObject.getValue() instanceof LogicAssertionValue logicAssertionValue) ||
				logicAssertionValue.getLogicValue() == LogicValue.ERROR) {
			super.sequence_Assertion(context, semanticObject);
			return;
		}
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, ProblemPackage.Literals.ASSERTION__RELATION) == ValueTransient.YES) {
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject,
						ProblemPackage.Literals.ASSERTION__RELATION));
			}
			if (transientValues.isListTransient(semanticObject, ProblemPackage.Literals.ASSERTION__ARGUMENTS) == ListTransient.YES) {
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject,
						ProblemPackage.Literals.ASSERTION__ARGUMENTS));
			}
		}
		var feeder = createSequencerFeeder(context, semanticObject);
		var access = grammarAccess.getAssertionAccess();
		feeder.accept(access.getValueShortLogicAssertionValueParserRuleCall_1_1_0_0(), logicAssertionValue);
		feeder.accept(access.getRelationRelationQualifiedNameParserRuleCall_1_1_1_0_1(), semanticObject.getRelation());
		var iterator = semanticObject.getArguments().iterator();
		if (iterator.hasNext()) {
			var firstArgument = iterator.next();
			feeder.accept(access.getArgumentsAssertionArgumentParserRuleCall_1_1_3_0_0(), firstArgument, 0);
			int index = 1;
			while (iterator.hasNext()) {
				var argument = iterator.next();
				feeder.accept(access.getArgumentsAssertionArgumentParserRuleCall_1_1_3_1_1_0(), argument, index);
				index++;
			}
		}
		feeder.finish();
	}
}
