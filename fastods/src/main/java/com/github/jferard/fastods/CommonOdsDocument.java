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

import com.github.jferard.fastods.odselement.OdsElements;
import com.github.jferard.fastods.style.PageStyle;
import com.github.jferard.fastods.style.TableCellStyle;
import com.github.jferard.fastods.style.TableColumnStyle;
import com.github.jferard.fastods.style.TableRowStyle;
import com.github.jferard.fastods.style.TableStyle;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

/**
 * Common part of an ods document.
 *
 * @author Julien Férard
 * @author Martin Schulz
 */
class CommonOdsDocument implements OdsDocument {
    /**
     * the default column capacity.
     */
    static final int DEFAULT_COLUMN_CAPACITY = 32;
    /**
     * the default row capacity.
     */
    static final int DEFAULT_ROW_CAPACITY = 1024;
    private final OdsElements odsElements;

    /**
     * Create a new ODS file.
     *
     * @param odsElements the ods elements (file entries in zip archive)
     */
    CommonOdsDocument(final OdsElements odsElements) {
        this.odsElements = odsElements;

        // Add five default stylesEntry to contentEntry
        TableStyle.DEFAULT_TABLE_STYLE.addToElements(this.odsElements);
        TableRowStyle.DEFAULT_TABLE_ROW_STYLE.addToElements(this.odsElements);
        TableColumnStyle.DEFAULT_TABLE_COLUMN_STYLE.addToElements(this.odsElements);
        TableCellStyle.DEFAULT_CELL_STYLE.addToElements(this.odsElements);
        PageStyle.DEFAULT_PAGE_STYLE.addToElements(this.odsElements);
    }

    @Override
    public Table addTable(final String name) throws IOException {
        return this.addTable(name, CommonOdsDocument.DEFAULT_ROW_CAPACITY,
                CommonOdsDocument.DEFAULT_COLUMN_CAPACITY);
    }

    @Override
    public Table addTable(final String name, final int rowCapacity, final int columnCapacity)
            throws IOException {
        final Table table = this.odsElements.addTableToContent(name, rowCapacity, columnCapacity);
        this.odsElements.setActiveTable(table);
        return table;
    }

    @Override
    public Table getTable(final int n) throws FastOdsException {
        final List<Table> tables = this.odsElements.getTables();
        if (n < 0 || n >= tables.size()) {
            throw FastOdsException.wrongTableNumber(n);
        }

        return tables.get(n);
    }

    @Override
    public Table getTable(final String name) throws FastOdsException {
        final Table table = this.odsElements.getTable(name);
        if (table == null) {
            throw FastOdsException.wrongTableName(name);
        }
        return table;
    }

    @Override
    public Table getOrAddTable(final String name) throws IOException {
        Table table = this.odsElements.getTable(name);
        if (table == null) {
            table = this.addTable(name);
        }
        return table;
    }


    @Override
    public String getTableName(final int n) throws FastOdsException {
        final Table t = this.getTable(n);
        return t.getName();
    }

    @Override
    public int getTableNumber(final String name) {
        final ListIterator<Table> iterator = this.odsElements.getTables().listIterator();
        while (iterator.hasNext()) {
            final int n = iterator.nextIndex();
            final Table tab = iterator.next();
            if (tab.getName().equals(name)) {
                return n;
            }
        }

        return -1;
    }

    @Override
    public List<Table> getTables() {
        return this.odsElements.getTables();
    }

    @Override
    public boolean setActiveTable(final int tableIndex) {
        if (tableIndex < 0 || tableIndex >= this.odsElements.getTableCount()) return false;

        final Table table = this.odsElements.getTable(tableIndex);
        this.odsElements.setActiveTable(table);
        return true;
    }

    @Override
    public void setViewSetting(final String viewId, final String item, final String value) {
        this.odsElements.setViewSetting(viewId, item, value);
    }

    @Override
    public int tableCount() {
        return this.odsElements.getTableCount();
    }

    @Override
    public void addAutofilter(final Table table, final int r1, final int c1, final int r2,
                              final int c2) {
        this.odsElements.addAutofilter(table, r1, c1, r2, c2);
    }
}
