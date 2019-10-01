package cuccovillo.alessio.pdfsplitmerge;

import java.awt.EventQueue;

import cuccovillo.alessio.pdfsplitmerge.gui.Main;

public class PdfSplitAndMerge {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
