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

package com.github.jferard.fastods.style;

import com.github.jferard.fastods.Color;
import com.github.jferard.fastods.datastyle.DataStyle;
import com.github.jferard.fastods.odselement.OdsElements;
import com.github.jferard.fastods.util.SimpleLength;
import com.github.jferard.fastods.util.XMLUtil;

import java.io.IOException;

/**
 * WHERE ? content.xml/office:document-content/office:automatic-styles/style:
 * style
 *
 * @author Julien Férard
 * @author Martin Schulz
 */
public class TableCellStyle implements StyleTag {
	private static TableCellStyle defaultCellStyle;

	public static TableCellStyleBuilder builder(final String name) {
		return new TableCellStyleBuilder(name);
	}

	public static TableCellStyle getDefaultCellStyle() {
		if (TableCellStyle.defaultCellStyle == null)
			TableCellStyle.defaultCellStyle = TableCellStyle.builder("Default")
					.textAlign(TableCellStyle.Align.LEFT)
					.verticalAlign(TableCellStyle.VerticalAlign.TOP)
					.fontWrap(false).backgroundColor(Color.WHITE)
					.allMargins(SimpleLength.mm(0.0)).parentCellStyle(null).build();

		return TableCellStyle.defaultCellStyle;
	}

	private final String backgroundColor;
	private final Borders borders;
	private final Margins margins;
	private final String name;
	// true
	private final String parentCellStyleName;
	private final Align textAlign; // 'center','end','start','justify'
	private final TextProperties textProperties;
	private final VerticalAlign verticalAlign; // 'middle', 'bottom', 'top'
	private final boolean wrap; // No line wrap when false, line wrap when
	private DataStyle dataStyle;
	private String key;

	/**
	 * Create a new cell style
	 *
	 * @param name A unique name for this style
	 */
	TableCellStyle(final String name, final DataStyle dataStyle,
				   final String backgroundColor, final TextProperties textProperties,
				   final Align textAlign, final VerticalAlign verticalAlign,
				   final boolean wrap, final String parentCellStyleName,
				   final Borders borders, final Margins margins) {
		this.borders = borders;
		this.margins = margins;
		this.name = name;
		this.dataStyle = dataStyle;
		this.backgroundColor = backgroundColor;
		this.textProperties = textProperties;
		this.textAlign = textAlign;
		this.verticalAlign = verticalAlign;
		this.wrap = wrap;
		this.parentCellStyleName = parentCellStyleName;
	}

	@Override
	public void addToElements(final OdsElements odsElements) {
		if (this.dataStyle != null)
			this.dataStyle.addToElements(odsElements);
		odsElements.addStyleTag(this);
	}

	private void appendCellProperties(final XMLUtil util, final Appendable appendable) throws IOException {
		appendable.append("<style:table-cell-properties");
		if (this.backgroundColor != null)
			util.appendAttribute(appendable, "fo:background-color",
					this.backgroundColor);

		if (this.verticalAlign != null)
			util.appendEAttribute(appendable, "style:vertical-align",
					this.verticalAlign.attrValue);

		this.borders.appendXMLToTableCellStyle(util, appendable);

		if (this.wrap)
			util.appendEAttribute(appendable, "fo:wrap-option", "wrap");

		appendable.append("/>");
	}

	/**
	 * Write the XML format for this object.<br>
	 * This is used while writing the ODS file.
	 *
	 * @throws IOException If an I/O error occurs
	 */
	@Override
	public void appendXML(final XMLUtil util, final Appendable appendable)
			throws IOException {
		appendable.append("<style:style");
		util.appendEAttribute(appendable, "style:name", this.name);
		util.appendEAttribute(appendable, "style:family", "table-cell");
		if (this.parentCellStyleName != null)
			util.appendEAttribute(appendable, "style:parent-style-name",
					this.parentCellStyleName);
		if (this.dataStyle != null)
			util.appendAttribute(appendable, "style:data-style-name",
					this.dataStyle.getName());

		if (this.hasCellProperties() || this.hasTextProperties() || this.hasParagraphProperties()) {
			appendable.append(">");
			if (this.hasCellProperties()) {
				this.appendCellProperties(util, appendable);
			}

			if (this.hasTextProperties()) {
				this.textProperties.appendXMLContent(util, appendable);
			}

			if (this.hasParagraphProperties()) {
				appendable.append("<style:paragraph-properties");
				if (this.textAlign != null)
					util.appendEAttribute(appendable, "fo:text-align",
							this.textAlign.attrValue);

				this.margins.appendXMLToTableCellStyle(util, appendable);
				appendable.append("/>");
			}
			appendable.append("</style:style>");
		} else {
			appendable.append("/>");
		}
	}

	boolean hasTextProperties() {
		return this.textProperties != null && this.textProperties.isNotEmpty();
	}

	private boolean hasParagraphProperties() {
		return this.textAlign != null || !this.margins.areVoid();
	}

	public DataStyle getDataStyle() {
		return this.dataStyle;
	}

	@Override
	public String getFamily() {
		return "table-cell";
	}

	@Override
	public String getKey() {
		if (this.key == null)
			this.key = this.getFamily() + "@" + this.getName();
		return this.key;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getRealName() {
		final int index = this.name.indexOf("@@");
		if (index > 0)
			return this.name.substring(0, index);
		else
			return this.name;
	}

	private boolean hasCellProperties() {
		return this.backgroundColor != null || this.verticalAlign != null || !this.borders.areVoid() || this.wrap;
	}

	public void setDataStyle(final DataStyle dataStyle) {
		this.dataStyle = dataStyle;
	}

	public boolean hasParent() {
		return this.parentCellStyleName != null;
	}

	public static enum Align {
		CENTER("center"), JUSTIFY("justify"), LEFT("start"), RIGHT("end");

		private final String attrValue;

		private Align(final String attrValue) {
			this.attrValue = attrValue;
		}
	}

	public static enum VerticalAlign {
		BOTTOM("bottom"), MIDDLE("middle"), TOP("top");

		private final String attrValue;

		private VerticalAlign(final String attrValue) {
			this.attrValue = attrValue;
		}
	}
}