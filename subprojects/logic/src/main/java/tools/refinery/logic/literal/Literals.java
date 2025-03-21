/*
 * SPDX-FileCopyrightText: 2021-2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package tools.refinery.logic.literal;

import tools.refinery.logic.term.Term;

public final class Literals {
	private Literals() {
		throw new IllegalStateException("This is a static utility class and should not be instantiated directly");
	}

	// Negates the given literal.
	public static <T extends CanNegate<T>> T not(CanNegate<T> literal) {
		return literal.negate();
	}

	public static CheckLiteral check(Term<Boolean> term) {
		return new CheckLiteral(term);
	}
}
