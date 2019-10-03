package cuccovillo.alessio.pdfsplitmerge.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cuccovillo.alessio.pdfsplitmerge.exceptions.BookmarkNotFoundException;
import cuccovillo.alessio.pdfsplitmerge.i18n.I18NLoader;
import cuccovillo.alessio.pdfsplitmerge.model.Bookmark;
import cuccovillo.alessio.pdfsplitmerge.pdf.PDFManager;

public class Main {
	private JFrame frame;
	private JTextField txtPageRange;
	private JList<Bookmark> lstBookmarks;
	private JList<File> lstFiles;
	private JRadioButton rdbtnBookmarks;
	private JRadioButton rdbtnPageRange;
	private JButton btnMerge;
	private JButton btnSplit;
	private JButton btnLoadMerge;
	/**
	 * @wbp.nonvisual location=24,329
	 */
	private final JFileChooser fileChooser = new JFileChooser();
	/**
	 * @wbp.nonvisual location=94,329
	 */
	private final JFileChooser directoryChooser = new JFileChooser();

	private DefaultListModel<Bookmark> bookmarkListModel;
	private DefaultListModel<File> mergeListModel;
	private File currentPath;
	private File currentPDF;
	private File currentOutPath;
	private PDFManager pdfManager;
	private final static String MAIN_TITLE;

	static {
		MAIN_TITLE = I18NLoader.getString("main.title");
	}

