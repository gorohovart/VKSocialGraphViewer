import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Visualizer {
    private VKSession vkSession;

    public Visualizer(VKSession session){
        vkSession = session;
    }

    public void visualize(HashMap<VkUser, List<VkUser>> friendToMutual){
        Set<String> nodes = new HashSet<>();
        Set<FriendsEdge> edges = new HashSet<>();
        nodes.add(vkSession.currentUser.FirstName + " " + vkSession.currentUser.LastName);
        for (VkUser friend : friendToMutual.keySet()) {
            nodes.add(friend.FirstName + " " + friend.LastName);
            edges.add(new FriendsEdge(vkSession.currentUser.FirstName + " " + vkSession.currentUser.LastName,
                    friend.FirstName + " " + friend.LastName));
            for (VkUser mutual : friendToMutual.get(friend)) {
                edges.add(new FriendsEdge(friend.FirstName + " " + friend.LastName,
                        mutual.FirstName + " " + mutual.LastName));
            }
        }

        Graph graph = createGraph(nodes, edges);
        graph.display();
    }

    private Graph createGraph(Set<String> nodes, Set<FriendsEdge> edges){
        Graph graph = new SingleGraph("Friends graph");
        for (String node : nodes) {
            Node n = graph.addNode(node);
            n.addAttribute("ui.label", node);
            n.addAttribute("ui.style", "fill-color: rgb(0,0,255);");
        }
        for (FriendsEdge edge : edges) {
            try {
                Edge e = graph.addEdge(edge.Name, edge.First, edge.Second);
                e.addAttribute("ui.style", "fill-color: rgb(255,192,203);");
            } catch (EdgeRejectedException e) {
                // for symmetric edges removal
            }

        }
        return graph;
    }
}
