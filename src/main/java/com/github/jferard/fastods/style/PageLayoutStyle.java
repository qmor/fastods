/*
 * FastODS - a Martin Schulz's SimpleODS fork
 *    Copyright (C) 2016 J. Férard <https://github.com/jferard>
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

import com.github.jferard.fastods.FooterHeader;
import com.github.jferard.fastods.odselement.OdsElements;
import com.github.jferard.fastods.util.XMLUtil;

import java.io.IOException;

/**
 * OpenDocument 16.5 style:page-layout
 *
 * @author Julien Férard
 * @author Martin Schulz
 */
public class PageLayoutStyle implements AddableToOdsElements {
	private final String backgroundColor;
	private final FooterHeader footer;
	private final FooterHeader header;
	private final Margins margins;

	private final String name;
	private final String numFormat;
	private final String pageHeight;

	private final String pageWidth;
	private final PageStyle.PaperFormat paperFormat;

	private final PageStyle.PrintOrientation printOrientation;

	private final PageStyle.WritingMode writingMode;

	/**
	 * Create a new page style. Version 0.5.0 Added parameter OdsDocument o
	 *
	 * @param name
	 *            A unique name for this style
	 * @param header
	 * @param footer
	 */
	public PageLayoutStyle(final String name, final Margins margins,
						   final String pageWidth, final String pageHeight,
						   final String numFormat, final String backgroundColor,
						   final FooterHeader footer, final FooterHeader header,
						   final PageStyle.PrintOrientation printOrientation,
						   final PageStyle.PaperFormat paperFormat, final PageStyle.WritingMode writingMode) {
		this.name = name;
		this.margins = margins;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.numFormat = numFormat;
		this.backgroundColor = backgroundColor;
		this.footer = footer;
		this.header = header;
		this.printOrientation = printOrientation;
		this.paperFormat = paperFormat;
		this.writingMode = writingMode;
	}

	@Override
	public void addToElements(final OdsElements odsElements) {
		odsElements.addPageLayoutStyle(this);
	}

	/**
	 * Write the XML format for this object.<br>
	 * This is used while writing the ODS file.
	 *
	 */
	public void appendXMLToAutomaticStyle(final XMLUtil util,
			final Appendable appendable) throws IOException {
		appendable.append("<style:page-layout");
		util.appendAttribute(appendable, "style:name", this.name);
		appendable.append("><style:page-layout-properties");
		util.appendAttribute(appendable, "fo:page-width", this.pageWidth);
		util.appendAttribute(appendable, "fo:page-height", this.pageHeight);
		util.appendAttribute(appendable, "style:num-format", this.numFormat);
		util.appendEAttribute(appendable, "style:writing-mode",
				this.writingMode.getAttrValue());
		util.appendEAttribute(appendable, "style:print-orientation",
				this.printOrientation.getAttrValue());
		this.appendBackgroundColor(util, appendable);
		this.margins.appendXMLToTableCellStyle(util, appendable);
		appendable.append("/>"); // End of page-layout-properties

		FooterHeader.appendStyleFooterHeaderXMLToAutomaticStyle(this.header, FooterHeader.Type.HEADER, util, appendable);
		FooterHeader.appendStyleFooterHeaderXMLToAutomaticStyle(this.footer, FooterHeader.Type.FOOTER, util, appendable);
		appendable.append("</style:page-layout>");
	}

	public String getBackgroundColor() {
		return this.backgroundColor;
	}

	public FooterHeader getFooter() {
		return this.footer;
	}

	public FooterHeader getHeader() {
		return this.header;
	}

	public Margins getMargins() {
		return this.margins;
	}

	/**
	 * Get the name of this page style.
	 *
	 * @return The page style name
	 */
	public String getName() {
		return this.name;
	}

	public String getPageHeight() {
		return this.pageHeight;
	}

	public String getPageWidth() {
		return this.pageWidth;
	}

	/**
	 * Get the paper format as one of PageStyle.STYLE_PAPERFORMAT_*.
	 */
	public PageStyle.PaperFormat getPaperFormat() {
		return this.paperFormat;
	}

	/**
	 * Get the writing mode<br>
	 * . STYLE_WRITINGMODE_LRTB lr-tb (left to right; top to bottom)<br>
	 * STYLE_WRITINGMODE_RLTB<br>
	 * STYLE_WRITINGMODE_TBRL<br>
	 * STYLE_WRITINGMODE_TBLR<br>
	 * STYLE_WRITINGMODE_LR<br>
	 * STYLE_WRITINGMODE_RL<br>
	 * STYLE_WRITINGMODE_TB<br>
	 * STYLE_WRITINGMODE_PAGE<br>
	 *
	 * @return The current writing mode.
	 */
	public PageStyle.WritingMode getWritingMode() {
		return this.writingMode;
	}

	private void appendBackgroundColor(final XMLUtil util,
			final Appendable appendable) throws IOException {
		if (this.getBackgroundColor().length() > 0) {
			util.appendAttribute(appendable, "fo:background-color",
					this.backgroundColor);
		}
	}
}