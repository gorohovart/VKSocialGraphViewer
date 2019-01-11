import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.friends.MutualFriend;
import com.vk.api.sdk.objects.users.UserXtrCounters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.application.Application.launch;

public class VKSession {
    private UserActor userActor;
    public VkUser currentUser;
    private VkApiClient vk;
    private HashMap<Integer, VkUser> idToUser = new HashMap<>();

    private void InitializeVK() {
        TransportClient transportClient = com.vk.api.sdk.httpclient.HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient, new Gson(), 2);
    }

    public VKSession() {
        InitializeVK();

        launch(VkAuthApp.class);
        userActor = VkAuthApp.userActor;
        try {
            List<UserXtrCounters> user = vk.users().get(userActor).userIds(userActor.getId().toString()).execute();

            currentUser = new VkUser(userActor.getId(), user.get(0).getFirstName(), user.get(0).getLastName());
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }

    }

    public void collectFriendsOfCurrentUser() {
        try {
            List<Integer> ids = vk.friends().get(userActor).execute().getItems();
            List<String> _ids = ids.stream().map(Object::toString).collect(Collectors.toList());

            Thread.sleep(300);
            List<UserXtrCounters> friends = vk.users().get(userActor).userIds(_ids).execute();

            for (UserXtrCounters friend : friends) {
                if (friend.getId() != 439640735)
                    idToUser.put(friend.getId(), new VkUser(friend.getId(), friend.getFirstName(), friend.getLastName()));
            }
        } catch (ApiException | ClientException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public HashMap<VkUser, List<VkUser>> getFriendsOfFriends() {
        HashMap<VkUser, List<VkUser>> friendToMutual = new HashMap<>();
        try {
            Thread.sleep(300);
            List<Integer> ids = new ArrayList<>(idToUser.keySet());
            List<MutualFriend> friendsOfFriends = vk.friends().getMutualTargetUids(userActor, ids).execute();
            for (MutualFriend mutualFriend : friendsOfFriends) {
                friendToMutual.put(idToUser.get(mutualFriend.getId()), mutualFriend.getCommonFriends().stream().map(frId -> idToUser.get(frId)).collect(Collectors.toList()));
            }
        } catch (ApiException | InterruptedException | ClientException e) {
            e.printStackTrace();
        }
        return friendToMutual;
    }
}
