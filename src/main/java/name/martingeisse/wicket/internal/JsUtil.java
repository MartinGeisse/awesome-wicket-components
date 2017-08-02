/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONStringer;
import org.apache.wicket.ajax.json.JsonSequenceStringer;

/**
 * This class is NOT part of the public API. Do not use outside this library!
 */
public class JsUtil {

	private static final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

	/**
	 * Returns a JS string literal for the specified value.
	 *
	 * @param s the string value
	 * @return the string literal
	 */
	public static String toStringLiteral(final String s) {
		return gson.toJson(s);
	}

	/**
	 * Returns a JS string literal for the specified value.
	 *
	 * @param s the string value
	 * @return the string literal
	 */
	public static CharSequence toStringLiteralCharSequence(final String s) {
		return toStringLiteral(s);
	}

}
