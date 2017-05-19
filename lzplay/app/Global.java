import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import rss2mail.Rss2Mail;
import scala.App;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by cussyou on 16-12-29.
 */
public class Global extends GlobalSettings {
    @Override
    public void onStart(Application application) {
        super.onStart(application);
        Logger.debug("Application started");

        // start some global actions
        Rss2Mail.startTask();
    }

    @Override
    public void onStop(Application app) {
        Logger.debug("Application stopped");
        super.onStop(app);
    }
}
