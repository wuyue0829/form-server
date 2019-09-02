package com.tssk.form.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;


public class HttpUtils {


	public static String doHandler(String busiService, Map<String, Object> param) {
		HttpURLConnection connection = null;
		try {
			URL urlObj = new URL(Common.getUrlPre() + busiService);
			connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("token", Common.token);
			connection.connect();
			Set<String> keys = param.keySet();
			StringBuffer sb = new StringBuffer();
			sb.append("1=1");
			for (String key : keys) {
				sb.append("&");
				sb.append(key);
				sb.append("=");
				sb.append(tirmNull(param.get(key)));
			}
			connection.getOutputStream().write(sb.toString().getBytes("UTF-8"));
			InputStream in = connection.getInputStream();
			byte[] data = readContent(in);
			String dataStr = new String(data, "UTF-8");
			return dataStr;
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Throwable t) {

				}
			}
		}
		return "";
	}

	private static String tirmNull(Object str) {
		if (str == null) {
			return "";
		}
		if (str instanceof String) {
			return ((String) str).trim();
		}
		return str.toString();
	}

	private static byte[] readContent(InputStream content) throws IOException {
		if (content == null) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];

		while (true) {
			final int read = content.read(buff);
			if (read == -1) {
				break;
			}
			outputStream.write(buff, 0, read);
		}

		return outputStream.toByteArray();
	}
}
