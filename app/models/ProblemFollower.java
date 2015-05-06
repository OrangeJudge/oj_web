package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class ProblemFollower {
    @Id
    public Long id;

    @ManyToOne
    public Problem problem;
    @ManyToOne
    public User user;

    public Date createTime = new Date();
}
