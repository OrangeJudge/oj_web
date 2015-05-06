package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Activity {
    @Id
    public Long id;

    @ManyToOne
    public User user;
    @ManyToOne
    public Problem problem;
    @ManyToOne
    public Discussion discussion;

    public int level;  // 0 notice itself, 1 notice follower, 2 notice the world.

    public int activityType;
    public String metaData;
    public Date createTime = new Date();
}
