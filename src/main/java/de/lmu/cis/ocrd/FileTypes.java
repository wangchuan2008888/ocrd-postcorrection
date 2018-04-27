package de.lmu.cis.ocrd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.lmu.cis.ocrd.parsers.*;
import org.pmw.tinylog.Logger;

import de.lmu.cis.ocrd.archive.Archive;
import de.lmu.cis.ocrd.archive.DirectoryArchive;
import de.lmu.cis.ocrd.archive.Entry;
import de.lmu.cis.ocrd.archive.ZipArchive;

public class FileTypes {
		public enum ArchiveType {
				ZIP, DIR //, FILE, METS
		}
		// order of types is used to breaks ties:
		// PAGEXML is preferred, then ALTOXML ...
		public enum OCRType {
				PAGEXML, ALTOXML, ABBYY, OCROPUS, HOCR
		}
		public static class Type {
				private final ArchiveType archiveType;
				private final OCRType ocrType;
				public Type(ArchiveType archiveType, OCRType ocrType) {
						this.archiveType = archiveType;
						this.ocrType = ocrType;
				}
				public ArchiveType getArchiveType() {
						return this.archiveType;
				}
				public OCRType getOCRType() {
						return this.ocrType;
				}
		}

		public static Type guess(String path) throws Exception {
				if (path.endsWith(".zip") || path.endsWith(".ZIP")) {
						Logger.debug("{} is a zip archive", path);
						return guess(ArchiveType.ZIP, path);
				}
				if (Files.isDirectory(Paths.get(path))) {
						Logger.debug("{} is a directory", path);
						return guess(ArchiveType.DIR, path);
				}
				throw new Exception("cannot automatically determine type of file: " + path);
		}

		public static Type guess(ArchiveType archiveType, String path) throws Exception {
				try(final Archive ar = newArchive(archiveType, path)) {
						final TreeMap<OCRType, Integer> counts = new TreeMap<OCRType, Integer>(); // use ordered map to break ties
						final Map<OCRType, XMLFileType> types = newOCRTypeMap();
						for (Entry entry : ar) {
								updateCounts(counts, types, entry);
						}
					return guess(archiveType, counts);
				}
		}

		public static Document openDocument(String path) throws Exception {
		    return openDocument(guess(path), path);
        }

		public static Document openDocument(Type type, String path) throws Exception {
		    try (final Archive ar = newArchive(type.getArchiveType(), path)) {
		        return newArchiveParser(type, ar).parse();
            }
        }

		private static Type guess(ArchiveType archiveType, TreeMap<OCRType, Integer> counts) throws Exception {
			int max = -1;
			OCRType argMax = OCRType.PAGEXML;
			for (Map.Entry<OCRType, Integer> entry : counts.entrySet()) {
				Logger.debug("{}: {}", entry.getKey(), entry.getValue());
				if (entry.getValue() > max) {
					max = entry.getValue();
					argMax = entry.getKey();
				}
			}
			if (max <= 0) {
				throw new Exception("cannot determine type of archive: no usable files");
			}
			return new Type(archiveType, argMax);
		}

		private static void updateCounts(Map<OCRType, Integer> counts, Map<OCRType, XMLFileType> types, Entry entry) {
				for (Map.Entry<OCRType, XMLFileType> type : types.entrySet()) {
						if (type.getValue().check(entry.getName().toString())) {
								Logger.debug("found file {} of type {}", entry.getName(), type.getKey());
								if (!counts.containsKey(type.getKey())) {
										counts.put(type.getKey(), 0);
								}
								final int n = counts.get(type.getKey());
								counts.put(type.getKey(), n+1);
						}
				}
		}
		private static Map<OCRType, XMLFileType> newOCRTypeMap() {
				Map<OCRType, XMLFileType> map = new HashMap<OCRType, XMLFileType>();
				for (OCRType t : OCRType.values()) {
						map.put(t, newXMLFileType(t));
				}
				return map;
		}

		public static Parser newArchiveParser(Type type, Archive ar) throws Exception {
		    if (type.getOCRType() == OCRType.OCROPUS) {
		        return new OcropusArchiveParser(ar);
            }
            final OCRType ocrType = type.getOCRType();
            return new ArchiveParser(newXMLParserFactory(ocrType), newXMLFileType(ocrType), ar);
        }

		public static XMLParserFactory newXMLParserFactory(OCRType ocrType) throws Exception {
            switch(ocrType) {
                case PAGEXML:
                    return new PageXMLParserFactory();
                case ALTOXML:
                    return new ALTOXMLParserFactory();
                case ABBYY:
                    return new ABBYYXMLParserFactory();
                case HOCR:
                    return new HOCRParserFactory();
            }
            throw new Exception("invalid OCR type: " + ocrType);
        }

		public static XMLFileType newXMLFileType(OCRType ocrType) {
				switch(ocrType) {
				case PAGEXML:
						return new PageXMLFileType();
				case ALTOXML:
						return new ALTOXMLFileType();
				case ABBYY:
						return new ABBYYXMLFileType();
				case OCROPUS:
						return new OcropusFileType();
				case HOCR:
						return new HOCRFileType();
				}
				throw new RuntimeException("non-exhaustive switch");
		}

		public static Archive newArchive(ArchiveType archiveType, String path) throws IOException {
				switch (archiveType) {
				case ZIP:
						return new ZipArchive(path);
				case DIR:
						return new DirectoryArchive(path);
				}
			throw new RuntimeException("non-exhaustive switch");
		}
}