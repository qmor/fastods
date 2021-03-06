/*
 * FastODS - A very fast and lightweight (no dependency) library for creating ODS
 *    (Open Document Spreadsheet, mainly for Calc) files in Java.
 *    It's a Martin Schulz's SimpleODS fork
 *    Copyright (C) 2016-2018 J. Férard <https://github.com/jferard>
 * SimpleODS - A lightweight java library to create simple OpenOffice spreadsheets
 *    Copyright (C) 2008-2013 Martin Schulz <mtschulz at users.sourceforge.net>
 *
 * This file is part of FastODS.
 *
 * FastODS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * FastODS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.jferard.fastods.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * An util for writing XML representation.
 * @author Julien Férard
 */
public class XMLUtil {
	/**
	 * A space char for append
	 */
	public static final char SPACE_CHAR = ' ';
	private final XMLEscaper escaper;

    /**
     * Create a new util
     * @param escaper the embedded escaper
     */
    XMLUtil(final XMLEscaper escaper) {
		this.escaper = escaper;
	}

    /**
     * @return a new default xml util
     */
    public static XMLUtil create() {
		final XMLEscaper escaper = FastOdsXMLEscaper.create();
		return new XMLUtil(escaper);
	}

	/**
	 * Append a space and new element to the appendable element, the name of the element is
	 * attrName and the value is attrRawValue. The will be escaped if necessary
	 *
	 * @param appendable   The StringBuilder to which the new element should be added.
	 * @param attrName     The new element name
	 * @param attrRawValue The value of the element
	 * @throws IOException If an I/O error occurs
	 */
	public void appendEAttribute(final Appendable appendable,
								final CharSequence attrName, final String attrRawValue)
			throws IOException {
		appendable.append(' ').append(attrName).append("=\"")
				.append(this.escaper.escapeXMLAttribute(attrRawValue))
				.append('"');
	}

    /**
     * Append a new element to the appendable element, the name of the element is
     * attrName and the value is the boolean attrValue.
     *
     * @param appendable The StringBuilder to which the new element should be added.
     * @param attrName   The new element name
     * @param attrValue  The value of the element
     * @throws IOException If an I/O error occurs
     */
    public void appendAttribute(final Appendable appendable,
                                final CharSequence attrName, final boolean attrValue) throws IOException {
		this.appendAttribute(appendable, attrName,
				Boolean.toString(attrValue));
	}

	/**
	 * Append a new element to the appendable element, the name of the element is
	 * attrName and the value is attrValue.
	 *
	 * @param appendable The StringBuilder to which the new element should be added.
	 * @param attrName   The new element name
	 * @param attrValue  The value of the element
	 * @throws IOException If an I/O error occurs
	 */
	public void appendAttribute(final Appendable appendable,
                                final CharSequence attrName, final int attrValue) throws IOException {
		this.appendAttribute(appendable, attrName,
				Integer.toString(attrValue));
	}

	/**
	 * Append a space, then a new element to the appendable element, the name of the element is
	 * attrName and the value is attrValue. The value won't be escaped.
	 *
	 * @param appendable where to write
	 * @param attrName   the name of the attribute
	 * @param attrValue  escaped attribute
	 * @throws IOException If an I/O error occurs
	 */
	public void appendAttribute(final Appendable appendable,
                                final CharSequence attrName, final CharSequence attrValue) throws IOException {
		appendable.append(' ').append(attrName).append("=\"").append(attrValue)
				.append('"');
	}

    /**
     * Append a content inside a tag
     * @param appendable the destination
     * @param tagName the tag name
     * @param content the content
     * @throws IOException if an I/O error occurs
     */
    public void appendTag(final Appendable appendable, final CharSequence tagName,
						  final String content) throws IOException {
		appendable.append('<').append(tagName).append('>')
				.append(this.escaper.escapeXMLContent(content)).append("</")
				.append(tagName).append('>');
	}

    /**
     * Escape an XML attribute
     * @param s the attribute
     * @return the escaped attribute
     */
    public String escapeXMLAttribute(final String s) {
		return this.escaper.escapeXMLAttribute(s);
	}

    /**
     * Escape an XML content
     * @param s the content
     * @return the escaped content
     */
    public String escapeXMLContent(final String s) {
		return this.escaper.escapeXMLContent(s);
	}

	/**
	 * XML Schema Part 2, 3.2.6 duration
	 * "'P'yyyy'Y'MM'M'dd'DT'HH'H'mm'M'ss.SSS'S'"
	 * Remark: removed days since OdfToolkit can't handle it
	 *
	 * @param milliseconds the interval to format in milliseconds
	 * @return the string that represents this interval
	 */
	public String formatTimeInterval(final long milliseconds) {
		long curMilliseconds = milliseconds;
		final StringBuilder sb = new StringBuilder().append("PT");
		final long hours = TimeUnit.MILLISECONDS.toHours(curMilliseconds);
		if (hours < 10)
			sb.append('0');
		sb.append(hours).append('H');
		curMilliseconds -= TimeUnit.HOURS.toMillis(hours);

		final long minutes = TimeUnit.MILLISECONDS.toMinutes(curMilliseconds);
		if (minutes < 10)
			sb.append('0');
		sb.append(minutes).append('M');
		curMilliseconds -= TimeUnit.MINUTES.toMillis(minutes);

		final long seconds = TimeUnit.MILLISECONDS.toSeconds(curMilliseconds);
		if (seconds < 10)
			sb.append('0');
		sb.append(seconds);
		curMilliseconds -= TimeUnit.SECONDS.toMillis(seconds);

		if (curMilliseconds > 0)
			sb.append('.').append(curMilliseconds);

		sb.append('S');
		return sb.toString();
	}
}
