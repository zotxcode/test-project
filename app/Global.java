import akka.actor.Cancellable;
import assets.Tool;
import com.enwie.scheduler.DailyJob;
import com.enwie.scheduler.HourlyJob;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hendriksaragih
 */
public class Global extends GlobalSettings {
    private List<Cancellable> jobs = new ArrayList<>(1);

    private class ActionWrapper extends Action.Simple {
        public ActionWrapper(Action<?> action) {
            this.delegate = action;
        }

        @Override
        public Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
            Promise<Result> result = this.delegate.call(ctx);
            Http.Response response = ctx.response();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, " +
                    "X-Requested-With, X-API_KEY, x-api_key, API_KEY, api_key, Api-Key," +
                    "X-Token, x-token, Token, token");
            return result;
        }
    }

    @Override
    public Action<?> onRequest(Http.Request request, java.lang.reflect.Method actionMethod) {
        Logger.info("\nREQUEST [" + request.method() + "] " + request.host() + request.uri());
        if (request.body().asJson() != null) {
            Logger.info(Tool.prettyPrint(request.body().asJson()));
        }
        Logger.info(new Date() + "\n");
        return new ActionWrapper(super.onRequest(request, actionMethod));
    }

    @Override
    public void onStart(Application app) {
        super.onStart(app);
		Seed.seedTestingRoleFeatureUser();
		Seed.seedSettings();
		Seed.seedTestingProduct();

//        jobs.add(new ServiceJob("* * * * * ?").scheduleIntervalMinutes());
//
//        jobs.add(new DailyJob("0 0 1 * * ?").scheduleInterval());
        jobs.add(new DailyJob("0 0 1 * * ?").scheduleInterval());
        jobs.add(new HourlyJob("0 0 0/1 * * ?").scheduleInterval());
    }

    @Override
    public void onStop(Application app)
    {
        Logger.info("Global.onStop...");
        Logger.info("Jobs Cancellable.");
        for (Cancellable sch : jobs) {
            sch.cancel();
        }

        super.onStop(app);
    }

}