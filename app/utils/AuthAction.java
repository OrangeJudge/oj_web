package utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.routes;
import models.User;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Controller.session;

public class AuthAction extends Action<Authentication> {
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        String username = session("user");
        if (username == null) {
            if (configuration.json()) {
                ObjectNode out = Json.newObject();
                out.put("status", 1234);
                out.put("message", "Not log in");
                return F.Promise.pure(ok(out));
            } else {
                return F.Promise.promise(() ->
                                redirect(routes.UserController.loginPage())
                );
            }
        } else {
            User user = User.find.where().eq("name", username).findUnique();
            if (user == null || user.adminLevel < configuration.admin()) {
                if (configuration.json()) {
                    ObjectNode out = Json.newObject();
                    out.put("status", 2345);
                    out.put("message", "Member not found");
                    return F.Promise.pure(ok(out));
                } else {
                    return F.Promise.promise(() ->
                            redirect(routes.UserController.logoutRedirect())
                    );
                }
            }
            Logger.debug("Setting user in ctx.");
            ctx.args.put("user", user);
            return delegate.call(ctx);
        }
    }
}
