package Client;

import java.util.ArrayList;


public class User {
    private ArrayList<String> contacts;
    private String name;
    private Integer id;
    
    public User(String name, Integer id) {
        this.name = name;
        this.id = id;
    }
    
    public void AddContact(String contact) { contacts.add(contact); }
    public void AddContacts(ArrayList<String> contacts) { contacts.addAll(contacts); }
    public void EditName(String name) { this.name = name; }
    public String GetName() { return name; }
    public Integer GetId() { return id; }
    public ArrayList<String> GetContacts() { return contacts; }
}
