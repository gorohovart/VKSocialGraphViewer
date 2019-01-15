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
import java.util.Objects;
import java.util.stream.Collectors;

import static javafx.application.Application.launch;

public class VKSession {
    private static UserActor userActor;
    public static VkUser currentUser;
    private static VkApiClient vk;
    private static HashMap<Integer, VkUser> idToUser = new HashMap<>();

    public static void Initialize() {
        TransportClient transportClient = com.vk.api.sdk.httpclient.HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient, new Gson(), 2);
        userActor = ActorLoader.tryLoadUserActor();
        if (userActor != null)
            getCurrentUser();

        launch(VkAuthApp.class);

        userActor = VkAuthApp.userActor;
        getCurrentUser();
    }

    public static VkUser getCurrentUser() {
        if (currentUser == null) {
            try {
                List<UserXtrCounters> user = vk.users().get(userActor).userIds(userActor.getId().toString()).execute();

                currentUser = new VkUser(userActor.getId(), user.get(0).getFirstName(), user.get(0).getLastName());
            } catch (ApiException | ClientException e) {
                e.printStackTrace();
            }
        }
        return currentUser;
    }

    public static void collectFriendsOfCurrentUser() {
        try {
            List<Integer> ids = vk.friends().get(userActor).execute().getItems();
            List<String> _ids = ids.stream().map(Object::toString).collect(Collectors.toList());

            Thread.sleep(300);
            List<UserXtrCounters> friends = vk.users().get(userActor).userIds(_ids).execute();

            for (UserXtrCounters friend : friends) {
                if (friend.getDeactivated() == null)
                    idToUser.put(friend.getId(), new VkUser(friend.getId(), friend.getFirstName(), friend.getLastName()));
            }
        } catch (ApiException | ClientException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<VkUser, List<Integer>> getFriendsOfFriendsNonMutal() {
        HashMap<VkUser, List<Integer>> friendToMutual = new HashMap<>();
        try {

            for (Integer id : idToUser.keySet()) {
                Thread.sleep(301);
                List<Integer> friendsOfFriend = vk.friends().get(userActor).userId(id).execute().getItems();
                friendToMutual.put(idToUser.get(id), friendsOfFriend);
            }
        } catch (ApiException | InterruptedException | ClientException e) {
            e.printStackTrace();
        }
        return friendToMutual;
    }

    public static HashMap<VkUser, List<VkUser>> getFriendsOfFriends() {
        HashMap<VkUser, List<VkUser>> friendToMutual = new HashMap<>();
        try {
            List<Integer> ids = new ArrayList<>(idToUser.keySet());
            List<List<Integer>> splitedIds = new ArrayList<>();
            int counter = -1;
            for (int i = 0; i < ids.size(); i++) {
                if (i % 100 == 0) {
                    counter++;
                    splitedIds.add(new ArrayList<>());
                }
                splitedIds.get(counter).add(ids.get(i));
            }
            for (List<Integer> idsList : splitedIds) {
                Thread.sleep(300);
                List<MutualFriend> friendsOfFriends = vk.friends().getMutualTargetUids(userActor, idsList).execute();
                for (MutualFriend mutualFriend : friendsOfFriends) {
                    friendToMutual.put(idToUser.get(mutualFriend.getId()),
                            mutualFriend.getCommonFriends()
                                    .stream().map(frId -> idToUser.get(frId))
                                    .filter(Objects::nonNull).collect(Collectors.toList()));
                }
            }
        } catch (ApiException | InterruptedException | ClientException e) {
            e.printStackTrace();
        }
        return friendToMutual;
    }


}
