package cuccovillo.alessio.pdfsplitmerge.model;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class Bookmark {
	private String title;
	private int firstPage;
	private int lastPage;

	public Bookmark(String title, PDPageDestination destination) {
		this.title = title;
		this.firstPage = destination.retrievePageNumber();
		this.lastPage = -1;
	}

	public Bookmark(String title, int firstPage, int lastPage) {
		this.title = title;
		this.firstPage = firstPage;
		this.lastPage = lastPage;
	}

	public String getTitle() {
		return title;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	@Override
	public String toString() {
		return String.format("%s [%s page(s)]", getTitle(), getLastPage() - getFirstPage());
	}

}
