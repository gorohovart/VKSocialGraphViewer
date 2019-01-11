import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        VKSession vkSession = new VKSession();
        try {
            vkSession.collectFriendsOfCurrentUser();
            HashMap<VkUser, List<VkUser>> friendToMutual = vkSession.getFriendsOfFriends();
            new Visualizer(vkSession).visualize(friendToMutual);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}