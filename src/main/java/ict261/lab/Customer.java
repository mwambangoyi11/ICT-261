package ict261.lab;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a customer. Uses JavaFX properties so the TableView
 * updates automatically when values change.
 */
public class Customer {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty province = new SimpleStringProperty();

    public Customer(String name, String province) {
        this.name.set(name);
        this.province.set(province);
    }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getProvince() { return province.get(); }
    public void setProvince(String value) { province.set(value); }
    public StringProperty provinceProperty() { return province; }

    @Override
    public String toString() {
        return getName() + " (" + getProvince() + ")";
    }
}
