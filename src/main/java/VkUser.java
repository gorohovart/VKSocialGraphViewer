public class VkUser {
    public final String FirstName;
    public final String LastName;
    public final Integer Id;

    public VkUser(Integer id, String firstName, String lastName) {
        FirstName = firstName;
        LastName = lastName;
        Id = id;
    }

    @Override
    public int hashCode() {
        return Id.hashCode() * FirstName.hashCode() * LastName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VkUser) {
            VkUser user = (VkUser) obj;
            return user.Id.equals(Id) && user.FirstName.equals(FirstName) && user.LastName.equals(LastName);
        }
        return false;
    }
}
