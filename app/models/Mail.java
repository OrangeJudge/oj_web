package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Mail extends Model {
    @Id
    public Long id;

    public String subject;
    public String receiver;

    @Lob
    public String content;

    public int status = 0;
    public Date createTime = new Date();

    @ManyToOne
    public User user;

    public static Finder<Long, Mail> find = new Finder<>(Long.class, Mail.class);
}
