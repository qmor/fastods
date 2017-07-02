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

package com.github.jferard.fastods.odselement;

import com.github.jferard.fastods.datastyle.DataStyle;
import com.github.jferard.fastods.style.*;
import com.github.jferard.fastods.util.Container;
import com.github.jferard.fastods.util.Container.Mode;
import com.github.jferard.fastods.util.MultiContainer;
import com.github.jferard.fastods.util.XMLUtil;
import com.github.jferard.fastods.util.ZipUTF8Writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * content.xml/office:document-content
 *
 * @author Julien Férard
 * @author Martin Schulz
 */
public class StylesContainer {

    private static class ChildCellStyle {
        private final TableCellStyle style;
        private final DataStyle dataStyle;

        ChildCellStyle(final TableCellStyle style, final DataStyle dataStyle) {
            this.style = style;
            this.dataStyle = dataStyle;
        }
    }

    /**
     * A register of existing anonymous styles. Won't be added to document.
     */
    private final Map<ChildCellStyle, TableCellStyle> anonymousStyleByChildCellStyle;


    /**
     * Data style that will be written in content.xml > automatic-styles
     * Those styles should be hidden
     */
    private final Container<String, DataStyle> dataStylesContainer;

    private final Container<String, MasterPageStyle> masterPageStylesContainer;

    /**
     * Should be hidden.
     */
    private final Container<String, PageLayoutStyle> pageLayoutStylesContainer;

    private final MultiContainer<String, ObjectStyle, Dest> objectStylesContainer;

    StylesContainer() {
        this.objectStylesContainer = new MultiContainer<String, ObjectStyle, Dest>(
                Dest.class);
        this.dataStylesContainer = new Container<String, DataStyle>();
        this.masterPageStylesContainer = new Container<String, MasterPageStyle>();
        this.pageLayoutStylesContainer = new Container<String, PageLayoutStyle>();
        this.anonymousStyleByChildCellStyle = new HashMap<ChildCellStyle, TableCellStyle>();
    }

    public TableCellStyle addChildCellStyle(final TableCellStyle style, final DataStyle dataStyle) {
        final ChildCellStyle childKey = new ChildCellStyle(style, dataStyle);
        TableCellStyle anonymousStyle = this.anonymousStyleByChildCellStyle.get(childKey);
        if (anonymousStyle == null) {
            this.addDataStyle(dataStyle);
            if (!style.hasParent())
                this.addStyleToStylesCommonStyles(style); // here, the style may be a child style
            final String name = style.getRealName() + "@@" + dataStyle.getName();
            anonymousStyle = TableCellStyle.builder(name).parentCellStyle(style)
                    .dataStyle(dataStyle).buildHidden();
            this.addStyleToContentAutomaticStyles(anonymousStyle);
            this.anonymousStyleByChildCellStyle.put(childKey, anonymousStyle);
        }
        return anonymousStyle;
    }

    /**
     * Create a new data style into styles container. No duplicate style name is allowed.
     *
     * @param dataStyle the data style to add
     */
    public void addDataStyle(final DataStyle dataStyle) {
        assert dataStyle.isHidden() : dataStyle.toString();
        this.dataStylesContainer.add(dataStyle.getName(), dataStyle,
                Mode.CREATE);
    }

    public boolean addDataStyle(final DataStyle dataStyle, final Mode mode) {
        assert dataStyle.isHidden() : dataStyle.toString();
        return this.dataStylesContainer.add(dataStyle.getName(), dataStyle,
                mode);
    }

    /**
     * Create a new master page style into styles container. No duplicate style name is allowed.
     *
     * @param masterPageStyle the data style to add
     * @return true if the style was created
     */
    public boolean addMasterPageStyle(final MasterPageStyle masterPageStyle) {
        return this.addMasterPageStyle(masterPageStyle, Mode.CREATE);
    }

