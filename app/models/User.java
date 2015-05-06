package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.routes;
import play.Logger;
import play.Play;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;

import play.cache.Cache;
import play.twirl.api.Html;
import utils.Mailer;
import utils.StringHashing;
import views.html.email.verifyEmail;

@Entity
public class User extends Model {
    @Id
    public long id;
    @Constraints.Pattern("^(\\w+)$")
    @Column(unique = true, nullable = false)
    public String name;

    @Column(unique = true, nullable = false)
    public String email;
    public boolean isEmailVerified = false;
    public Date lastEmailModified = new Date();
    public Date lastVerificationEmailSent = new Date();

    private String password;
    public String secret = UUID.randomUUID().toString();

    public int adminLevel = 0;

    public int status = 0;  // 1 for pending delete. 2 for deleted.

    public boolean gender;  // female is always true
    public String displayName;
    public String school;
    public String country;
    public String link;
    @Lob
    public String description;

    public Date createTime = new Date();  // Create time is never changed after user account is created.
    public Date lastLoginTime;

    @OneToMany(mappedBy = "follower")
    public List<UserRelation> following;

    @OneToMany(mappedBy = "following")
    public List<UserRelation> followers;

    @OneToMany(mappedBy = "user")
    public List<ProblemFollower> followingProblems;

    @OneToMany(mappedBy = "user")
    public List<ProblemFollower> starringProblems;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    public List<Solution> solutions;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    public List<ContestParticipant> participatingContests;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    public List<Mail> mails;

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static boolean isValidEmail(String email) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(EMAIL_PATTERN);
    }

    public String setEmail(String newEmail) {
        if (newEmail == null) {
            return "Email is empty.";
        }
        if (!isValidEmail(newEmail)) {
            return "Email is invalid";
        }
        if (User.find.where().eq("email", newEmail).findRowCount() > 0) {
            return "Email is registered.";
        }
        email = newEmail;
        isEmailVerified = false;
        lastEmailModified = new Date();
        return null;
    }

    public static void updateStatus() {
        List<User> pendingVerifyUsers = find.where().eq("isEmailVerified", false).findList();
        for (User user : pendingVerifyUsers) {
            if ((new Date()).getTime() - user.lastEmailModified.getTime() > ONE_DAY) {
                user.pendingDelete();
            }
        }
    }

    private static String substring(String in) {
        if (in.length() <= 255) {
            return in;
        }
        return in.substring(0, 255);
    }

    public void pendingDelete() {
        if (status > 0) return;
        status = 1;
        name = substring("deleted_" + UUID.randomUUID().toString() + "_" + name);
        email = substring("deleted_" + UUID.randomUUID().toString() + "_" + email);
        save();
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(hashPassword(password));
    }

    private static String hashPassword(String password) {
        return StringHashing.sha1(password);
    }

    public static Finder<Long, User> find = new Finder<> (Long.class, User.class);

    @JsonIgnore
    public List<Problem> getSolvedProblems() {
        List<Problem> problems;
        problems = (List<Problem>) Cache.get("userSolvedProblems." + id);
        if (problems == null) {
            List<Solution> solutions  = Solution.find.where().eq("user_id", id).eq("result", 200)
                    .select("problem").setDistinct(true).orderBy("id DESC").findList();
//        Set<Problem> problems = solutions.stream().map(solution -> solution.problem).collect(Collectors.toSet());
//        Will get ebean error when using collector.
            problems = new ArrayList<>();
            for (Solution solution : solutions) {
                if (!problems.contains(solution.problem)) {
                    problems.add(solution.problem);
                }
            }
            Cache.set("userSolvedProblems." + id, problems, 60*15);
        }
        return problems;
    }

    public void removeSolvedProblemsCache() {
        Cache.remove("userSolvedProblems." + id);
    }

    @JsonIgnore
    public String getVerificationPass() {
        String secret = Play.application().configuration().getString("application.secret");
        String createTimeString = "" + createTime.getTime();
        String original = secret + email + createTimeString;
        Logger.debug("Original verification pass: " + original);
        return StringHashing.sha1(original);
    }

    @JsonIgnore
    public String getVerificationLink() {
        String siteUrl = Play.application().configuration().getString("application.siteUrl");
        String verifyPath = routes.UserController.verifyEmail(name, getVerificationPass()).toString();
        return siteUrl + verifyPath;
    }

    public void sendVerifyEmail() {
        lastVerificationEmailSent = new Date();
        Html content = verifyEmail.render(this);
        Mailer.sendMail(this, "Verify Your Email Address on OrangeJudge.com", content.toString());
    }
}
