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

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The OdsFileWriterAdapter class represents an adapter to a writer. It stores a queue of flushers. Usage:
 * <ul>
 * <li>A producer thread that writes on a OdsFileWriterAdapter.document()</li>
 * <li>A consumer thread that uses the following stucture</li>
 * </ul>
 *
 * <pre>
 * while (this.writerAdapter.isNotStopped()) {
 *     this.writerAdapter.waitForData();
 *     this.writerAdapter.flushAdaptee();
 * }
 * this.writerAdapter.flushAdaptee();
 * </pre>
 *
 * @author Julien Férard
 * @author Martin Schulz
 */
public class OdsFileWriterAdapter implements OdsFileWriter {
	private final OdsFileWriter adaptee;
	private boolean stopped;
	private final Queue<OdsFlusher> flushers;
	private boolean producerAlive;

	public static OdsFileWriterAdapter create(final OdsFileWriter adaptee) {
		return new OdsFileWriterAdapter(adaptee, new LinkedList<OdsFlusher>());
	}

	OdsFileWriterAdapter(final OdsFileWriter adaptee, final Queue<OdsFlusher> flushers) {
		this.adaptee = adaptee;
		this.flushers = flushers;
		this.producerAlive = true;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public OdsDocument document() {
		return this.adaptee.document();
	}

	@Override
	public synchronized void save() throws IOException {
	}

	@Override
	public synchronized void update(final OdsFlusher flusher) throws IOException {
		this.flushers.add(flusher);
		if (flusher instanceof FinalizeFlusher)
			this.producerAlive = false;
		this.notifyAll();
	}

	public synchronized void flushAdaptee() throws IOException {
		OdsFlusher flusher = this.flushers.poll();
		if (flusher == null)
			return;

		while (flusher != null) {
			this.adaptee.update(flusher);
			if (flusher instanceof FinalizeFlusher) {
				this.stopped = true;
				break;
			}
			if (this.producerAlive) {
				try {
					this.wait();
				} catch (final InterruptedException e) {
				}
			}
			this.notifyAll(); //
			flusher = this.flushers.poll();
		}
		if (this.stopped)
			this.adaptee.save();
		this.notifyAll(); // nobody is waiting
	}

	public synchronized boolean isNotStopped() {
		return !this.stopped;
	}

	public synchronized void waitForData() {
		while (this.flushers.isEmpty() && this.isNotStopped()) {
			try {
				this.wait();
			} catch (final InterruptedException e) {
			}
		}
	}

}
