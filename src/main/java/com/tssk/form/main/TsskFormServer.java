package com.tssk.form.main;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.tssk.fw.boot.TsskMain;

public class TsskFormServer {

	public static void main(String[] args) {
		URL url = Thread.currentThread().getContextClassLoader().getResource("my-log4j2.xml");
		if (url != null) {
			ConfigurationSource source;
			try {
				source = new ConfigurationSource(url.openStream(), url);
				Configurator.initialize(null, source);
			} catch (IOException e) {
			}
		}
		TsskMain.main(args);
	}
}
