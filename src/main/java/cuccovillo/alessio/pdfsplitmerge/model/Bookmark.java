package cuccovillo.alessio.pdfsplitmerge.model;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class Bookmark {
	private String title;
	private PDPageDestination destination;

	public Bookmark(String title, PDPageDestination destination) {
		this.title = title;
		this.destination = destination;
	}

	public String getTitle() {
		return title;
	}

	public PDPageDestination getDestination() {
		return destination;
	}

	public int getDestinationPage() {
		return destination.retrievePageNumber();
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
