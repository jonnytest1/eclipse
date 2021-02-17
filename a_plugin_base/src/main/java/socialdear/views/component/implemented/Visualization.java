package socialdear.views.component.implemented;

import static guru.nidi.graphviz.model.Factory.graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;

/**
 * @since 3.0
 */
public class Visualization {

	private Map<Visualisable, List<Visualisable>> visited;

	private Map<String, Node> nodesMap = new HashMap<>();

	public static interface Visualisable {

		<T extends Visualisable> List<T> getConnections();

		<T extends Visualisable> List<T> getBackwardConnections();

		String getNodeName();
	}

	public Visualization() {

		// 
		//	GraphvizEngine
		GraphvizCmdLineEngine graphvizCmdLineEngine = new GraphvizCmdLineEngine("C:\\Programme\\Graphviz2.44.1\\bin\\dot.exe");

		//graphvizCmdLineEngine.executor(new CommandRunner());
		Graphviz.useEngine(graphvizCmdLineEngine);

	}

	public File visualize(Visualisable o, Integer depth) throws IOException {
		nodesMap = new HashMap<>();
		visited = new HashMap<>();

		List<LinkSource> nodes = new ArrayList<>();

		addNodes(o, nodes, depth, null);

		Graph g = graph("example1").directed().graphAttr()//
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT))//
				.linkAttr().with("class", "link-class")//
				.with(nodes);

		Graphviz.fromGraph(g).height(100).render(Format.SVG).toImage();
		File file = new File(System.getenv().get("USERPROFILE") + "\\AppData\\Local\\Temp\\GraphvizJava");

		File[] files = file.listFiles();
		Instant lastModTime = null;
		File lastMOd = null;
		for (File f : files) {
			BasicFileAttributes atts = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
			if (lastMOd == null || atts.lastModifiedTime().toInstant().isAfter(lastModTime)) {
				lastMOd = f;
				lastModTime = atts.lastModifiedTime().toInstant();
			}
		}

		return new File(lastMOd.getAbsolutePath() + "\\outfile.svg");

	}

	private void addNodes(Visualisable o, List<LinkSource> nodes, Integer depth, Boolean forward) {
		if (depth == 1) {
			return;
		}

		if (forward == null || forward) {
			List<Visualisable> connected = o.getConnections();

			Node from = nodesMap.computeIfAbsent(o.getNodeName(), t -> {
				if (forward == null) {
					return Factory.node(t).with(Color.RED);
				}
				return Factory.node(t);
			});
			for (Visualisable c : connected) {
				if (!visited.computeIfAbsent(o, n -> new ArrayList<>()).contains(c)) {

					Node to = nodesMap.computeIfAbsent(c.getNodeName(), Factory::node);
					LinkSource n = from.link(to);
					nodes.add(n);
					visited.computeIfAbsent(o, okey -> new ArrayList<>()).add(c);
					addNodes(c, nodes, depth - 1, true);
				}
			}
		}
		if (forward == null || !forward) {
			List<Visualisable> connected = o.getBackwardConnections();

			Node to = nodesMap.computeIfAbsent(o.getNodeName(), t -> {
				if (forward == null) {
					return Factory.node(t).with(Color.RED);
				}
				return Factory.node(t);
			});

			for (Visualisable c : connected) {
				if (!visited.computeIfAbsent(c, n -> new ArrayList<>()).contains(o)) {

					Node from = nodesMap.computeIfAbsent(c.getNodeName(), Factory::node);
					LinkSource n = from.link(to);
					nodes.add(n);
					visited.computeIfAbsent(c, okey -> new ArrayList<>()).add(o);
					addNodes(c, nodes, depth - 1, false);
				}
			}
		}
	}
}
