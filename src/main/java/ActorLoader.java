import com.vk.api.sdk.client.actors.UserActor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ActorLoader {
    public static void dumpUserActor(UserActor userActor) {
        try(PrintWriter out = new PrintWriter("userActor.txt")) {
            out.println(userActor.getId());
            out.println(userActor.getAccessToken());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static UserActor tryLoadUserActor() {
        try(Scanner s = new Scanner(new File("userActor.txt"))) {
            List<String> list = new ArrayList<String>();
            while (s.hasNext()){
                list.add(s.next());
            }
            if (list.size() < 2) return null;
            return new UserActor(Integer.parseInt(list.get(0)), list.get(1));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void removeActor() {
        File file = new File("userActor.txt");
        file.delete();
    }
}
