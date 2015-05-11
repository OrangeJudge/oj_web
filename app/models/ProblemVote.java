package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class ProblemVote extends Model {
    @Id
    public Long id;
    @ManyToOne
    public Problem problem;
    @ManyToOne
    public User user;

    @Constraints.Min(1)
    @Constraints.Max(5)
    public int rating;

    @Constraints.Min(1)
    @Constraints.Max(5)
    public int difficulty;

    public Date createTime = new Date();

    public static Finder<Long, ProblemVote> find = new Finder<>(Long.class, ProblemVote.class);
}
