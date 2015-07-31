package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Activity extends Model {
    @Id
    public Long id;

    @ManyToOne
    public User user;
    // Users who follow this user will get those activities if level >= 1.

    @ManyToOne
    public Problem problem;
    // Users who follow this problem will get those activities if level >= 1.

    @ManyToOne
    public Discussion discussion;
    // Users who participate in this discussion will get those activities if level >= 1.

    public int level;  // 0 notice itself, 1 notice follower, 2 notice the world.

    public int activityType;
    public String metaData;
    public Date createTime = new Date();
}
