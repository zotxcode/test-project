package models;

/**
 * @author hendriksaragih
 */
public class UserInfo {

    private String name;
    private String email;
    private String password;
    private Long brandId;

    /**
     * Creates a new UserInfo instance.
     * @param name The name.
     * @param email The email.
     * @param brandId The brandId.
     */
    public UserInfo(String name, String email, Long brandId) {
        this.name = name;
        this.email = email;
        this.brandId = brandId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }
}