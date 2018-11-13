package io.jbotsim.ui.android;

import java.util.HashMap;
import java.util.Iterator;

import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileAccess {

	public String save(Topology topology) throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject vertices = new JSONObject();
		json.put("vertices", vertices);

		JSONArray edges = new JSONArray();
		json.put("edges", edges);

		for (Node v : topology.getNodes()) {
			JSONObject vertexJson = new JSONObject();
			vertexJson.put("x", v.getX());
			vertexJson.put("y", v.getY());
			if (v.getLabel() == null || vertices.has((String) v.getLabel())) {
				v.setLabel("n" + v.getID());
			}
			vertices.put((String) v.getLabel(), vertexJson);
		}
		for (Link e : topology.getLinks()) {
			JSONObject edge = new JSONObject();
			edge.put("source", e.endpoint(0).getLabel());
			edge.put("target", e.endpoint(1).getLabel());
			edges.put(edge);
		}
		return json.toString();
	}

	public void load(Topology topology,	String json) throws JSONException {
		JSONObject graphJson = new JSONObject(json);
		topology.clear();

		JSONObject vertices = graphJson.getJSONObject("vertices");
		HashMap<String, Node> verticesMap = new HashMap<>();
		for (@SuppressWarnings("rawtypes")
		Iterator i = vertices.keys(); i.hasNext();) {
			String key = (String) i.next();
			JSONObject vertexJson = vertices.getJSONObject(key);
			Node node = new Node();
			node.setLocation(vertexJson.getDouble("x"), vertexJson.getDouble("y"));
			node.setLabel(key);
			topology.addNode(node);
			verticesMap.put(key, node);
		}

		JSONArray edges = graphJson.getJSONArray("edges");

		for (int i = 0; i < edges.length(); i++) {
			JSONObject edge = edges.getJSONObject(i);
			String source = edge.getString("source");
			String target = edge.getString("target");
			topology.addLink(new Link(verticesMap.get(source), verticesMap.get(target)));
		}
	}
}
