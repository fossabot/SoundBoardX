package io.github.xtonousou.soundboardx;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.ArrayList;

abstract class SoundStore {

    static ArrayList<Sound> getSelectedSounds(Context context) {
        switch (SharedPrefs.getInstance().getSelectedCategory()) {
            case 1:
                return getAllSounds(context);
            case 2:
                return getRingtones(context);
            case 3:
                return getNotifications(context);
        }
        return null;
    }

    static ArrayList<Sound> getAllSounds(Context context) {
        Resources res = context.getApplicationContext().getResources();

        TypedArray allSounds = res.obtainTypedArray(R.array.allSounds);
        TypedArray allSoundsIDs = res.obtainTypedArray(R.array.allSoundsIDs);

        ArrayList<Sound> sounds = new ArrayList<>();

        for (int i = 0; i < allSounds.length(); i++) {
            sounds.add(new Sound(allSounds.getString(i), allSoundsIDs.getResourceId(i, -1)));
        }

        allSounds.recycle();
        allSoundsIDs.recycle();

        return sounds;
    }

    static ArrayList<Sound> getRingtones(Context context) {
        Resources res = context.getApplicationContext().getResources();

        TypedArray ringtones = res.obtainTypedArray(R.array.ringtones);
        TypedArray ringtonesIDs = res.obtainTypedArray(R.array.ringtonesIDs);

        ArrayList<Sound> sounds = new ArrayList<>();

        final int funnySounds_length = ringtones.length();
        for (int i = 0; i < funnySounds_length; i++) {
            sounds.add(new Sound(ringtones.getString(i), ringtonesIDs.getResourceId(i, -1)));
        }

        ringtones.recycle();
        ringtonesIDs.recycle();

        return sounds;
    }

    static ArrayList<Sound> getNotifications(Context context) {
        Resources res = context.getApplicationContext().getResources();

        TypedArray notifications = res.obtainTypedArray(R.array.notifications);
        TypedArray notificationsIDs = res.obtainTypedArray(R.array.notificationsIDs);

        ArrayList<Sound> sounds = new ArrayList<>();

        final int funnySounds_length = notifications.length();
        for (int i = 0; i < funnySounds_length; i++) {
            sounds.add(new Sound(notifications.getString(i), notificationsIDs.getResourceId(i, -1)));
        }

        notifications.recycle();
        notificationsIDs.recycle();

        return sounds;
    }
}