	/**
	 * Create the application.
	 */
	public Main() {
		bookmarkListModel = new DefaultListModel<>();
		mergeListModel = new DefaultListModel<File>();
		currentPath = new File(System.getProperty("user.home"));
		currentPDF = null;
		currentOutPath = null;
		pdfManager = new PDFManager();
		initialize();
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
		directoryChooser.setFileSelectionMode(1);
		directoryChooser.setAcceptAllFileFilterUsed(false);
		directoryChooser.setFileFilter(new DirectoryOnlyFileFilter());
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (pdfManager != null) {
					try {
						pdfManager.close();
					} catch (IOException ioe) {
						showError(ioe);
					}
				}
			}
		});
		BorderLayout borderLayout = (BorderLayout) frame.getContentPane().getLayout();
		borderLayout.setVgap(10);
		borderLayout.setHgap(10);
		frame.setTitle(MAIN_TITLE);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		rdbtnBookmarks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setActiveRadioButton(rdbtnBookmarks);
			}
		});
		rdbtnBookmarks.setEnabled(false);
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
		lstBookmarks.setModel(bookmarkListModel);
		scrollPane.setViewportView(lstBookmarks);

		JPanel pnlTabSplitBottom = new JPanel();
		pnlTabSplit.add(pnlTabSplitBottom, BorderLayout.SOUTH);
		pnlTabSplitBottom.setLayout(new BorderLayout(0, 0));

		rdbtnPageRange = new JRadioButton(I18NLoader.getString("rdbtnPageRange"));
		rdbtnPageRange.setEnabled(false);
		rdbtnPageRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setActiveRadioButton(rdbtnPageRange);
			}
		});
		pnlTabSplitBottom.add(rdbtnPageRange, BorderLayout.NORTH);

		txtPageRange = new JTextField();
		txtPageRange.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				enableBtnSplitFromPageRange();
			}
		});
		txtPageRange.setEnabled(false);
		pnlTabSplitBottom.add(txtPageRange, BorderLayout.CENTER);
		txtPageRange.setColumns(10);

		JPanel pnlTabMerge = new JPanel();
		tabbedPane.addTab(I18NLoader.getString("pnlTabMerge.text"), null, pnlTabMerge, null);
		pnlTabMerge.setLayout(new BorderLayout(0, 0));

		JPanel pnlTabMergeTop = new JPanel();
		pnlTabMerge.add(pnlTabMergeTop, BorderLayout.NORTH);

		btnLoadMerge = new JButton(I18NLoader.getString("btnLoadMerge.text"));
		btnLoadMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choosePDFs();
			}
		});
		pnlTabMergeTop.add(btnLoadMerge);

		btnMerge = new JButton(I18NLoader.getString("btnMerge.text"));
		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mergeFiles();
			}
		});
		btnMerge.setEnabled(false);
		pnlTabMergeTop.add(btnMerge);

		JPanel pnlTabMergeCenter = new JPanel();
		pnlTabMerge.add(pnlTabMergeCenter, BorderLayout.CENTER);
		pnlTabMergeCenter.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		pnlTabMergeCenter.add(scrollPane_1);

		lstFiles = new JList<>();
		lstFiles.setModel(mergeListModel);
		scrollPane_1.setViewportView(lstFiles);
	}

	// SPLIT
	private void setActiveRadioButton(JRadioButton component) {
		rdbtnPageRange.setEnabled(true);
		if (component.equals(rdbtnBookmarks)) {
			rdbtnBookmarks.setEnabled(true);
			rdbtnBookmarks.setSelected(true);
			rdbtnPageRange.setSelected(false);
			txtPageRange.setEnabled(false);
			enableBtnSplitFromBookmarks();
		} else if (component.equals(rdbtnPageRange)) {
			if (bookmarkListModel.getSize() == 0) {
				rdbtnBookmarks.setEnabled(false);
			}
			rdbtnBookmarks.setSelected(false);
			rdbtnPageRange.setSelected(true);
			txtPageRange.setEnabled(true);
			enableBtnSplitFromPageRange();
		}
	}

	private void choosePDF() {
		fileChooser.setCurrentDirectory(currentPath);
		fileChooser.setSelectedFile(currentPDF);
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			currentPath = fileChooser.getCurrentDirectory();
			currentPDF = fileChooser.getSelectedFile();
			try {
				extractBookmarks(currentPDF);
				if (bookmarkListModel.getSize() == 0) {
					setActiveRadioButton(rdbtnPageRange);
				} else {
					setActiveRadioButton(rdbtnBookmarks);
				}
				showMessage(String.format(I18NLoader.getString("file.loaded.message"), bookmarkListModel.getSize(), pdfManager.getPageCount()));
			} catch (IOException e) {
				showError(e);
			}
		}
	}

	private void extractBookmarks(File pdf) throws FileNotFoundException, IOException {
		bookmarkListModel.clear();
		try {
			pdfManager.load(pdf);
			for (Bookmark bookmark : pdfManager.getBookmarks()) {
				bookmarkListModel.addElement(bookmark);
			}
		} catch (BookmarkNotFoundException bnfe) {
			showMessage(String.format(I18NLoader.getString("BookmarkNotFoundException.message"), pdf.toString()));
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

	private void enableBtnSplitFromPageRange() {
		if (txtPageRange.getText().trim().length() > 0) {
			btnSplit.setEnabled(true);
		} else {
			btnSplit.setEnabled(false);
		}
	}

	private void splitPdf() {
		try {
			directoryChooser.setCurrentDirectory(currentOutPath);
			if (rdbtnPageRange.isSelected()) {
				if (txtPageRange.getText().trim().length() == 0)
					throw new Exception("Pattern not valid!");
			}
			if (currentOutPath == null) {
				currentOutPath = currentPath;
			}
			if (directoryChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				currentOutPath = directoryChooser.getSelectedFile();
				List<Bookmark> bookmarks = new ArrayList<>();
				if (rdbtnBookmarks.isSelected()) {
					bookmarks.addAll(lstBookmarks.getSelectedValuesList());
				} else if (rdbtnPageRange.isSelected()) {
					String[] p1 = txtPageRange.getText().trim().split(",");
					for (String s : p1) {
						String[] p2 = s.split("-");
						int firstPage = Integer.parseInt(p2[0]);
						int lastPage = firstPage;
						if (p2.length == 2) {
							lastPage = Integer.parseInt(p2[1]);
						}
						String title = String.format("%s [%s-%s]", currentPDF.getName(), firstPage, lastPage);
						Bookmark bookmark = new Bookmark(title, firstPage, lastPage);
						bookmarks.add(bookmark);
					}
				}
				Map<Integer, byte[]> pdfs = pdfManager.split(bookmarks);
				for (int id : pdfs.keySet()) {
					String title = PDFManager.convertTitleToFileName(bookmarks.get(id).getTitle());
					String fileName = String.format("%s - %s.pdf", id, title);
					String out = Paths.get(currentOutPath.toString(), fileName).toString();
					try (FileOutputStream fos = new FileOutputStream(out)) {
						fos.write(pdfs.get(id));
					}
				}
				showMessage(I18NLoader.getString("process.done.message"));
			}
		} catch (Exception e) {
			showError(e);
		}
	}

	// MERGE
	private void choosePDFs() {
		fileChooser.setCurrentDirectory(currentPath);
		fileChooser.setSelectedFile(currentPDF);
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			mergeListModel.clear();
			currentPath = fileChooser.getCurrentDirectory();
			for (File pdf : fileChooser.getSelectedFiles()) {
				mergeListModel.addElement(pdf);
			}
		}
		if (mergeListModel.getSize() < 2) {
			btnMerge.setEnabled(false);
		} else {
			btnMerge.setEnabled(true);
		}
	}

	private void mergeFiles() {
		try {
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setSelectedFile(null);
			if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				List<File> files = new ArrayList<>(mergeListModel.getSize());
				for (int i = 0; i < mergeListModel.getSize(); i++) {
					files.add(mergeListModel.get(i));
				}
				byte[] pdf = pdfManager.merge(files);
				try (FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile())) {
					fos.write(pdf);
				}
				showMessage(I18NLoader.getString("process.done.message"));
			}
		} catch (Exception e) {
			showError(e);
		}
	}
	
	// COMMON
	private void showError(Throwable t) {
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);) {
			t.printStackTrace(pw);
			ErrorPanel errorPanel = new ErrorPanel(sw.toString());
			JOptionPane.showMessageDialog(frame, errorPanel, MAIN_TITLE, JOptionPane.ERROR_MESSAGE);
		} catch (Exception e2) {
			// do nothing
		}
	}
	
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(frame, message, MAIN_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}
}
