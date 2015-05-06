package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Solution extends Model {
    @Id
    public Long id;

    @ManyToOne
    public User user;
    public boolean isPrivate = false;

    @ManyToOne
    public Contest contest;

    // Used by judge
    public int language;
    @JsonIgnore
    @Lob
    public String code;

    @ManyToOne
    public Problem problem;

    // Used for dispatching
    public Date submitTime = new Date();
    public Date dispatchTime;
    public Date judgeTime;

    @ManyToOne
    public Judge judge;

    public int result;
    @Lob
    public String judgeResponse;

    public Integer timeUsed;  // in ms.
    public Integer memoryUsed;  // in KB

    @JsonIgnore
    @OneToMany(mappedBy = "solution")
    public List<Discussion> discussions;

    public boolean isDispatched() {
        return dispatchTime != null;
    }

    public boolean isJudged() {
        return judgeTime != null;
    }

    public static Finder<Long, Solution> find = new Finder<>(Long.class, Solution.class);

    public boolean codeAccess(User user) {
        /*
        People who can view public solution code:
        1. The author
        2. System admin
        3. Users who solved that problem
        People who can view the private solution are the same as above except for 3.
         */
        if (user == null) {
            return false;
        }
        if (user.equals(this.user)) {
            return true;
        }
        if (user.adminLevel > 0) {
            return true;
        }
        if (!isPrivate && problem.isSolvedBy(user)) {
            return true;
        }
        return false;
    }
}