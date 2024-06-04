/**
 * Copyright 2014 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.data.writer;

import java.io.OutputStream;

/**
 * Abstract {@link IDataModelWriter} that outputs model data to an {@link OutputStream}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public abstract class AbstractDataModelWriter implements IDataModelWriter {

	protected OutputStream out;

	public AbstractDataModelWriter(OutputStream out) {
		setOutputStream(out);
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

}
