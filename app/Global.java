import models.Judge;
import models.Problem;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        Logger.info("Online judge is to start.");
        if (User.find.findRowCount() == 0) {
            Logger.info("Create default users.");
            User admin = new User();
            admin.name = "admin";
            admin.displayName = "Admin";
            admin.setPassword("admin");
            admin.email = "admin@orangejudge.com";
            admin.adminLevel = 1;
            admin.isEmailVerified = true;
            admin.save();
            User userTest = new User();
            userTest.name = "test";
            userTest.displayName = "Test";
            userTest.email = "test@orangejudge.com";
            userTest.setPassword("test");
            userTest.isEmailVerified = true;
            userTest.save();
        }
//        if (Problem.find.findRowCount() == 0) {
//            Problem problem = new Problem();
//            problem.title = "A + B Problem";
//            problem.slug = "1";
//            problem.list = 0;  // Normal
//            problem.save();
//        }
        if (Judge.find.findRowCount() == 0) {
            Judge judge = new Judge();
            judge.id = 1;
            judge.nickname = "Orange";
            judge.secret = "JUDGE_SECRET";
            judge.sort = 100;
            judge. save();
        }
        if (Judge.find.findRowCount() == 0) {
            Judge judge = new Judge();
            judge.id = 2;
            judge.nickname = "Orange X";
            judge.secret = "JUDGE_SECRET";
            judge.sort = 200;
            judge. save();
        }
    }

    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        return F.Promise.<Result>pure(notFound(
                views.html.info.notFound.render()
        ));
    }
}
