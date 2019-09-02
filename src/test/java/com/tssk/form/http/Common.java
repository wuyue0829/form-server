package com.tssk.form.http;

public class Common {

	public static final String token = "275d149f-dd72-4138-9d0c-bc38cc944cc9";

	public static final String urlPreLocal = "http://127.0.0.1:6158/rest/";

	public static final String urlPreServer = "http://192.168.1.175:82/form/rest/";

	public static final boolean isLocal = true;

	public static String getUrlPre() {
		if (isLocal) { 
			return urlPreLocal;
		} else {
			return urlPreServer;
		}
	}

}
