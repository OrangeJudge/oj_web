package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class ContestController extends Controller {
    public static Result contestListPage() {
        return ok(views.html.contest.list.render());
    }
}
