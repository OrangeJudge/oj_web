package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ContestProblem extends Model {
    @Id
    public long id;
    @ManyToOne
    public Contest contest;
    @ManyToOne
    public Problem problem;
    public String slug;
}
