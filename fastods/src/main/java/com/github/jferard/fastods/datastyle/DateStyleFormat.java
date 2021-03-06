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

package com.github.jferard.fastods.datastyle;

import com.github.jferard.fastods.XMLConvertible;
import com.github.jferard.fastods.util.XMLUtil;

import java.io.IOException;

/**
 * @author Julien Férard
 * @author Martin Schulz
 */
public class DateStyleFormat implements XMLConvertible {
	/**
	 * The default date format Format.DDMMYY.
	 */
	public static final String DASH = "<number:text>-</number:text>";
    /**
     * the day
     */
    public static final String DAY = "<number:day/>";
    /**
     * A dot
     */
    public static final String DOT = "<number:text>.</number:text>";
    /**
     * A dot and a space
     */
    public static final String DOT_SPACE = "<number:text>. </number:text>";
    /**
     * A slash
     */
    public static final String SLASH = "<number:text>/</number:text>";
    /**
     * A day (long)
     */
    public static final String LONG_DAY = "<number:day number:style=\"long\"/>";
    /**
     * A month (long)
     */
    public static final String LONG_MONTH = "<number:month number:style=\"long\"/>";
    /**
     * A month
     */
    public static final String MONTH = "<number:month/>";
    /**
     * A month name
     */
    public static final String LONG_TEXTUAL_MONTH = "<number:month number:style=\"long\" number:textual=\"true\"/>";
    /**
     * A year YYYY
     */
    public static final String LONG_YEAR = "<number:year number:style=\"long\"/>";
    /**
     * A space
     */
    public static final String SPACE = "<number:text> </number:text>";
    /**
     * A week number in the year
     */
    public static final String WEEK = "<number:week-of-year/>";
    /**
     * A year YY
     */
    public static final String YEAR = "<number:year/>";
    private final String[] strings;

    /**
     * The constructor
     * @param strings the string that compose the format
     */
    public DateStyleFormat(final String... strings) {
        this.strings = strings;
    }

    @Override
    public void appendXMLContent(final XMLUtil util, final Appendable appendable) throws IOException {
        for (final String string : this.strings)
            appendable.append(string);
    }
}
