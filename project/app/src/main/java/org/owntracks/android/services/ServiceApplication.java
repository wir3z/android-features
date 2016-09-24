package org.owntracks.android.services;

import org.greenrobot.eventbus.Subscribe;
import org.owntracks.android.App;
import org.owntracks.android.messages.MessageWaypoints;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.MessageWaypointCollection;
import org.owntracks.android.support.Preferences;
import org.owntracks.android.support.interfaces.ProxyableService;

import android.content.Intent;

import org.owntracks.android.support.unfree.GoogleApiAvailability;

public class ServiceApplication implements ProxyableService {

    @Override
    public void onCreate(ServiceProxy context) {
        checkPlayServices();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onStartCommand(Intent intent) {
    }

    @Subscribe
    public void onEvent(Events.Dummy event) {

    }
    public static boolean checkPlayServices() {
        return GoogleApiAvailability.checkPlayServices(App.getContext());
    }

    public boolean publishWaypointsMessage() {
        MessageWaypoints m = new MessageWaypoints();
        MessageWaypointCollection waypoints = Preferences.waypointsToJSON();
        if(waypoints == null)
            return false;

        m.setWaypoints(waypoints);

        ServiceProxy.getServiceMessage().sendMessage(m);
        return true;
    }
}
