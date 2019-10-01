package cuccovillo.alessio.pdfsplitmerge.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import cuccovillo.alessio.pdfsplitmerge.PDFManager;
import cuccovillo.alessio.pdfsplitmerge.exceptions.BookmarkNotFoundException;
import cuccovillo.alessio.pdfsplitmerge.i18n.I18NLoader;
import cuccovillo.alessio.pdfsplitmerge.model.Bookmark;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class Main {
	private JFrame frame;
	private JTextField textField;
	private JList<Bookmark> lstBookmarks;
	private JRadioButton rdbtnBookmarks;
	private JRadioButton rdbtnPageRange;
	private JButton btnMerge;
	private JButton btnSplit;
	private JList<File> lstFiles;
	private JButton btnLoadMerge;
	/**
	 * @wbp.nonvisual location=24,329
	 */
	private final JFileChooser fileChooser = new JFileChooser();

	private DefaultListModel<Bookmark> listModel;
	private File currentPath;
	private File currentPDF;
	private PDFManager pdfManager;
	private final static String MAIN_TITLE;

	static {
		MAIN_TITLE = I18NLoader.getString("main.title");
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
		currentPath = new File(System.getProperty("user.home"));
		currentPDF = null;
		pdfManager = new PDFManager();
	}

	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new PDFOnlyFileFilter());
		frame = new JFrame();
		BorderLayout borderLayout = (BorderLayout) frame.getContentPane().getLayout();
		borderLayout.setVgap(10);
		borderLayout.setHgap(10);
		frame.setTitle(MAIN_TITLE);
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

		JButton btnLoadSplit = new JButton(I18NLoader.getString("btnLoadSplit.text"));
		btnLoadSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choosePDF();
			}
		});
		pnlTabSplitTop.add(btnLoadSplit);

		btnSplit = new JButton(I18NLoader.getString("btnSplit.text"));
		btnSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				splitPdf();
			}
		});
		btnSplit.setEnabled(false);
		pnlTabSplitTop.add(btnSplit);

		JPanel pnlTabSplitCenter = new JPanel();
		pnlTabSplit.add(pnlTabSplitCenter, BorderLayout.CENTER);
		pnlTabSplitCenter.setLayout(new BorderLayout(0, 0));

		rdbtnBookmarks = new JRadioButton(I18NLoader.getString("rdbtnBookmarks.text"));
		rdbtnBookmarks.setSelected(true);
		pnlTabSplitCenter.add(rdbtnBookmarks, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		pnlTabSplitCenter.add(scrollPane);

		lstBookmarks = new JList<>();
		lstBookmarks.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				enableBtnSplitFromBookmarks();
			}
		});
		lstBookmarks.setModel(listModel);
		scrollPane.setViewportView(lstBookmarks);

		JPanel pnlTabSplitBottom = new JPanel();
		pnlTabSplit.add(pnlTabSplitBottom, BorderLayout.SOUTH);
		pnlTabSplitBottom.setLayout(new BorderLayout(0, 0));

		rdbtnPageRange = new JRadioButton(I18NLoader.getString("rdbtnPageRange"));
		pnlTabSplitBottom.add(rdbtnPageRange, BorderLayout.NORTH);

		textField = new JTextField();
		pnlTabSplitBottom.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);

		JPanel pnlTabMerge = new JPanel();
		tabbedPane.addTab(I18NLoader.getString("pnlTabMerge.text"), null, pnlTabMerge, null);
		pnlTabMerge.setLayout(new BorderLayout(0, 0));

		JPanel pnlTabMergeTop = new JPanel();
		pnlTabMerge.add(pnlTabMergeTop, BorderLayout.NORTH);

		btnLoadMerge = new JButton(I18NLoader.getString("btnLoadMerge.text"));
		pnlTabMergeTop.add(btnLoadMerge);

		btnMerge = new JButton(I18NLoader.getString("btnMerge.text"));
		btnMerge.setEnabled(false);
		pnlTabMergeTop.add(btnMerge);

		JPanel pnlTabMergeCenter = new JPanel();
		pnlTabMerge.add(pnlTabMergeCenter, BorderLayout.CENTER);
		pnlTabMergeCenter.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		pnlTabMergeCenter.add(scrollPane_1);

		lstFiles = new JList<>();
		scrollPane_1.setViewportView(lstFiles);
	}

	private void choosePDF() {
		fileChooser.setCurrentDirectory(currentPath);
		fileChooser.setSelectedFile(currentPDF);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			currentPath = fileChooser.getCurrentDirectory();
			currentPDF = fileChooser.getSelectedFile();
			try {
				extractBookmarks(currentPDF);
				if (listModel.getSize() == 0) {
					rdbtnBookmarks.setEnabled(false);
					rdbtnBookmarks.setSelected(false);
					rdbtnPageRange.setSelected(true);
				} else {
					rdbtnBookmarks.setEnabled(true);
					rdbtnBookmarks.setSelected(true);
					rdbtnPageRange.setSelected(false);
				}
				JOptionPane.showMessageDialog(frame, String.format(I18NLoader.getString("file.loaded.message"),
						listModel.getSize(), pdfManager.getPageCount()), MAIN_TITLE, JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
					e.printStackTrace(pw);
					ErrorPanel errorPanel = new ErrorPanel(sw.toString());
					JOptionPane.showMessageDialog(frame, errorPanel, MAIN_TITLE, JOptionPane.ERROR_MESSAGE);
				} catch (Exception e2) {
					// do nothing
				}
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
					String.format(I18NLoader.getString("BookmarkNotFoundException.message"), pdf.toString()),
					MAIN_TITLE, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void enableBtnSplitFromBookmarks() {
		if (!lstBookmarks.getValueIsAdjusting()) {
			if (lstBookmarks.getSelectedIndices().length > 0) {
				btnSplit.setEnabled(true);
			} else {
				btnSplit.setEnabled(false);
			}
		}
	}

	private void splitPdf() {
		JOptionPane.showMessageDialog(frame, "Hi!", MAIN_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}
}
