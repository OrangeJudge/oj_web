package controllers;

import play.*;
import play.mvc.*;

public class PortalController extends OJController {

    public static Result index() {
        Logger.debug("Prepare to serve index.");
        return ok(views.html.portal.index.render());
    }

    public static Result about() {
        return redirect("http://blog.orangejudge.com/about/");
    }

    public static Result terms() {
        return redirect("http://blog.orangejudge.com/about/terms/");
    }

    public static Result contactUs() {
        return redirect("http://blog.orangejudge.com/about/contact-us/");
    }

}
