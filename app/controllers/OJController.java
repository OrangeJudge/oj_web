package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.util.ArrayList;

public class OJController extends Controller {
    public static String getSiteName() {
        return Play.application().configuration().getString("application.siteName");
    }

    public static String getSiteUrl() {
        return Play.application().configuration().getString("application.siteUrl");
    }

    protected static Result formSubmitResponse(int code, String field, String message) {
        ObjectNode out = Json.newObject();
        out.put("status", code);
        out.put("field", field);
        out.put("message", message);
        return ok(out);
    }

    protected static ObjectNode jsonResponse(int code, Object data) {
        ObjectNode out = Json.newObject();
        out.put("status", code);
        if (code > 0) {
            out.put("message", Json.toJson(data));
        } else {
            out.put("data", Json.toJson(data));
        }
        return out;
    }

    protected static Result uploadFile(String[] dirList) {
        String dir = "";
        for (String s : dirList) {
            dir += s + "/";
            File tmp = new File(dir);
            if (!(tmp.exists() && tmp.isDirectory())) {
                if (!tmp.mkdir()) {
                    return ok(jsonResponse(1, "Could not create upload directory."));
                }
            }
        }
        Logger.info("Upload dir " + dir);
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        if (file == null) {
            return ok(jsonResponse(2, "File is missing."));
        } else {
            File newPath = new File(dir + file.getFilename());
            if (newPath.exists()) {
                Logger.info("File " + newPath.getPath() + " will be overwritten.");
                if (!newPath.delete()) {
                    Logger.info("File " + newPath.getPath() + " cannot be overwritten.");
                    return ok(jsonResponse(3, "Unable to save file."));
                }
            }
            if (!file.getFile().renameTo(newPath)) {
                return ok(jsonResponse(3, "Unable to save file."));
            }
        }
        return null;
    }

    protected static Result listDirectory(String dir) {
        File uploadPath = new File(dir);
        if (!(uploadPath.exists() && uploadPath.isDirectory())) {
            Logger.info("Path " + uploadPath.getPath() + " does not exist.");
            return ok(jsonResponse(0, new ArrayList<>()));
        }
        ArrayList<ObjectNode> fileNameList = new ArrayList<>();
        for (File file : uploadPath.listFiles()) {
            ObjectNode fileNode = Json.newObject();
            fileNode.put("filename", file.getName());
            fileNode.put("size", file.length());
            fileNode.put("lastModified", file.lastModified());
            fileNameList.add(fileNode);
        }
        return ok(jsonResponse(0, fileNameList));
    }

    protected static Result deleteFile(String path) {
        File file = new File(path);
        if (file.delete()) {
            return ok(jsonResponse(0, null));
        } else {
            return ok(jsonResponse(1, "Cannot delete file."));
        }
    }

    public static boolean isOrangeJudgeDotCom() {
        return Play.application().configuration().getString("application.isOrangeJudgeDotCom").equals("true");
    }

    public static String trackingId() {
        return Play.application().configuration().getString("application.trackingId");
    }

}
