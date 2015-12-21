/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.internal;

import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONStringer;
import org.apache.wicket.ajax.json.JsonSequenceStringer;

/**
 * This class is NOT part of the public API. Do not use outside this library!
 */
public class JsUtil {

	/**
	 * Returns a JS string literal for the specified value.
	 *
	 * @param s the string value
	 * @return the string literal
	 */
	public static String toStringLiteral(final String s) {
		try {
			final JSONStringer stringer = new JSONStringer();
			stringer.value(s);
			return stringer.toString();
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a JS string literal for the specified value.
	 *
	 * @param s the string value
	 * @return the string literal
	 */
	public static CharSequence toStringLiteralCharSequence(final String s) {
		try {
			final JsonSequenceStringer stringer = new JsonSequenceStringer();
			stringer.value(s);
			return stringer.toCharSequence();
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}
	}

}
