package utils;

import models.Judge;
import play.Play;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class JudgeAccessAction extends Action<JudgeAccessAction> {

    private boolean ctxContains(Http.Context ctx, String key) {
        return ctx.request().queryString().get(key) != null
                && ctx.request().queryString().get(key).length > 0;
    }

    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        if (ctxContains(ctx, "judge") && ctxContains(ctx, "secret")) {
            Judge judge = Judge.find.byId(Integer.parseInt(ctx.request().queryString().get("judge")[0]));
            if (judge != null &&
                    judge.secret.equals(ctx.request().queryString().get("secret")[0])) {
                ctx.args.put("judge", judge);
                return delegate.call(ctx);
            }
        }
        return F.Promise.pure(badRequest("Judge is not authenticated."));
    }
}