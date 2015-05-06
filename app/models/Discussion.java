package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Discussion extends Model {
    @Id
    public Long id;
    public String title;
    @ManyToOne
    public Problem problem;
    @ManyToOne
    public Solution solution;
    @ManyToOne
    public Contest contest;

    @ManyToOne
    public Discussion parent;
    @ManyToOne
    public User user;
    @Lob
    public String content;

    @ManyToMany
    public List<User> mentions;

    public Date createTime = new Date();
    public Date lastReplyTime;  // used for sorting

    public boolean containSolution = false;
    public int alwaysOnTop = 0;  // used for sorting

    public static Finder<Long, Discussion> find = new Finder<>(Long.class, Discussion.class);
}
