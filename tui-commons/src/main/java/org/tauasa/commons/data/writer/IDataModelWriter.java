/**
 * Copyright 2014 Tauasa Timoteo. All rights reserved.
 */
package org.tauasa.commons.data.writer;

import org.tauasa.commons.data.DataModelException;
import org.tauasa.commons.data.IDataModel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Writes a {@link IDataModel}
 *
 * @author <a href="mailto:tauasa@gmail.com?subject=Tui Java API">tauasa@gmail.com</a>
 * 
 */
public interface IDataModelWriter {

	/**
	 * Writes the specified {@link IDataModel}, typically to an underlying {@link OutputStream} or {@link Writer}
	 * */
	public void write(IDataModel model)throws DataModelException, IOException;

}
