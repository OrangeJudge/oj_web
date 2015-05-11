package controllers;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.JsonNode;
import models.Problem;
import models.ProblemVote;
import models.Solution;
import models.User;
import play.mvc.Result;
import utils.Authentication;
import java.io.File;
import java.util.List;

public class ProblemController extends OJController {
    public static Result problemListPage() {
        List<Problem> problems = Problem.find.where().or(Expr.eq("status", 0), Expr.eq("status", 3)).findList();
        return ok(views.html.problem.list.render(problems, null));
    }

    public static Result problemListPage(User user) {
        List<Problem> problems = user.getSolvedProblems();
        return ok(views.html.problem.list.render(problems, user));
    }

    public static Result problemDetailPage(String slug) {
        Problem problem = Problem.findBySlug(slug);
        if (problem == null) {
            return notFound("Problem not found.");
        }
        boolean solvedByCurrentUser = false;
        boolean votedByCurrentUser = false;
        User user = UserController.currentUser();
        if (problem.isSolvedBy(user)) {
            solvedByCurrentUser = true;
            ProblemVote vote = ProblemVote.find.where().eq("problem", problem).eq("user", user).findUnique();
            if (vote != null) {
                votedByCurrentUser = true;
            }
        }
        return ok(views.html.problem.detail.render(problem, solvedByCurrentUser, votedByCurrentUser));
    }

    public static Result problemDetail(String slug) {
        Problem problem = Problem.findBySlug(slug);
        if (problem == null) {
            return notFound(jsonResponse(1, "Problem not found."));
        }
        return ok(jsonResponse(0, problem));
    }

    @Authentication(json = true)
    public static Result handleProblemSubmit(String slug) {
        JsonNode in = request().body().asJson();
        if (in == null) {
            return formSubmitResponse(1, null, "Expecting Json data.");
        }
        Problem problem = Problem.findBySlug(slug);
        User user = (User) ctx().args.get("user");
        Solution solution = new Solution();
        solution.language = in.get("language").asInt();
        solution.code = in.get("code").asText();
        solution.user = user;
        solution.problem = problem;
        solution.save();
        return formSubmitResponse(0, null, null);
    }

    public static Result assetFile(Long id, String filename) {
        String path = "upload/assets/" + id + "/" + filename;
        File file = new File(path);
        return ok(file);
    }

    @Authentication(json = true)
    public static Result handleVoting(String slug) {
        Problem problem = Problem.findBySlug(slug);
        User user = (User) ctx().args.get("user");
        if (problem == null) {
            return notFound(jsonResponse(1, "Problem not found."));
        }
        JsonNode in = request().body().asJson();
        if (in == null) {
            return formSubmitResponse(1, null, "Expecting Json data.");
        }
        try {
            ProblemVote vote = ProblemVote.find.where().eq("problem", problem).eq("user", user).findUnique();
            if (vote == null) {
                vote = new ProblemVote();
            }
            vote.problem = problem;
            vote.user = user;
            vote.rating = in.get("quality").asInt();
            vote.difficulty = in.get("difficulty").asInt();
            vote.save();
            return ok(jsonResponse(0, null));
        } catch (Exception e) {
            return badRequest(jsonResponse(2, e.toString()));
        }
    }
}
