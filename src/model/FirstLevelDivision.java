package model;

/** This class creates and manages objects of type 'FirstLevelDivision' and all associated variables. */
public class FirstLevelDivision {
    private Integer id;
    private String name;
    private int country_id;

    public FirstLevelDivision() {
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

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }
}
