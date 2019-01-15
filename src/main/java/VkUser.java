public class VkUser {
    private final String FirstName;
    private final String LastName;
    private final Integer Id;

    public VkUser(Integer id, String firstName, String lastName) {
        FirstName = firstName;
        LastName = lastName;
        Id = id;
    }

    public String getFullName() {
        return this.FirstName + " " + this.LastName;
    }

    public Integer getId() {
        return this.Id;
    }


    @Override
    public int hashCode() {
        return Id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VkUser) {
            VkUser user = (VkUser) obj;
            return user.Id.equals(Id);
        }
        return false;
    }
}
