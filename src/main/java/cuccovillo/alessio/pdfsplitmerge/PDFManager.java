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

		findBookmarks();

		pageCount = doc.getNumberOfPages();

	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public int getPageCount() {
		return pageCount;
	}

	private void findBookmarks() throws BookmarkNotFoundException, IOException {
		bookmarks = new ArrayList<Bookmark>();
		PDDocumentOutline outline = doc.getDocumentCatalog().getDocumentOutline();
		if (outline == null) {
			throw new BookmarkNotFoundException();
		}
		PDOutlineItem current = outline.getFirstChild();
		while (current != null) {
			PDDestination destination = current.getDestination();
			if (destination instanceof PDNamedDestination) {
				PDPageDestination pd = doc.getDocumentCatalog()
						.findNamedDestinationPage((PDNamedDestination) destination);
				if (pd != null) {
					bookmarks.add(new Bookmark(current.getTitle(), pd));
				}
			} else if (destination instanceof PDPageDestination) {
				bookmarks.add(new Bookmark(current.getTitle(), (PDPageDestination) destination));
			}
			current = current.getNextSibling();
		}

	}
}
