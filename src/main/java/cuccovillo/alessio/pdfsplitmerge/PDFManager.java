package cuccovillo.alessio.pdfsplitmerge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import cuccovillo.alessio.pdfsplitmerge.exceptions.BookmarkNotFoundException;
import cuccovillo.alessio.pdfsplitmerge.model.Bookmark;

public class PDFManager {
	private PDDocument doc;
	private List<Bookmark> bookmarks;
	private int pageCount;

	public void load(File pdf) throws IOException, BookmarkNotFoundException {
		doc = PDDocument.load(pdf);
		pageCount = doc.getNumberOfPages();
		bookmarks = findBookmarks();
		doc.close();
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public int getPageCount() {
		return pageCount;
	}

	private List<Bookmark> findBookmarks() throws IOException {
		List<Bookmark> bookmarks = new ArrayList<>();
		PDDocumentOutline outline = doc.getDocumentCatalog().getDocumentOutline();
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
			pd = doc.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) destination);
		} else if (destination instanceof PDPageDestination) {
			pd = (PDPageDestination) destination;
		}
		if (pd == null) {
			throw new BookmarkNotFoundException();
		}
		return pd;
	}
}
