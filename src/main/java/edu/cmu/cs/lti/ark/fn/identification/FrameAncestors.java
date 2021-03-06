package edu.cmu.cs.lti.ark.fn.identification;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author sthomson@cs.cmu.edu
 */
public class FrameAncestors {
	private static final String DEFAULT_ANCESTORS_FILE = "data/ancestors.csv";
	private static final String DEFAULT_PARENTS_FILE = "data/frame_parents.csv";
	public static final int PARENT = 1, ANCESTOR = 2; // mk: ancestors

	private static InputSupplier<InputStream> DEFAULT_ANCESTOR_SUPPLIER = new InputSupplier<InputStream>() {
		@Override public InputStream getInput() throws IOException {
			return getClass().getClassLoader().getResourceAsStream(DEFAULT_ANCESTORS_FILE);
		} };

	private static InputSupplier<InputStream> DEFAULT_PARENT_SUPPLIER = new InputSupplier<InputStream>() {
		@Override public InputStream getInput() throws IOException {
			return getClass().getClassLoader().getResourceAsStream(DEFAULT_PARENTS_FILE);
		} };

	private final Multimap<String, String> ancestors;

	public FrameAncestors(Multimap<String, String> ancestors) {
		this.ancestors = ancestors;
	}

	public static FrameAncestors load() throws IOException {
		return load(CharStreams.newReaderSupplier(DEFAULT_ANCESTOR_SUPPLIER, Charsets.UTF_8));
	}

	public static FrameAncestors load(int type) throws IOException {
		if(type == PARENT)
			return load(CharStreams.newReaderSupplier(DEFAULT_PARENT_SUPPLIER, Charsets.UTF_8));
		else
			return load(CharStreams.newReaderSupplier(DEFAULT_ANCESTOR_SUPPLIER, Charsets.UTF_8));
	}

	public static FrameAncestors load(InputSupplier<InputStreamReader> input) throws IOException {
		return new FrameAncestors(readCsv(input));
	}

	private static Multimap<String, String> readCsv(InputSupplier<InputStreamReader> input) throws IOException {
		final Multimap<String, String> ancestors = HashMultimap.create();
		final List<String> lines = CharStreams.readLines(input);
		for (String line : lines) {
			final String[] frames = line.split(",", 2);
			if (frames.length > 1) {
				ancestors.putAll(frames[0], Lists.newArrayList(frames[1].split(",")));
			}
		}
		return ancestors;
	}

	public Collection<String> getAncestors(String frame) {
		return ancestors.get(frame);
	}

	public Map<String, Collection<String>> getAllAncestors() {
		return ancestors.asMap();
	}

	public Collection<String> getAllParents() {
		return ancestors.values();
	}

}