    /**
     * Create a new master page style into styles container. No duplicate style name is allowed.
     *
     * @param masterPageStyle the data style to add
     * @param mode            create, update or "free"
     * @return true if the style was created
     */
    public boolean addMasterPageStyle(final MasterPageStyle masterPageStyle,
                                      final Mode mode) {
        if (this.masterPageStylesContainer.add(masterPageStyle.getName(), masterPageStyle, mode)) {
            masterPageStyle.addEmbeddedStylesToStylesContainer(this, mode);
            return true;
        } else
            return false;
    }

    public void addNewDataStyleFromCellStyle(final TableCellStyle style) {
        this.addStyleToContentAutomaticStyles(style);
        this.addDataStyle(style.getDataStyle());
    }

    public boolean addPageLayoutStyle(final PageLayoutStyle pageLayoutStyle) {
        return this.addPageLayoutStyle(pageLayoutStyle, Mode.CREATE);
    }

    public boolean addPageLayoutStyle(final PageLayoutStyle pageLayoutStyle, final Mode mode) {
        return this.pageLayoutStylesContainer.add(pageLayoutStyle.getName(), pageLayoutStyle, mode);
    }

    public void addPageStyle(final PageStyle ps) {
        this.addMasterPageStyle(ps.getMasterPageStyle());
        this.addPageLayoutStyle(ps.getPageLayoutStyle());
    }

    public void addPageStyle(final PageStyle ps, final Mode mode) {
        this.addMasterPageStyle(ps.getMasterPageStyle(), mode);
        this.addPageLayoutStyle(ps.getPageLayoutStyle(), mode);
    }

    public void addStyleToContentAutomaticStyles(final ObjectStyle objectStyle) {
        assert objectStyle.isHidden() : objectStyle.toString();
        this.objectStylesContainer.add(objectStyle.getKey(), objectStyle,
                Dest.CONTENT_AUTOMATIC_STYLES, Mode.CREATE);
    }

    public boolean addStyleToContentAutomaticStyles(final ObjectStyle objectStyle,
                                                    final Mode mode) {
        assert objectStyle.isHidden();
        return this.objectStylesContainer.add(objectStyle.getKey(), objectStyle,
                Dest.CONTENT_AUTOMATIC_STYLES, mode);
    }

	/*
    @Deprecated
	public Map<String, ObjectStyle> getObjectStyleByName() {
		return this.contentAutomaticStyleTagByName;
	}
	*/

    public void addStyleToStylesAutomaticStyles(final ObjectStyle objectStyle) {
        assert objectStyle.isHidden();
        this.objectStylesContainer.add(objectStyle.getKey(), objectStyle,
                Dest.STYLES_AUTOMATIC_STYLES, Mode.CREATE);
    }

    public boolean addStyleToStylesAutomaticStyles(final ObjectStyle objectStyle,
                                                   final Mode mode) {
        assert objectStyle.isHidden() : objectStyle.toString();
        return this.objectStylesContainer.add(objectStyle.getKey(), objectStyle,
                Dest.STYLES_AUTOMATIC_STYLES, mode);
    }

    public void addStyleToStylesCommonStyles(final ObjectStyle objectStyle) {
        this.objectStylesContainer.add(objectStyle.getKey(), objectStyle,
                Dest.STYLES_COMMON_STYLES, Mode.CREATE);
    }

    public boolean addStyleToStylesCommonStyles(final ObjectStyle objectStyle,
                                                final Mode mode) {
        assert !objectStyle.isHidden();
        return this.objectStylesContainer.add(objectStyle.getKey(), objectStyle,
                Dest.STYLES_COMMON_STYLES, mode);
    }

    public void debug() {
        this.objectStylesContainer.debug();
        this.dataStylesContainer.debug();
        this.masterPageStylesContainer.debug();
        this.pageLayoutStylesContainer.debug();
    }

    public void freeze() {
        this.objectStylesContainer.freeze();
        this.dataStylesContainer.freeze();
        this.masterPageStylesContainer.freeze();
        this.pageLayoutStylesContainer.freeze();
    }

    @Deprecated
    public Map<String, DataStyle> getDataStyles() {
        return this.dataStylesContainer.getValueByKey();
    }

