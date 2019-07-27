package example;

import java.io.Serializable;

// Add Serializable
public class Employee implements Serializable {

    // Serializer ID
    private static final long serialVersionUID = -295422703255886286L;

    private String name;
    private String id;

    public Employee() {
    }

    public Employee(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Welcome our new employee! {Name: %s, Id: %s}", getName(), getId());
    }

}
