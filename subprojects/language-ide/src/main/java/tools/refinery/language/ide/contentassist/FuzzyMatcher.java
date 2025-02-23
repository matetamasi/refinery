/*
 * SPDX-FileCopyrightText: 2021-2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package tools.refinery.language.ide.contentassist;

import org.eclipse.xtext.ide.editor.contentassist.IPrefixMatcher;

import com.google.inject.Singleton;

import java.util.Locale;

/**
 * Implements the candidate matching algorithm used by CodeMirror 6.
 * <p>
 * Using this class ensures that the same candidates will be returned when
 * filtering content assist proposals on the server as on the client.
 * </p>
 * <p>
 * The matching is "fuzzy" (<code>fzf</code>-like), i.e., the prefix characters
 * may occur anywhere in the name, but must be in the same order as in the
 * prefix.
 * </p>
 *
 * @author Kristóf Marussy
 */
@Singleton
public class FuzzyMatcher implements IPrefixMatcher {
	@Override
	public boolean isCandidateMatchingPrefix(String name, String prefix) {
		var nameIgnoreCase = name.toLowerCase(Locale.ROOT);
		var prefixIgnoreCase = prefix.toLowerCase(Locale.ROOT);
		int prefixLength = prefixIgnoreCase.length();
		if (prefixLength == 0) {
			return true;
		}
		int nameLength = nameIgnoreCase.length();
		if (prefixLength > nameLength) {
			return false;
		}
		int prefixIndex = 0;
		for (int nameIndex = 0; nameIndex < nameLength; nameIndex++) {
			if (nameIgnoreCase.charAt(nameIndex) == prefixIgnoreCase.charAt(prefixIndex)) {
				prefixIndex++;
				if (prefixIndex == prefixLength) {
					return true;
				}
			}
		}
		return false;
	}
}
