package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class UserRelation {
    @Id
    public Long id;
    @ManyToOne
    public User follower;
    @ManyToOne
    public User following;
    public Date createTime = new Date();
}
