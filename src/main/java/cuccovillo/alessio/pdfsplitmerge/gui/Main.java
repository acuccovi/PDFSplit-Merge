package cuccovillo.alessio.pdfsplitmerge.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import cuccovillo.alessio.pdfsplitmerge.PDFManager;
import cuccovillo.alessio.pdfsplitmerge.exceptions.BookmarkNotFoundException;
import cuccovillo.alessio.pdfsplitmerge.i18n.I18NLoader;
import cuccovillo.alessio.pdfsplitmerge.model.Bookmark;
import javax.swing.JTabbedPane;

public class Main {
	private JFrame frame;
	private JTextField textField;
	private JList<Bookmark> lstBookmarks;
	/**
	 * @wbp.nonvisual location=24,329
	 */
	private final JFileChooser fileChooser = new JFileChooser();

	private DefaultListModel<Bookmark> listModel;
	private File currentPath;
	private File currentPDF;
	private PDFManager pdfManager;

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
		currentPath = new File(System.getProperty("user.home"));
		currentPDF = null;
		pdfManager = new PDFManager();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new PDFOnlyFileFilter());
		frame = new JFrame();
		frame.setTitle(I18NLoader.getString("main.title")); //$NON-NLS-1$
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listModel = new DefaultListModel<>();

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel pnlTabSplit = new JPanel();
		tabbedPane.addTab(I18NLoader.getString("pnlTabSplit.text"), null, pnlTabSplit, null);
		pnlTabSplit.setLayout(new BorderLayout(0, 0));

		JPanel pnlTabSplitTop = new JPanel();
		pnlTabSplit.add(pnlTabSplitTop, BorderLayout.NORTH);

		JButton btnLoad = new JButton(I18NLoader.getString("btnLoad.text")); //$NON-NLS-1$
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choosePDF();
			}
		});
		pnlTabSplitTop.add(btnLoad);

		JButton btnSplit = new JButton(I18NLoader.getString("btnSplit.text"));
		pnlTabSplitTop.add(btnSplit);

		JPanel pnlTabSplitCenter = new JPanel();
		pnlTabSplit.add(pnlTabSplitCenter, BorderLayout.CENTER);
		pnlTabSplitCenter.setLayout(new BorderLayout(0, 0));

		JRadioButton rdbtnBookmarks = new JRadioButton(I18NLoader.getString("rdbtnBookmarks.text")); //$NON-NLS-1$
		rdbtnBookmarks.setSelected(true);
		pnlTabSplitCenter.add(rdbtnBookmarks, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		pnlTabSplitCenter.add(scrollPane);

		lstBookmarks = new JList<>();
		lstBookmarks.setModel(listModel);
		scrollPane.setViewportView(lstBookmarks);

		JPanel pnlTabSplitBottom = new JPanel();
		pnlTabSplit.add(pnlTabSplitBottom, BorderLayout.SOUTH);
		pnlTabSplitBottom.setLayout(new BorderLayout(0, 0));

		JRadioButton rdbtnPageRange = new JRadioButton(I18NLoader.getString("rdbtnPageRange")); //$NON-NLS-1$
		pnlTabSplitBottom.add(rdbtnPageRange, BorderLayout.NORTH);

		textField = new JTextField();
		pnlTabSplitBottom.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	private void choosePDF() {
		fileChooser.setCurrentDirectory(currentPath);
		fileChooser.setSelectedFile(currentPDF);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			currentPath = fileChooser.getCurrentDirectory();
			currentPDF = fileChooser.getSelectedFile();
			try {
				extractBookmarks(currentPDF);
				JOptionPane.showMessageDialog(frame, String.format(I18NLoader.getString("file.loaded.message"),
						listModel.getSize(), pdfManager.getPageCount()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void extractBookmarks(File pdf) throws FileNotFoundException, IOException {
		listModel.clear();
		try {
			pdfManager.load(pdf);
			for (Bookmark bookmark : pdfManager.getBookmarks()) {
				listModel.addElement(bookmark);
			}
		} catch (BookmarkNotFoundException bnfe) {
			JOptionPane.showMessageDialog(frame,
					String.format(I18NLoader.getString("BookmarkNotFoundException.message"), pdf.toString()));
		}
	}
}
