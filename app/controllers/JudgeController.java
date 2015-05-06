package controllers;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Judge;
import models.Problem;
import models.Solution;
import org.zeroturnaround.zip.ZipUtil;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.JudgeAccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JudgeController extends OJController {

    @JudgeAccess
    private static Result responseSolutionToJudge(Solution solution) {
        solution.dispatchTime = new Date();
        solution.save();
        ObjectNode response = Json.newObject();
        response.put("solution", solution.id);
        response.put("problem", solution.problem.id);
        response.put("problem_hash", solution.problem.resourcesHash);
        response.put("code", solution.code);
        response.put("language", solution.language);
        return ok(jsonResponse(0, response));
    }

    @JudgeAccess
    public static Result judgeFetchSolution() {
        String[] requestLanguages = request().queryString().get("language");
        if (requestLanguages == null || requestLanguages.length == 0) {
            return ok(jsonResponse(2, "Supported Language not provided."));
        }
        List<Integer> languageList = new ArrayList<>();
        for (String requestLanguage : requestLanguages) {
            languageList.add(Integer.parseInt(requestLanguage));
        }
        Date now = new Date();
        // If a solution is dispatched 3 minutes ago, it will be re-dispatched.
        Date expiredTime = new Date(now.getTime() - 3 * 60 * 1000);
        List<Solution> solutions = Solution.find.where()
                .or(Expr.eq("dispatchTime", null),
                    Expr.and(Expr.lt("dispatchTime", expiredTime), Expr.eq("judgeTime", null)))
                .in("language", languageList)
                .orderBy("id").findList();
        if (solutions.size() > 0) {
            Solution solution = solutions.get(0);
            solution.judge = (Judge) ctx().args.get("judge");
            solution.save();
            return responseSolutionToJudge(solution);
        } else {
            return ok(jsonResponse(1, "Currently no more solutions to check."));
        }
    }

    @JudgeAccess
    public static Result handleJudgeUpdateResult(long solutionId) {
        try {
            JsonNode in = request().body().asJson();
            int result = in.get("result").asInt();
            Integer timeUsed = in.get("time").asInt(-1);
            Integer memoryUsed = in.get("memory").asInt(-1);
            if (timeUsed < 0) timeUsed = null;
            if (memoryUsed < 0) memoryUsed = null;
            String detail = in.get("detail").asText();
            Solution solution = Solution.find.byId(solutionId);
            solution.judge = (Judge) ctx().args.get("judge");
            solution.result = result;
            solution.judgeResponse = detail;
            solution.timeUsed = timeUsed;
            solution.memoryUsed = memoryUsed;
            // When result is greater than 200, there will be no more update.
            if (result >= 200) {
                solution.judgeTime = new Date();
                if (result == 200) {
                    solution.user.removeSolvedProblemsCache();
                }
            }
            solution.save();
            return ok(jsonResponse(0, "Success."));
        } catch (NullPointerException e) {
            return ok(jsonResponse(2, "Illegal update request."));
        }
    }

    @JudgeAccess
    public static Result getProblemResourcesHash(Long id) {
        Problem problem = Problem.find.byId(id);
        return ok(jsonResponse(0, problem.resourcesHash));
    }

    @JudgeAccess
    public static Result getProblemResourcesZip(Long id) {
        String dir = "upload/problem/" + id;
        File uploadPath = new File(dir);
        if (!uploadPath.exists()) {
            return notFound("Problem resources not found.");
        }
        try {
            File zip = File.createTempFile("package", ".zip");
            ZipUtil.pack(uploadPath, zip);
            return ok(zip);
        } catch (IOException e) {
            Logger.info("Cannot create temp file.");
            e.printStackTrace();
            return badRequest("Server Error.");
        }
    }

}
