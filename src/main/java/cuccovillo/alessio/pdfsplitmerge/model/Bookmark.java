package cuccovillo.alessio.pdfsplitmerge.model;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class Bookmark {
	private String title;
	private PDPageDestination destination;
	private int lastPage;

	public Bookmark(String title, PDPageDestination destination) {
		this.title = title;
		this.destination = destination;
		lastPage = -1;
	}

	public String getTitle() {
		return title;
	}

	public PDPageDestination getDestination() {
		return destination;
	}

	public int getFirstPage() {
		return destination.retrievePageNumber();
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
