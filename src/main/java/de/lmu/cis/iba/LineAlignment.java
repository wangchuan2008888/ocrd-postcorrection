package de.lmu.cis.iba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import de.lmu.cis.ocrd.Document;
import de.lmu.cis.ocrd.OCRLine;

public class LineAlignment extends ArrayList<ArrayList<OCRLine>> {
	static ArrayList<Node> sinks = new ArrayList<Node>();
	public ArrayList<String> stringset = new ArrayList<String>();

	private static class pair {
		public Node node;
		public HashSet<Integer> ids;
	}

	public LineAlignment(Document doc, int nlines) throws Exception {
		super();

		if (nlines <= 0) {
			throw new Exception("cannot allign " + nlines + " lines");
		}

		ArrayList<String> stringset = new ArrayList<String>();
		ArrayList<OCRLine> ocrlines = new ArrayList<OCRLine>();

		doc.eachLine(new Document.Visitor() {
			@Override
			public void visit(OCRLine l) throws Exception {
				stringset.add("#" + l.line.getNormalized() + "$");
				ocrlines.add(l);
			}
		});

		Online_CDAWG_sym scdawg = new Online_CDAWG_sym(stringset, false);
		scdawg.determineAlphabet(false);
		scdawg.build_cdawg();
		// scdawg.print_automaton("svgs/scdawg");

		Common_SCDAWG_Functions scdawg_functions = new Common_SCDAWG_Functions(scdawg);

		HashMap<Node, Integer> nodes_count = scdawg_functions.count_nodes();

		HashMap count_nodes_sorted = Util.sortByValues(nodes_count, "DESC");
		ArrayList<pair> nodes_sink_set = new ArrayList<pair>();

		count_nodes_sorted.put(scdawg.root, null);
		Iterator it3 = count_nodes_sorted.entrySet().iterator();

		HashSet<Integer> usedIDs = new HashSet<Integer>();
		main_loop: while (it3.hasNext()) {
			Map.Entry pair = (Map.Entry) it3.next();

			Node n = (Node) pair.getKey();

			HashSet<Integer> ids = find_n_transitions_to_sinks(n, scdawg, new HashSet<Integer>());

			if (ids.size() != nlines) {
				continue;
			}
			for (Integer id : ids) {
				if (usedIDs.contains(id)) {
					continue main_loop;
				}
			}
			for (Integer id : ids) {
				usedIDs.add(id);
			}
			pair p = new pair();
			p.ids = ids;
			p.node = n;
			nodes_sink_set.add(p);
		}
		// handle final nodes (special case if all ocrs are identical)
		sinkloop: for (Node sink : scdawg.sinks) {
			if (sink.stringnumbers.size() == nlines) {
				// it is impossilbe (?) that this node was used before
				// System.out.println("got sink with " + N + " sinks");
				// System.out.println(sink.stringnumbers);

				// Special case if identical strings had an smaller quasi max node as their sink

				for (pair pn : nodes_sink_set) {
					HashSet<Integer> ids = pn.ids;
					HashSet<Integer> sink_ids = new HashSet<Integer>();

					for (Integer i : sink.stringnumbers) {
						sink_ids.add(i);
					}

					if (sink_ids.equals(ids)) {
						continue sinkloop;
					}
				}

				// end special case

				pair p = new pair();
				p.ids = new HashSet<Integer>();
				for (Integer id : sink.stringnumbers) {
					p.ids.add(id);
				}
				p.node = scdawg.root;
				nodes_sink_set.add(p);
			}
		}

		// ArrayList<String> xyz = new ArrayList<String>(stringset.size());
		String[] xyz = new String[stringset.size()];
		for (pair p : nodes_sink_set) {
			// System.out.println(scdawg.get_node_label(p.node));
			// System.out.println(p.ids);
			ArrayList<OCRLine> linetupel = new ArrayList<OCRLine>();
			for (Integer id : p.ids) {
				int idx = id;

				linetupel.add(ocrlines.get(idx));

				// System.out.println("- " + stringset.get(idx) + ": " +
				// strids.get(idx));
				xyz[idx] = stringset.get(idx);
			}
			this.add(linetupel);
			// System.out.println();
		}
	}

	private static HashSet<Integer> find_n_transitions_to_sinks(Node node, Online_CDAWG_sym scdawg,
			HashSet<Integer> acc) {
		Iterator it = node.children.entrySet().iterator();
		HashSet<Integer> result = new HashSet<Integer>();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Node child = (Node) pair.getValue();
			for (int j = 0; j < scdawg.sinks.size(); j++) {
				if (scdawg.sinks.get(j) == child) {
					if (!sinks.contains(scdawg.sinks.get(j))) {
						for (int k = 0; k < scdawg.sinks.get(j).stringnumbers.size(); k++) {
							acc.add(scdawg.sinks.get(j).stringnumbers.get(k));
							// sinks.add(scdawg.sinks.get(j));
						}
					}
				}
			}
		}

		Iterator it2 = node.children_left.entrySet().iterator();

		while (it2.hasNext()) {
			Map.Entry pair = (Map.Entry) it2.next();
			Node child = (Node) pair.getValue();

			for (int j = 0; j < scdawg.sinks.size(); j++) {
				if (scdawg.sinks.get(j) == child) {
					if (!sinks.contains(scdawg.sinks.get(j))) {
						for (int k = 0; k < scdawg.sinks.get(j).stringnumbers.size(); k++) {
							acc.add(scdawg.sinks.get(j).stringnumbers.get(k));
							// sinks.add(scdawg.sinks.get(j));
						}
					}
				}
			}
		}

		// REC AUFRUF der Funktion mit den Kindern
		Iterator it3 = node.children.entrySet().iterator();

		while (it3.hasNext()) {
			Map.Entry pair = (Map.Entry) it3.next();
			Node child = (Node) pair.getValue();
			find_n_transitions_to_sinks(child, scdawg, acc);
		}

		Iterator it4 = node.children_left.entrySet().iterator();

		while (it4.hasNext()) {
			Map.Entry pair = (Map.Entry) it4.next();
			Node child = (Node) pair.getValue();
			find_n_transitions_to_sinks(child, scdawg, acc);
		}
		return acc;
	}

}
