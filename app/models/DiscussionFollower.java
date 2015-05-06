package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class DiscussionFollower {
    @Id
    public Long id;

    @ManyToOne
    public Discussion discussion;
    @ManyToOne
    public User user;

    public Date createTime = new Date();
}
