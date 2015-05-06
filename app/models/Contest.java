package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Contest extends Model {
    @Id
    public Long id;
    public String title;
    public String description;
    public Date beginTime;
    public int duration;  // minutes
    public boolean isPublic;
    public String password;
    @ManyToOne
    public User manager;

    @OneToMany(mappedBy = "contest")
    public List<ContestParticipant> participants;
    @OneToMany(mappedBy = "contest")
    public List<Problem> problems;
    @OneToMany(mappedBy = "contest")
    public List<Solution> solutions;

    @OneToMany(mappedBy = "contest")
    public List<Discussion> discussions;

    public Date createTime = new Date();

    public static Finder<Long, Contest> find = new Finder<>(Long.class, Contest.class);
}
