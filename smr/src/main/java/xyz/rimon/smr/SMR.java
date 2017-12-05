package xyz.rimon.smr;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;

import java.util.List;

import xyz.rimon.ael.commons.utils.StorageUtil;
import xyz.rimon.ael.domains.Event;
import xyz.rimon.ael.logger.Ael;
import xyz.rimon.smr.commons.Parser;
import xyz.rimon.smr.commons.Pref;
import xyz.rimon.smr.exceptions.InvalidException;
import xyz.rimon.smr.model.User;
import xyz.rimon.smr.service.ApiClient;

/**
 * Created by SAyEM on 4/12/17.
 */

public class SMR {

    public static void initialize(Context context, String clientId, String clientSecret, User user) throws InvalidException {
        if (clientId == null || clientSecret == null)
            throw new IllegalArgumentException("Client id or secret can not be null");

        Pref.saveCredentials(context, clientId, clientSecret, user);
        AndroidNetworking.initialize(context);
        ApiClient.registerUser(context, user);
    }


    public static void logOffline(Activity context, Event event) {
        Ael.logEvent(context, event);
    }

    public static void logOnline(final Activity context, Event event) {
        StorageUtil.writeObject(context, StorageUtil.TEMP_FILE_NAME, event);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Event> eventList = StorageUtil.readObjects(context, StorageUtil.TEMP_FILE_NAME);
                ApiClient.postEvent(context, eventList);
                Log.d("LOG_ONLINE", Parser.getGson().toJson(eventList));
            }
        }, 5000);
    }

}