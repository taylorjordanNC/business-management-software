package model;

/** This class creates and manages objects of type 'Country' and all associated variables. */
public class Country {
    private Integer id;
    private String name;

    public Country() {
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
