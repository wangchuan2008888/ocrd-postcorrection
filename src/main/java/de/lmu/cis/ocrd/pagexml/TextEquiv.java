package de.lmu.cis.ocrd.pagexml;

import de.lmu.cis.ocrd.util.Normalizer;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

public class TextEquiv {
	private final Node node;

	public TextEquiv(Node node) {
		this.node = node;
	}

	public int getIndex() {
		return Integer.parseInt(XPathHelper.getAttribute(node, "index").orElse("0"));
	}

	public double getConfidence() {
		return Double.parseDouble(XPathHelper.getAttribute(node, "conf").orElse("0.0"));
	}

	public String getDataType() {
		return XPathHelper.getAttribute(node, "dataType").orElse("");
	}

	public String getDataTypeDetails() {
		return XPathHelper.getAttribute(node, "dataTypeDetails").orElse("");
	}

	public String getUnicode() {
		try {
			final Node n = XPathHelper.getNode(node, "./Unicode");
			return n.getFirstChild().getTextContent();
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	public String getUnicodeNormalized() {
		return Normalizer.normalize(getUnicode());
	}
}
