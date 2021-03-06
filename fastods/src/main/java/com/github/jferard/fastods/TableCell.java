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

package com.github.jferard.fastods;

import com.github.jferard.fastods.datastyle.DataStyle;
import com.github.jferard.fastods.style.TableCellStyle;
import com.github.jferard.fastods.util.Length;
import com.github.jferard.fastods.util.XMLUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * A TableCell represents a cell in a spreadsheet sheet.
 * @author Julien Férard
 */
public interface TableCell {
    /**
	 * 19.385 office:value-type.
	 * Javadoc text below is taken from Open Document Format for Office Applications (OpenDocument) Version 1.2
	 */
	enum Type {
		/**
		 * a boolean: "true or false"
		 */
		BOOLEAN("office:boolean-value", "boolean"),
		/**
		 * a currency: "Numeric value and currency symbol"
		 */
		CURRENCY("office:value", "currency"),
		/**
		 * a date: "Date value as specified in §3.2.9 of [xmlschema-2], or date and time value as
		 * specified in §3.2.7 of [xmlschema-2]"
		 */
		DATE("office:date-value", "date"),
		/**
		 * a float: "Numeric value"
		 */
		FLOAT("office:value", "float"),
		/**
		 * a percentage: "Numeric value"
		 */
		PERCENTAGE("office:value", "percentage"),
		/**
		 * a string: "String"
		 */
		STRING("office:string-value", "string"),
		/**
		 * a time: "Duration, as specified in §3.2.6 of [xmlschema-2]"
		 */
		TIME("office:time-value", "time"),
		/**
		 * a void cell value: nothing.
		 */
		VOID("office-value", "");

		final String valueType;
		final String valueAttribute;

		/**
		 * @param valueType the value type. Will produce office:value-type="float"
		 * @param valueAttribute the value attribute. Will store the value as in: office:string-value="xyz".
		 */
		Type(final String valueType, final String valueAttribute) {
			this.valueAttribute = valueAttribute;
			this.valueType = valueType;
		}

        /**
         * @return the value type
         */
        public String getValueType() {
			return this.valueType;
		}

        /**
         * @return the value attribute
         */
        public String getValueAttribute() {
			return this.valueAttribute;
		}
	}

    /**
     * Generate the XML for the table cell.
     * @param util an util.
     * @param appendable the appendable to fill
     * @throws IOException if an error occurs
     */
    void appendXMLToTableRow(XMLUtil util, Appendable appendable)
			throws IOException;

    /**
     * Marks a number of rows with a span
     * @param n the number of rows
     */
    void markRowsSpanned(int n);

	/**
	 * Set the boolean value
	 * @param value true or false
	 */
	void setBooleanValue(boolean value);

	/**
	 * Set the float value for a cell with TableCell.Type.STRING.
	 *
	 * @param value
	 *            the value as a CellValue object.
	 *
	 */
	void setCellValue(CellValue value);

	/**
	 * Set the currency value and table cell style to STYLE_CURRENCY.
	 *
	 * @param value the value as a float
	 * @param currency
	 *            The currency value
	 */
	void setCurrencyValue(float value, String currency);

	/**
	 * Set the currency value and table cell style to STYLE_CURRENCY.
	 *
	 * @param value the value as an int
	 * @param currency
	 *            The currency value
	 */
	void setCurrencyValue(int value, String currency);

	/**
	 * Set the currency value and table cell style to STYLE_CURRENCY.
	 *
	 * @param value the value as a Number
	 * @param currency the currency value
	 */
	void setCurrencyValue(Number value, String currency);

	/**
	 * Set the date value for a cell with TableCell.STYLE_DATE.
	 *
	 * @param cal a Calendar object with the date to be used
	 */
	void setDateValue(Calendar cal);

    /**
     * Set the date value for a cell with TableCell.STYLE_DATE.
     * @param date a Date object
     */
    void setDateValue(Date date);

	/**
	 * Set the float value for a cell with TableCell.Type.FLOAT.
	 *
	 * @param value a double object with the value to be used
	 */
	void setFloatValue(float value);

