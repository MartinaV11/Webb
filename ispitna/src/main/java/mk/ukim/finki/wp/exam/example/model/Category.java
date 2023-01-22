package mk.ukim.finki.wp.exam.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity //prvo baranje
public class Category {

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    @Id //prvo baranje
    @GeneratedValue //prvo baranje
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
