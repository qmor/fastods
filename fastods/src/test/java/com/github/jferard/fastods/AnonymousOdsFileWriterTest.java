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

import com.github.jferard.fastods.odselement.OdsElements;
import com.github.jferard.fastods.style.PageStyle;
import com.github.jferard.fastods.style.TableCellStyle;
import com.github.jferard.fastods.style.TableColumnStyle;
import com.github.jferard.fastods.style.TableRowStyle;
import com.github.jferard.fastods.style.TableStyle;
import com.github.jferard.fastods.util.WriteUtil;
import com.github.jferard.fastods.util.XMLUtil;
import com.github.jferard.fastods.util.ZipUTF8Writer;
import com.github.jferard.fastods.util.ZipUTF8WriterBuilder;
import com.google.common.collect.Sets;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.easymock.PowerMock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 */
public class AnonymousOdsFileWriterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ZipUTF8WriterBuilder builder;

    private Logger logger;
    private OdsElements odsElements;
    private OdsFactory odsFactory;
    private ByteArrayOutputStream os;
    private WriteUtil writeUtil;
    private ZipUTF8Writer writer;
    private XMLUtil xmlUtil;

    private void initOdsElements() {
        TableStyle.DEFAULT_TABLE_STYLE.addToElements(this.odsElements);
        TableRowStyle.DEFAULT_TABLE_ROW_STYLE.addToElements(this.odsElements);
        TableColumnStyle.DEFAULT_TABLE_COLUMN_STYLE.addToElements(this.odsElements);
        TableCellStyle.DEFAULT_CELL_STYLE.addToElements(this.odsElements);
        PageStyle.DEFAULT_PAGE_STYLE.addToElements(this.odsElements);
    }

    @Before
    public final void setUp() {
        this.logger = PowerMock.createNiceMock(Logger.class);
        this.os = new ByteArrayOutputStream();
        this.writer = PowerMock.createMock(ZipUTF8Writer.class);
        this.xmlUtil = XMLUtil.create();
        this.odsElements = PowerMock.createMock(OdsElements.class);
        this.builder = PowerMock.createMock(ZipUTF8WriterBuilder.class);
        this.odsFactory = OdsFactory.create(this.logger, Locale.US);
    }

    @Test(expected = IOException.class)
    public final void testFileIsDir() throws IOException {
        final Logger l = PowerMock.createNiceMock(Logger.class);
        final OdsFactory of = OdsFactory.create(l, Locale.US);

        PowerMock.resetAll();
        this.initOdsElements();

        PowerMock.replayAll();
        final NamedOdsDocument document = this.getNamedDocument();
        of.createWriter().saveAs(".");

        PowerMock.verifyAll();
    }

    @Test
    public final void testSaveEmpyDocumentToOutputStream() throws IOException {
        final AnonymousOdsFileWriter writer = this.odsFactory.createWriter();
        final OdsDocument document = writer.document();

        PowerMock.resetAll();
        PowerMock.replayAll();
        writer.save(this.os);

        PowerMock.verifyAll();
        final InputStream is = new ByteArrayInputStream(this.os.toByteArray());
        final ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry = zis.getNextEntry();
        final Set<String> names = new HashSet<String>();
        while (entry != null) {
            names.add(entry.getName());
            entry = zis.getNextEntry();
        }

        Assert.assertEquals(Sets.newHashSet("settings.xml", "Configurations2/images/Bitmaps/",
                "Configurations2/toolbar/", "META-INF/manifest.xml", "Thumbnails/",
                "Configurations2/floater/", "Configurations2/menubar/", "mimetype", "meta.xml",
                "Configurations2/accelerator/current.xml", "Configurations2/popupmenu/",
                "styles.xml", "content.xml", "Configurations2/progressbar/",
                "Configurations2/statusbar/"), names);
    }

    @Test
    public final void testSaveWriter() throws IOException {
        final ZipUTF8WriterBuilder zb = PowerMock.createMock(ZipUTF8WriterBuilder.class);
        final ZipUTF8Writer z = PowerMock.createMock(ZipUTF8Writer.class);
        final File temp = File.createTempFile("tempfile", ".tmp");

        PowerMock.resetAll();
        this.initOdsElements();
        EasyMock.expect(zb.build(EasyMock.isA(FileOutputStream.class))).andReturn(z);
        this.odsElements.writeMeta(this.xmlUtil, z);
        this.odsElements.writeStyles(this.xmlUtil, z);
        this.odsElements.writeContent(this.xmlUtil, z);
        this.odsElements.writeSettings(this.xmlUtil, z);
        z.close();

        PowerMock.replayAll();
        final NamedOdsDocument document = this.getNamedDocument();
        final NamedOdsFileWriter writer = new OdsFileWriterBuilder(this.logger, document)
                .zipBuilder(zb).filename(temp.getAbsolutePath()).build();
        writer.save();

        PowerMock.verifyAll();
    }

    private NamedOdsDocument getNamedDocument() {
        return NamedOdsDocument.create(this.logger, this.xmlUtil, this.odsElements);
    }

    private AnonymousOdsDocument getAnonymousDocument() {
        return AnonymousOdsDocument.create(this.logger, this.xmlUtil, this.odsElements);
    }

    @Test // (expected = IOException.class)
    public final void testSaveWriterWithException() throws IOException {
        final OutputStream outputStream = PowerMock.createMock(OutputStream.class);

        PowerMock.resetAll();
        this.initOdsElements();
        this.odsElements.createEmptyElements(EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeImmutableElements(EasyMock.eq(this.xmlUtil),
                EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeMeta(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeStyles(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeContent(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements
                .writeSettings(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        outputStream.write(EasyMock.anyObject(byte[].class), EasyMock.anyInt(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        outputStream.flush();
        EasyMock.expectLastCall().anyTimes();
        outputStream.close();

        PowerMock.replayAll();
        final AnonymousOdsDocument document = this.getAnonymousDocument();
        new AnonymousOdsFileWriter(this.logger, document).save(outputStream);

        PowerMock.verifyAll();
    }

    @Test(expected = IOException.class)
    public final void testSaveAsNullFile() throws IOException {
        final OutputStream outputStream = PowerMock.createMock(OutputStream.class);

        PowerMock.resetAll();
        this.initOdsElements();

        PowerMock.replayAll();
        final AnonymousOdsDocument document = this.getAnonymousDocument();
        new AnonymousOdsFileWriter(this.logger, document).saveAs((File) null);

        PowerMock.verifyAll();
    }

    @Test
    public final void testSaveWithWriter() throws IOException {
        final OutputStream outputStream = PowerMock.createMock(OutputStream.class);
        final ZipUTF8Writer z = PowerMock.createMock(ZipUTF8Writer.class);

        PowerMock.resetAll();
        this.initOdsElements();
        EasyMock.expect(this.builder.build(EasyMock.isA(FileOutputStream.class))).andReturn(z);

        this.odsElements.createEmptyElements(EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeImmutableElements(EasyMock.eq(this.xmlUtil),
                EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeMeta(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeStyles(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements.writeContent(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        this.odsElements
                .writeSettings(EasyMock.eq(this.xmlUtil), EasyMock.isA(ZipUTF8Writer.class));
        outputStream.write(EasyMock.anyObject(byte[].class), EasyMock.anyInt(), EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();
        outputStream.flush();
        EasyMock.expectLastCall().anyTimes();
        z.close();

        PowerMock.replayAll();
        final AnonymousOdsDocument document = this.getAnonymousDocument();
        new AnonymousOdsFileWriter(this.logger, document).saveAs("test", this.builder);

        PowerMock.verifyAll();
    }

    @Test(expected = IOException.class)
    public final void testSaveWithWriterDir() throws IOException {
        final OutputStream outputStream = PowerMock.createMock(OutputStream.class);
        final ZipUTF8Writer z = PowerMock.createMock(ZipUTF8Writer.class);

        PowerMock.resetAll();
        this.initOdsElements();

        PowerMock.replayAll();
        final AnonymousOdsDocument document = this.getAnonymousDocument();
        new AnonymousOdsFileWriter(this.logger, document).saveAs(".", this.builder);

        PowerMock.verifyAll();
    }
}