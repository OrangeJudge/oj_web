package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class ProblemFollower extends Model {
    @Id
    public Long id;

    @ManyToOne
    public Problem problem;
    @ManyToOne
    public User user;

    public Date createTime = new Date();
}
