package controllers;

import models.Problem;
import models.Solution;
import models.User;
import play.mvc.Result;

import java.util.List;

public class SolutionController extends OJController {

    public static Result solutionListPage() {
        return ok(views.html.solution.list.render());
    }


    public static Result solutionPage(Long id) {
        Solution solution = Solution.find.byId(id);
        if (solution != null) {
            return ok(views.html.solution.detail.render(solution));
        } else {
            return notFound("Solution not found.");
        }
    }


    public static Result recentSolutions() {
        int pageSize = 100;
        int pageNum = 0;
        if (request().queryString().containsKey("pageSize")) {
            pageSize = Integer.parseInt(request().queryString().get("pageSize")[0]);
        }
        if (request().queryString().containsKey("pageNum")) {
            pageNum = Integer.parseInt(request().queryString().get("pageNum")[0]);
        }
        String queryCondition = "";
        if (request().queryString().containsKey("user")) {
            String username = request().queryString().get("user")[0];
            User user = User.find.where().eq("name", username).findUnique();
            if (user == null) {
                return ok(jsonResponse(1, "User not found."));
            }
            queryCondition += "( user_id = " + user.id + ")";
        }
        if (request().queryString().containsKey("problem")) {
            String problemSlug = request().queryString().get("problem")[0];
            Problem problem = Problem.findBySlug(problemSlug);
            if (problem == null) {
                return ok(jsonResponse(1, "Problem not found."));
            }
            if (queryCondition.length() > 0) {
                queryCondition += " and ";
            }
            queryCondition += "( problem_id = " + problem.id + ")";
        }
        if (request().queryString().containsKey("result")) {
            String resultString = request().queryString().get("result")[0];
            int result = Integer.parseInt(resultString);
            if (queryCondition.length() > 0) {
                queryCondition += " and ";
            }
            queryCondition += "( result = " + result + ")";
        }
        List<Solution> solutions = Solution.find.orderBy("id DESC")
                .where(queryCondition).findPagingList(pageSize).getPage(pageNum).getList();
        return ok(jsonResponse(0, solutions));
    }
}