    public Map<String, MasterPageStyle> getMasterPageStyles() {
        return this.masterPageStylesContainer.getValueByKey();
    }

    public Map<String, PageLayoutStyle> getPageLayoutStyles() {
        return this.pageLayoutStylesContainer.getValueByKey();
    }

    public Map<String, ObjectStyle> getObjectStyleByName(final Dest dest) {
        return this.objectStylesContainer.getValueByKey(dest);
    }

    public HasFooterHeader hasFooterHeader() {
        boolean hasHeader = false;
        boolean hasFooter = false;

        for (final MasterPageStyle ps : this.masterPageStylesContainer
                .getValues()) {
            if (hasHeader && hasFooter)
                break;
            if (!hasHeader && ps.getHeader() != null)
                hasHeader = true;
            if (!hasFooter && ps.getFooter() != null)
                hasFooter = true;
        }
        return new HasFooterHeader(hasHeader, hasFooter);
    }

    private void write(final Iterable<ObjectStyle> iterable, final XMLUtil util,
                       final ZipUTF8Writer writer) throws IOException {
        for (final ObjectStyle os : iterable)
            os.appendXML(util, writer);
    }

    /**
     * Write the various styles in the automatic styles.
     *
     * @param util   an XML util
     * @param writer the destination
     * @throws IOException if the styles can't be written
     */
    public void writeContentAutomaticStyles(final XMLUtil util,
                                            final ZipUTF8Writer writer) throws IOException {
        final Iterable<ObjectStyle> styles = this.objectStylesContainer
                .getValues(Dest.CONTENT_AUTOMATIC_STYLES);
        for (final ObjectStyle style : styles)
            assert style.isHidden() : style.toString();

        this.write(styles, util, writer);
    }

    /**
     * Write the data styles in the automatic styles.
     *
     * @param util   an XML util
     * @param writer the destination
     * @throws IOException if the styles can't be written
     */
    public void writeDataStyles(final XMLUtil util, final ZipUTF8Writer writer)
            throws IOException {
        for (final DataStyle dataStyle : this.dataStylesContainer.getValues()) {
            assert dataStyle.isHidden() : dataStyle.toString();
            dataStyle.appendXML(util, writer);
        }
    }

    public void writeMasterPageStylesToAutomaticStyles(final XMLUtil util,
                                                       final ZipUTF8Writer writer) throws IOException {
        for (final PageLayoutStyle ps : this.pageLayoutStylesContainer
                .getValues()) {
            assert ps.isHidden();
            ps.appendXMLToAutomaticStyle(util, writer);
        }
    }

    public void writeMasterPageStylesToMasterStyles(final XMLUtil util,
                                                    final ZipUTF8Writer writer) throws IOException {
        for (final MasterPageStyle ps : this.masterPageStylesContainer
                .getValues())
            ps.appendXMLToMasterStyle(util, writer);
    }

    public void writeStylesAutomaticStyles(final XMLUtil util,
                                           final ZipUTF8Writer writer) throws IOException {
        final Iterable<ObjectStyle> styles = this.objectStylesContainer.getValues(Dest.STYLES_AUTOMATIC_STYLES);
        for (final ObjectStyle style : styles)
            assert style.isHidden() : style.toString();

        this.write(styles, util, writer);
    }

    public void writeStylesCommonStyles(final XMLUtil util,
                                        final ZipUTF8Writer writer) throws IOException {
        final Iterable<ObjectStyle> styles = this.objectStylesContainer.getValues(Dest.STYLES_COMMON_STYLES);
        for (final ObjectStyle style : styles)
            assert !style.isHidden() : style.toString() + " - " + style.getName() + TableCellStyle.DEFAULT_CELL_STYLE.toString();

        this.write(styles,
                util, writer);
    }

    public enum Dest {
        CONTENT_AUTOMATIC_STYLES, STYLES_AUTOMATIC_STYLES, STYLES_COMMON_STYLES,
    }
}
