package moviemanager.entity.oldschool;


import javax.persistence.*;
import java.util.Date;

@Entity(name="OldPerson")
@Table(name="old_person")
public class Person {
    @Id
    private Integer  id;
    private String name;

    @Temporal(TemporalType.DATE)
    private Date birthdate;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, Date birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    public Integer getId() {
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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
}
