package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import utils.Authentication;

import javax.persistence.PersistenceException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class UserController extends OJController {

    public static Result userListPage() {
        List<User> users = User.find.all();
        return ok(views.html.user.list.render(users));
    }

    public static Result registerPage() {
        return ok(views.html.user.register.render());
    }

    public static Result loginPage() {
        return ok(views.html.user.login.render());
    }

    public static Result logoutRedirect() {
        session().remove("user");
        return redirect(routes.UserController.loginPage());
    }

    public static Result verifyEmail(String username, String verificationPass) {
        User user = User.find.where().eq("name", username).findUnique();
        if (user == null) {
            String message = "The account \"" + username + "\" you want to verify is not found on our system. "
                    + "This might be because you have did verify your email within 24 hours and "
                    + " your account has been deleted automatically.";
            return badRequest(views.html.info.message.render("Account Not Found", message));
        }
        if (user.getVerificationPass().equals(verificationPass)) {
            user.isEmailVerified = true;
            user.save();
            String message = "This account \"" + username + "\" has been successfully verified. "
                    + "Thank you for your support.";
            return badRequest(views.html.info.message.render("Email Verified", message));
        } else {
            String message = "Sorry. This account \"" + username + "\" could not be verified. "
                    + " The verification link is not valid.";
            return badRequest(views.html.info.message.render("Invalid Verification Link", message));
        }
    }

    public static User currentUser() {
        User user = (User) ctx().args.get("user");
        if (user == null) {
            Logger.info("User not exist in context. Try to read from session.");
            user = User.find.where().eq("name", session("user")).findUnique();
            ctx().args.put("user", user);
        }
        return user;
    }

    public static Result getSolvedProblems(String username) {
        User user = User.find.where().eq("name", username).findUnique();
        if (user == null) {
            return notFound(jsonResponse(1, "User not found"));
        }
        return ok(jsonResponse(0, user.getSolvedProblems()));
    }

    public static Result getSolvedProblemsPage(String username) {
        User user = User.find.where().eq("name", username).findUnique();
        if (user == null) {
            return notFound("User not found");
        }
        return ProblemController.problemListPage(user);
    }

    @Authentication
    public static Result getMySolvedProblemsPage() {
        User user = (User) ctx().args.get("user");
        return redirect(routes.UserController.getSolvedProblemsPage(user.name));
    }


    @Authentication
    public static Result myProfilePage() {
        User user = (User) ctx().args.get("user");
        return redirect(routes.UserController.profilePage(user.name));
    }

    public static Result profilePage(String username) {
        User user = User.find.where().eq("name", username).findUnique();
        if (user == null) {
            return notFound("User Not Found.");
        }
        return ok(views.html.user.profile.render(user));
    }

    public static Result profileImage(String username) {
        User user = User.find.where().eq("name", username).findUnique();
        if (user == null) {
            return notFound("User Not Found.");
        }
        Path path = user.getProfileImagePath();
        if (Files.exists(path)) {
            return ok(path.toFile());
        } else {
            return redirect(routes.Assets.versioned(new Assets.Asset("images/avatar.jpg")));
        }
    }

    public static Result userProfile(String username) {
        User user = User.find.where().eq("name", username).findUnique();
        if (user == null) {
            return notFound(jsonResponse(1, "User not found"));
        }
        return ok(jsonResponse(0, user));
    }

    @Authentication
    public static Result editProfilePage() {
        return ok(views.html.user.profileEdit.render());
    }

    @Authentication
    public static Result mySolutionListRedirect() {
        User user = (User) ctx().args.get("user");
        return redirect(routes.SolutionController.solutionListPage() + "?user=" + user.name);
    }

    public static Result userSolutionListRedirect(String username) {
        User user = User.find.where().eq("name", username).findUnique();
        if (user == null) {
            return notFound(jsonResponse(1, "User not found"));
        }
        return redirect(routes.SolutionController.solutionListPage() + "?user=" + user.name);
    }

    public static Result register() {
        JsonNode in = request().body().asJson();
        if (in == null) {
            return formSubmitResponse(1, null, "Expecting Json data.");
        }
        try {
            User user = new User();
            String username = in.get("username").textValue();
            if (username == null) {
                return formSubmitResponse(1, "username", "Username cannot be empty.");
            }
            if (username.length() < 4 || username.length() > 32) {
                return formSubmitResponse(1, "username", "The length of username should be between 4 and 32.");
            }
            if (!username.matches("^(\\w+)$")) {
                return formSubmitResponse(1, "username", "Username contains illegal characters.");
            }
            if (User.find.where().eq("name", username).findRowCount() > 0) {
                return formSubmitResponse(1, "username", "Username is taken.");
            }
            user.name = username;
            String email = in.get("email").textValue();
            user.email = email;
            String emailException = user.setEmail(email);
            if (emailException != null) {
                return formSubmitResponse(1, "email", emailException);
            }
            user.displayName = in.get("displayName").textValue().trim();
            user.school = in.get("school").textValue().trim();
            user.country = in.get("country").textValue().trim();
            user.gender = in.get("gender").booleanValue();
            String password = in.get("password").textValue();
            user.setPassword(password);
            user.save();
            user.refresh();  // The date in Java and MySQL has different precisions
            user.sendVerifyEmail();
        } catch (PersistenceException e) {
            // Database change after checking.
            return formSubmitResponse(1, null, "Server encountered a problem, please try again.");
        } catch (NullPointerException npe) {
            return formSubmitResponse(2, null, "Please check your input.");
        }
        return formSubmitResponse(0, null, null);
    }

    public static Result login() {
        JsonNode in = request().body().asJson();
        if (in == null) {
            return formSubmitResponse(1, null, "Expecting Json data.");
        }
        User.updateStatus();
        String username = in.get("username").textValue();
        String password = in.get("password").textValue();
        User user;
        if (username.contains("@")) {
            user = User.find.where().eq("email", username).findUnique();
        } else {
            user = User.find.where().eq("name", username).findUnique();
        }
        if (user == null) {
            return formSubmitResponse(1, "username", "No such account found.");
        }
        if (!user.verifyPassword(password)) {
            return formSubmitResponse(2, "password", "Password is incorrect.");
        }
        if (user.status > 0) {
            return formSubmitResponse(3, null, "The user has been deleted.");
        }
        session("user", user.name);
        return formSubmitResponse(0, null, null);
    }

    @Authentication(json = true)
    public static Result uploadProfileImage() {
        Logger.info("Handle upload profile image.");
        User user = (User) ctx().args.get("user");
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFile = body.getFile("file");
        File file = uploadFile.getFile();
        Logger.debug("File type: " + uploadFile.getContentType());
        switch (uploadFile.getContentType()) {
            case "image/jpeg":
                break;
            default:
                return badRequest(jsonResponse(1, "Unsupported file format."));
        }
        try {
            user.setProfileImage(file);
        } catch (Exception e) {
            return internalServerError(jsonResponse(2, e.toString()));
        }
        return ok(jsonResponse(0, null));
    }

    @Authentication(json = true)
    public static Result editBasicProfile() {
        JsonNode in = request().body().asJson();
        if (in == null) {
            return formSubmitResponse(1, null, "Expecting Json data.");
        }
        User user = (User) ctx().args.get("user");
        try {
            String email = in.get("email").textValue();
            if (!user.email.equals(email)) {
                String emailException = user.setEmail(email);
                if (emailException != null) {
                    return formSubmitResponse(1, "email", emailException);
                }
                user.sendVerifyEmail();
            }
            user.displayName = in.get("displayName").textValue().trim();
            String country = in.get("country").textValue();
            if (country != null) {
                country = country.trim();
                // TODO: check if country code is valid.
            }
            user.country = country;
            user.school = in.get("school").textValue().trim();
            user.gender = in.get("gender").booleanValue();
            user.save();
        } catch (NullPointerException npe) {
            return formSubmitResponse(3, null, "Invalid input data.");
        }
        return formSubmitResponse(0, null, null);
    }

    @Authentication(json = true)
    public static Result changePassword() {
        JsonNode in = request().body().asJson();
        if (in == null) {
            return formSubmitResponse(1, null, "Expecting Json data.");
        }
        User user = (User) ctx().args.get("user");
        String oldPassword = in.get("passwordOld").textValue();
        if (!user.verifyPassword(oldPassword)) {
            return formSubmitResponse(2, "password", "Current password is not correct.");
        }
        String newPassword = in.get("password").textValue();
        // TODO: Check if password is valid.
        user.setPassword(newPassword);
        user.save();
        return formSubmitResponse(0, null, null);
    }

    @Authentication(json = true)
    public static Result requestVerificationEmail() {
        User user = (User) ctx().args.get("user");
        user.sendVerifyEmail();
        return ok(jsonResponse(0, null));
    }
}