	/**
	 * Set the float value for a cell with TableCell.Type.FLOAT.
	 *
	 * @param value a double object with the value to be used
	 */
	void setFloatValue(int value);

	/**
	 * Set the float value for a cell with TableCell.Type.FLOAT.
	 *
	 * @param value a double object with the value to be used
	 */
	void setFloatValue(Number value);

	/**
	 * Set the float value for a cell with TableCell.Type.STRING.
	 *
	 * @param value a double object with the value to be used
	 */
	void setObjectValue(Object value);

	/**
	 * Set the float value for a cell with TableCell.Type.PERCENTAGE.
	 *
	 * @param value a float object with the value to be used
	 */
	void setPercentageValue(float value);

	/**
	 * Set the int value for a cell with TableCell.Type.PERCENTAGE.
	 *
	 * @param value an int with the value to be used
	 */
	void setPercentageValue(int value);

	/**
	 * Set the float value for a cell with TableCell.Type.PERCENTAGE.
	 *
	 * @param value a double object with the value to be used
	 */
	void setPercentageValue(Number value);

	/**
	 * Set the float value for a cell with TableCell.Type.STRING.
	 *
	 * @param value a double object with the value to be used
	 */
	void setStringValue(String value);

    /**
     * Set a style for this cell
     * @param style the style
     */
    void setStyle(TableCellStyle style);

	/**
	 * Set the time value as in 19.382 office:time-value. The xml datatype is "duration" (https://www.w3.org/TR/xmlschema-2/#duration)
	 * @param timeInMillis the duration in milliseconds
	 */
	void setTimeValue(long timeInMillis);

	/**
	 * Add a tooltip to the cell
	 *
	 * @param tooltip the text of the tooltip
	 */
	void setTooltip(String tooltip);

    /**
     * Add a tooltip to the cell
     *
     * @param tooltip the text of the tooltip
     * @param width the width of the tooltip
     * @param height the height of the tooltip
     * @param visible if the tooltip should be visible.
     */
	void setTooltip(String tooltip, Length width, Length height, boolean visible);

	/**
	 * Sets a formula in an existing cell. The user is responsible for creating the cell and setting the
	 * correct value, as show below:
	 * <pre>{@code
	 *     walker.setFloatValue(2.0);
	 *     walker.setFormula("1+1");
	 * }</pre>
	 *
	 * One can type Shift+Ctrl+F9 to recalculate the right value in LibreOffice.
	 *
	 * @param formula the formula, without '=' sign.
	 */
	void setFormula(String formula);

    /**
     * @return true if the cell is covered by a span
     */
    boolean isCovered();

    /**
     * Set the cell covered flag
     */
    void setCovered();

    /**
     * Create a span over cells at the right
     * @param n the number of cells to be spanned
     */
    void setColumnsSpanned(int n);

    /**
     * Mark the columns a spanned
     * @param n the number of columns
     */
    void markColumnsSpanned(int n);

    /**
     * Create a span over cells below
     * @param n the number of cells to be spanned
     * @throws IOException if the cell can't be merged (only when flushing data)
     */
	void setRowsSpanned(int n) throws IOException;

    /**
     * Set a void value in this cell
     */
    void setVoidValue();

    /**
     * @return true if the cell has a value. A void value is a value
     */
    boolean hasValue();

    /**
     * Set a text in this cell
     * @param text the text
     */
    void setText(Text text);

    /**
     * Merge cells
     * @param rowMerge number of rows below
     * @param columnMerge number of rows at the right
     * @throws IOException if the cell can't be merged (only when flushing data)
     */
    void setCellMerge(int rowMerge,
							 int columnMerge) throws IOException;

	/**
	 * Set a custom data style. In an Open Document, a data style is always carried by a style.
	 * Thus, FastOds will create a new style, child of the current style, with the given data style.
     * The new style will have the same visibility as the data style.
	 * @param dataStyle the data style
	 */
	void setDataStyle(DataStyle dataStyle);
}