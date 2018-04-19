package de.lmu.cis.ocrd.archive;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface Archive extends Closeable, Iterable<Entry> {
		public Path getName();

		public InputStream open(Entry entry) throws IOException;
}
