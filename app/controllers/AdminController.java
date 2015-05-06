package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Contest;
import models.Problem;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utils.Authentication;
import utils.FileHashing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Authentication(admin = 1)
public class AdminController extends OJController {

    public static Result adminHomePage() {
        return ok(views.html.admin.home.render());
    }

    public static Result userListPage() {
        List<User> users = User.find.all();
        return ok(views.html.admin.userList.render(users));
    }

    public static Result problemListPage() {
        List<Problem> problems = Problem.find.all();
        return ok(views.html.admin.problemList.render(problems));
    }

    public static Result problemEditPage(Long id) {
        Problem problem = Problem.find.byId(id);
        if (problem == null) {
            return redirect(routes.AdminController.problemListPage());
        }
        return ok(views.html.admin.problemEdit.render(problem));
    }

    public static Result problemResourcesPage(Long id) {
        Problem problem = Problem.find.byId(id);
        if (problem == null) {
            return redirect(routes.AdminController.problemListPage());
        }
        return ok(views.html.admin.problemResources.render(problem));
    }

    public static Result problemEditRedirect(Long id) {
        DynamicForm in = Form.form().bindFromRequest();
        Problem problem = Problem.find.byId(id);
        problem.title = in.get("title");
        problem.slug = in.get("slug");
        problem.status = Integer.parseInt(in.get("status"));
        problem.description = in.get("description");
        problem.timeLimit = Integer.parseInt(in.get("timeLimit"));
        problem.memoryLimit = Integer.parseInt(in.get("memoryLimit"));
        problem.specialJudge = in.get("specialJudge").equals("1");
        problem.tags = in.get("tags");
        problem.source = in.get("source");
        problem.lastModifyTime = new Date();
        problem.update();
        return redirect(routes.AdminController.problemEditPage(id));
    }

    public static Result contestListPage() {
        List<Contest> contests = Contest.find.all();
        return ok(views.html.admin.contestList.render(contests));
    }

    public static Result editUserPage(long id) {
        User user = User.find.byId(id);
        if (user == null) {
            return notFound("User not found.");
        }
        return ok(views.html.admin.userDetail.render(user));
    }

    public static Result deleteUser(long id) {
        User user = User.find.byId(id);
        if (user == null) {
            return ok(jsonResponse(1, "User not found."));
        }
        if (user.status > 0) {
            return ok(jsonResponse(3, "User has been deleted."));
        }
        if (user.adminLevel > 0) {
            return ok(jsonResponse(2, "You are not allowed to delete a system admin."));
        }
        user.pendingDelete();
        return ok(jsonResponse(0, null));
    }

    public static Result createProblem(String slug) {
        Problem problem = new Problem();
        problem.slug = slug;
        problem.save();
        return ok(jsonResponse(0, null));
    }

    private static String problemHash(Long id) {
        String dir = "upload/problem/" + id;
        File uploadPath = new File(dir);
        if (!uploadPath.exists()) {
            return "";
        }
        return FileHashing.calcMD5HashForDir(uploadPath);
    }

    @Authentication(admin = 1, json = true)
    public static synchronized Result handleUploadAssetFile(Long id) {
        Problem problem = Problem.find.byId(id);

        if (problem == null) {
            return ok(jsonResponse(5, "Problem not found."));
        }

        String[] dirList = {"upload", "assets", id.toString()};

        Result errorResponse = uploadFile(dirList);

        if (errorResponse != null) {
            return errorResponse;
        }

        return ok(jsonResponse(0, null));
    }

    @Authentication(admin = 1, json = true)
    public static synchronized Result handleUploadResourceFile(Long id) {
        Problem problem = Problem.find.byId(id);

        if (problem == null) {
            return ok(jsonResponse(5, "Problem not found."));
        }

        String[] dirList = {"upload", "problem", id.toString()};

        Result errorResponse = uploadFile(dirList);

        if (errorResponse != null) {
            return errorResponse;
        }

        problem.resourcesHash = problemHash(id);
        problem.save();

        return ok(jsonResponse(0, null));
    }

    @Authentication(admin = 1, json = true)
    public static synchronized Result handleUploadProblemPackage() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        try {
            Problem problem = Problem.importZipFile(file.getFile());
            return ok(jsonResponse(0, problem));
        } catch (IOException e) {
            e.printStackTrace();
            return ok(jsonResponse(1, "Error importing files."));
        }
    }

    public static Result listAssetFiles(Long id) {
        String dir = "upload/assets/" + id;
        return listDirectory(dir);
    }

    public static Result listResourceFiles(Long id) {
        String dir = "upload/problem/" + id;
        return listDirectory(dir);
    }

    @Authentication(admin = 1, json = true)
    public static Result resourceFilePreview(Long id, String filename) {
        String path = "upload/problem/" + id + "/" + filename;
        File file = new File(path);
        String preview = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                preview += scanner.nextLine() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            return ok(jsonResponse(1, "Not Found"));
        }
        return ok(jsonResponse(0, preview));
    }

    public static Result deleteAssetFile(Long id, String filename) {
        String path = "upload/assets/" + id + "/" + filename;
        return deleteFile(path);
    }

    public static Result deleteResourceFile(Long id, String filename) {
        String path = "upload/problem/" + id + "/" + filename;
        return deleteFile(path);
    }

    public static Result exportProblem(long problemId) {
        Problem problem = Problem.find.byId(problemId);
        try {
            return ok(problem.problemZipFile());
        } catch (IOException e) {
            Logger.debug(e.toString());
            e.printStackTrace();
            return ok(jsonResponse(1, "Unable to export problem."));
        }
    }
}
