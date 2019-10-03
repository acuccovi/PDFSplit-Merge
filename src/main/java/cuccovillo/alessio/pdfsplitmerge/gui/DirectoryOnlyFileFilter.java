package cuccovillo.alessio.pdfsplitmerge.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import cuccovillo.alessio.pdfsplitmerge.i18n.I18NLoader;

public class DirectoryOnlyFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		return f.isDirectory();
	}

	@Override
	public String getDescription() {
		return I18NLoader.getString("directoryfilter.description");
	}

}
