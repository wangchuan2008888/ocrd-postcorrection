package de.lmu.cis.ocrd.parsers;

public class PageXMLFileType implements XMLFileType {
	@Override
	public boolean check(String name) {
		return name.contains("page") || name.contains("PAGE");
	}
}