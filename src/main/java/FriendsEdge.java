public class FriendsEdge {
    public String Name;
    public String First;
    public String Second;
    private static Integer NextId = 0;

    private String getNextName() {
        return (NextId++).toString();
    }
    public FriendsEdge(String first, String second) {
        First = first;
        Second = second;
        Name = getNextName();
    }
}
