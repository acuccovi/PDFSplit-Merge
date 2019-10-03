package cuccovillo.alessio.pdfsplitmerge.pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import cuccovillo.alessio.pdfsplitmerge.exceptions.BookmarkNotFoundException;
import cuccovillo.alessio.pdfsplitmerge.model.Bookmark;

public class PDFManager {
	private PDDocument document;
	private List<Bookmark> bookmarks;
	private int pageCount;

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void load(File pdf) throws IOException, BookmarkNotFoundException {
		close();
		document = PDDocument.load(pdf);
		pageCount = document.getNumberOfPages();
		bookmarks = findBookmarks();
	}

	public void close() throws IOException {
		if (document != null) {
			document.close();
		}
	}

	public void split(File path, List<Bookmark> bookmarks) throws IOException {
		int i = 1;
		for (Bookmark bookmark : bookmarks) {
			String title = bookmark.getTitle().replaceAll("[<>:\"/\\|?*]", "_");
			String fileName = i + " - " + title + ".pdf";
			String outPath = Paths.get(path.toString(), fileName).toString();
			int start = bookmark.getFirstPage() == bookmark.getLastPage() ? bookmark.getFirstPage()
					: bookmark.getFirstPage() + 1;
			PageExtractor pe = new PageExtractor(document, start, bookmark.getLastPage());
			PDDocument outDoc = pe.extract();
			optimize(outDoc);
			outDoc.save(outPath);
			outDoc.close();
			i++;
		}
	}

	private List<Bookmark> findBookmarks() throws IOException {
		List<Bookmark> bookmarks = new ArrayList<>();
		PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
		try {
			if (outline == null) {
				throw new BookmarkNotFoundException();
			}
			PDOutlineItem current = outline.getFirstChild();
			while (current != null) {
				PDDestination destination = current.getDestination();
				PDPageDestination pd = findValidBookmark(destination);
				Bookmark bookmark = new Bookmark(current.getTitle(), pd);
				bookmarks.add(bookmark);
				if (bookmarks.size() > 1) {
					Bookmark previuos = bookmarks.get((bookmarks.size() - 1) - 1);
					if (previuos != null) {
						previuos.setLastPage(bookmark.getFirstPage());
					}
				}
				current = current.getNextSibling();
			}
			Bookmark last = bookmarks.get(bookmarks.size() - 1);
			last.setLastPage(getPageCount());
		} catch (BookmarkNotFoundException e) {
			// nothing to do!
		}
		return bookmarks;
	}

	private PDPageDestination findValidBookmark(PDDestination destination)
			throws IOException, BookmarkNotFoundException {
		PDPageDestination pd = null;
		if (destination instanceof PDNamedDestination) {
			pd = document.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) destination);
		} else if (destination instanceof PDPageDestination) {
			pd = (PDPageDestination) destination;
		}
		if (pd == null) {
			throw new BookmarkNotFoundException();
		}
		return pd;
	}

	private void optimize(PDDocument doc) {
		// https://stackoverflow.com/questions/53420344/how-to-reduce-the-size-of-merged-pdf-a-1b-files-with-pdfbox-or-other-java-librar
		Map<String, COSBase> fontFileCache = new HashMap<>();
		for (int pageNumber = 0; pageNumber < doc.getNumberOfPages(); pageNumber++) {
			final PDPage page = doc.getPage(pageNumber);
			COSDictionary pageDictionary = (COSDictionary) page.getResources().getCOSObject().getDictionaryObject(COSName.FONT);
			if (pageDictionary != null) {
				for (COSName currentFont : pageDictionary.keySet()) {
					COSDictionary fontDictionary = (COSDictionary) pageDictionary.getDictionaryObject(currentFont);
					for (COSName actualFont : fontDictionary.keySet()) {
						COSBase actualFontDictionaryObject = fontDictionary.getDictionaryObject(actualFont);
						if (actualFontDictionaryObject instanceof COSDictionary) {
							COSDictionary fontFile = (COSDictionary) actualFontDictionaryObject;
							if (fontFile.getItem(COSName.FONT_NAME) instanceof COSName) {
								COSName fontName = (COSName) fontFile.getItem(COSName.FONT_NAME);
								fontFileCache.computeIfAbsent(fontName.getName(), key -> fontFile.getItem(COSName.FONT_FILE2));
								fontFile.setItem(COSName.FONT_FILE2, fontFileCache.get(fontName.getName()));
							}
						}
					}
				}
			}
		}
	}
}
