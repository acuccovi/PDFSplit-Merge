package cuccovillo.alessio.pdfsplitmerge.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea;

	/**
	 * Create the panel.
	 */
	public ErrorPanel(String message) {
		setPreferredSize(new Dimension(640, 400));
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		textArea.setText(message);
		textArea.setCaretPosition(0);
	}

}
