import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Visualizer {
    private VKSession vkSession;
    private static Integer nextEdgeIndex = 0;

    private static String selfColor = "rgb(255,0,0)";
    private static String friendColor = "rgb(0,0,255)";
    private static String selfToFriendEdgeColor = "rgb(220,220,220)";
    private static String friendToFriendEdgeColor = "rgb(255,192,203)";

    public Visualizer() {
    }

    private String getnextEdgeIndex() {
        return (nextEdgeIndex++).toString();
    }

    private void addNode(Graph graph, Integer id, String label, String color) {
        Node n = graph.addNode(id.toString());
        n.addAttribute("ui.label", label);
        n.addAttribute("ui.style", "fill-color: " + color + ";");
    }

    private void addEdge(Graph graph, String from, String to, String color, String size) {
        Edge e = graph.addEdge(getnextEdgeIndex(), from, to);
        e.addAttribute("ui.style", "size: " + size + "; fill-color: " + color + ";");
    }

    private int getMutualCount(HashMap<VkUser, List<VkUser>> friendToMutual, VkUser first, VkUser second) {
        Set<VkUser> firstSet = new HashSet<>(friendToMutual.get(first));
        firstSet.retainAll(friendToMutual.get(second));
        return firstSet.size();
    }

    private String getFriendToFriendEdgeColor(int mutualCount, int maxCount) {
        int minInRange = 0;
        int maxInRange = 200;
        int range = maxInRange - minInRange;
        int intence = (range * mutualCount) / maxCount;
        int color = maxInRange - intence;
        return "rgb(240," + color + "," + color + ")";
    }

    private String getPair(VkUser user1, VkUser user2) {
        int id1 = user1.getId();
        int id2 = user2.getId();
        if (id1 > id2) return id2 + "-" + id1;
        return id1 + "-" + id2;
    }


    public void visualize(HashMap<VkUser, List<VkUser>> friendToMutual) {
        Graph graph = new SingleGraph("Friends graph");
        String currUserLabel = vkSession.currentUser.getFullName();
        addNode(graph, vkSession.currentUser.getId(), currUserLabel, selfColor);
        int maxMutual = 0;

        Map<String, Integer> pairToMutualCount = new HashMap<>();

        for (VkUser friend : friendToMutual.keySet()) {
            Set<VkUser> another = new HashSet<>(friendToMutual.keySet());
            another.remove(friend);
            for (VkUser secondFriend : another) {
                String label = getPair(friend, secondFriend);
                int mutualCount = getMutualCount(friendToMutual, friend, secondFriend);
                pairToMutualCount.put(label, mutualCount);
                if (mutualCount > maxMutual) maxMutual = mutualCount;
            }
        }

//        for (VkUser friend : friendToFriends.keySet()) {
//            Set<VkUser> another = new HashSet<>(friendToFriends.keySet());
//            another.remove(friend);
//            for (VkUser secondFriend : another) {
//                String label = friend.getId() + "-" + secondFriend.getId();
//                int mutualCount = getMutualCount(friendToFriends, friend, secondFriend);
//                pairToMutualCount.put(label, mutualCount);
//                if (mutualCount > maxMutual) maxMutual = mutualCount;
//            }
//        }


        // first, add all nodes and edges to them from self
        for (VkUser friend : friendToMutual.keySet()) {
            String friendLabel = friend.getFullName();
            addNode(graph, friend.getId(), friendLabel, friendColor);
            addEdge(graph, vkSession.currentUser.getId().toString(), friend.getId().toString(), selfToFriendEdgeColor, "1px");
        }

        int counter = 0;
        for (VkUser friend : friendToMutual.keySet()) {
            for (VkUser mutual : friendToMutual.get(friend)) {
                if (!friendToMutual.containsKey(mutual)) {
                    System.out.println(mutual.getFullName() + "   " + mutual.getId());
                    counter++;
                }
            }
        }

        System.out.println("number of not found = " + counter);

        for (VkUser friend : friendToMutual.keySet()) {
            String friendLabel = friend.getFullName();
            for (VkUser mutual : friendToMutual.get(friend)) {
                try {
                    String mutualLabel = mutual.getFullName();
                    int mutualOfPair = pairToMutualCount.get(getPair(friend, mutual));
                    addEdge(graph, friend.getId().toString(), mutual.getId().toString(), getFriendToFriendEdgeColor(mutualOfPair, maxMutual), "3px");
                } catch (EdgeRejectedException ex) {
                    // for symmetric edges removal
                }
            }
        }
        graph.display(true);
    }
}
