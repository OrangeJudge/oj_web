package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Judge extends Model {
    @Id
    public Integer id;
    public String nickname;
    public String secret;
    public int sort;

    public static Finder<Integer, Judge> find = new Finder<>(Integer.class, Judge.class);
}
