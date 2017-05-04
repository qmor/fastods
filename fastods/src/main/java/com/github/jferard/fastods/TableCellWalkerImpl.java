/*
 * FastODS - a Martin Schulz's SimpleODS fork
 *    Copyright (C) 2016-2017 J. Férard <https://github.com/jferard>
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

package com.github.jferard.fastods;

import com.github.jferard.fastods.style.TableCellStyle;
import com.github.jferard.fastods.util.Length;
import com.github.jferard.fastods.util.XMLUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Julien Férard
 */
public class TableCellWalkerImpl implements TableCellWalker {
	final static SimpleDateFormat DATE_VALUE_FORMAT;

	static {
		/**
		 * XML Schema Part 2, 3.2.7 dateTime
		 * Z and UTC time zone for universal time.
		 */
		DATE_VALUE_FORMAT = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DATE_VALUE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private final TableRow row;
	private int c;

	TableCellWalkerImpl(final TableRow row) {
		this.row = row;
		this.c = 0;
	}

	@Override
	public void appendXMLToTableRow(final XMLUtil util,
									final Appendable appendable) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBooleanValue(final boolean value) {
		this.row.getOrCreateCell(this.c).setBooleanValue(value);
	}

	@Override
	public void setText(final Text text) {
		this.row.getOrCreateCell(this.c).setText(text);
	}

	@Override
	public void setColumnsSpanned(final int n) {
		this.row.setColumnsSpanned(this.c, n);
	}

	@Override
	public void setDateValue(final Date value) {
		this.row.getOrCreateCell(this.c).setDateValue(value);
	}

	@Override
	public void setFloatValue(final Number value) {
		this.row.getOrCreateCell(this.c).setFloatValue(value);
	}

	@Override
	public void setPercentageValue(final Number value) {
		this.row.getOrCreateCell(this.c).setPercentageValue(value);
	}

	@Override
	public void setRowsSpanned(final int n) throws IOException {
		this.row.setRowsSpanned(this.c, n);
	}

	@Override
	public void setVoidValue() {
		this.row.getOrCreateCell(this.c).setVoidValue();
	}

	@Override
	public void setStringValue(final String value) {
		this.row.getOrCreateCell(this.c).setStringValue(value);
	}

	@Override
	public void setTimeValue(final long timeInMillis) {
		this.row.getOrCreateCell(this.c).setTimeValue(timeInMillis);
	}

	@Override
	public void setTooltip(final String tooltip) {
		this.row.getOrCreateCell(this.c).setTooltip(tooltip);
	}

	@Override
	public void setTooltip(final String tooltip, final Length width, final Length height, final boolean visible) {
		this.row.getOrCreateCell(this.c).setTooltip(tooltip, width, height, visible);
	}

	@Override
	public void setFormula(final String formula) {
		this.row.getOrCreateCell(this.c).setFormula(formula);

	}

	@Override
	public TableCellStyle getStyle() {
		return this.row.getOrCreateCell(this.c).getStyle();
	}

	@Override
	public boolean hasValue() {
		return this.row.getOrCreateCell(this.c).hasValue();
	}

	@Override
	public boolean isCovered() {
		return this.row.getOrCreateCell(this.c).isCovered();
	}

	@Override
	public void setCovered() {
		this.row.getOrCreateCell(this.c).setCovered();
	}

	@Override
	public boolean hasNext() {
		return this.c < this.row.getColumnCount();
	}

	@Override
	public boolean hasPrevious() {
		return this.c > 0 && this.c <= this.row.getColumnCount();
	}

	@Override
	public void lastCell() {
		this.c = this.row.getColumnCount() - 1;
	}

	@Override
	public void next() {
		if (this.c >= this.row.getColumnCount())
			throw new IndexOutOfBoundsException();
		this.c++;
	}

	@Override
	public void previous() {
		if (this.c <= 0)
			throw new IndexOutOfBoundsException();
		this.c--;
	}

	@Override
	public void setCellValue(final CellValue value) {
		this.row.getOrCreateCell(this.c).setCellValue(value);
	}

	@Override
	public void setCurrencyValue(final float value, final String currency) {
		this.row.getOrCreateCell(this.c).setCurrencyValue(value, currency);
	}

	@Override
	public void setCurrencyValue(final int value, final String currency) {
		this.row.getOrCreateCell(this.c).setCurrencyValue(value, currency);
	}

	@Override
	public void setCurrencyValue(final Number value, final String currency) {
		this.row.getOrCreateCell(this.c).setCurrencyValue(value, currency);
	}

	@Override
	public void setDateValue(final Calendar cal) {
		this.row.getOrCreateCell(this.c).setDateValue(cal);
	}

	@Override
	public void setFloatValue(final float value) {
		this.row.getOrCreateCell(this.c).setFloatValue(value);
	}

	@Override
	public void setFloatValue(final int value) {
		this.row.getOrCreateCell(this.c).setFloatValue(value);
	}

	/**
	 * @deprecated Shortcut for {@code setCellValue(c, CellValue.fromObject(object))}
	 */
	@Override
	@Deprecated
	public void setObjectValue(final Object value) {
		this.row.getOrCreateCell(this.c).setObjectValue(value);
	}

	@Override
	public void setPercentageValue(final float value) {
		this.row.getOrCreateCell(this.c).setPercentageValue(value);
	}

	@Override
	public void setPercentageValue(final int value) {
		this.row.getOrCreateCell(this.c).setPercentageValue(value);
	}

	@Override
	public void setStyle(final TableCellStyle style) {
		this.row.getOrCreateCell(this.c).setStyle(style);
	}

	@Override
	public void to(final int c) {
		if (c < 0)
			throw new IndexOutOfBoundsException();
		this.c = c;
	}
}