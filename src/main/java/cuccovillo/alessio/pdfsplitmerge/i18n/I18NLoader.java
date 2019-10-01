package cuccovillo.alessio.pdfsplitmerge.i18n;

import java.util.ResourceBundle;

public final class I18NLoader {
	private static final ResourceBundle I18N_BUNDLE;
	private static final I18NLoader INSTANCE;
	static {
		I18N_BUNDLE = ResourceBundle.getBundle("cuccovillo.alessio.pdfsplitmerge.i18n.pdfsplitandmerge");
		INSTANCE = new I18NLoader();
	}

	private I18NLoader() {
	}

	public static I18NLoader getInstance() {
		return INSTANCE;
	}

	public static String getString(String key) {
		return I18N_BUNDLE.getString(key);
	}
}
