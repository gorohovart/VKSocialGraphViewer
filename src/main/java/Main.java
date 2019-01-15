import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        VKSession.Initialize();
        try {
            VKSession.collectFriendsOfCurrentUser();
            HashMap<VkUser, List<VkUser>> friendToMutual = VKSession.getFriendsOfFriends();
            //HashMap<VkUser, List<Integer>> friendToFriends = vkSession.getFriendsOfFriendsNonMutal();
            new Visualizer().visualize(friendToMutual);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}