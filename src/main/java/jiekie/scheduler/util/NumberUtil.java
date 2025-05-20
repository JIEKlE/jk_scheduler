package jiekie.scheduler.util;

import jiekie.scheduler.exception.SchedulerException;

public class NumberUtil {
    public static int getIntervalFromString(String intervalString) throws SchedulerException {
        int interval;
        try {
            interval = Integer.parseInt(intervalString);
        } catch (NumberFormatException e) {
            throw new SchedulerException(ChatUtil.INTERVAL_NOT_NUMBER);
        }

        if(interval < 1)
            throw new SchedulerException(ChatUtil.INTERVAL_LESS_THAN_ONE);

        return interval;
    }

    public static double getCoordinateFromString(String coordinateString) throws SchedulerException {
        double coordinate;
        try {
            coordinate = Double.parseDouble(coordinateString);
        } catch (NumberFormatException e) {
            throw new SchedulerException(ChatUtil.COORDINATE_NOT_NUMBER);
        }

        return coordinate;
    }

    public static int getRankFromString(String rankString) throws SchedulerException {
        int rank;
        try {
            rank = Integer.parseInt(rankString);
        } catch (NumberFormatException e) {
            throw new SchedulerException(ChatUtil.RANK_NOT_NUMBER);
        }

        if(rank < 1)
            throw new SchedulerException(ChatUtil.RANK_LESS_THAN_ONE);

        return rank;
    }

    public static float getVolumeFromString(String volumeString) throws SchedulerException {
        float volume;
        try {
            volume = Float.parseFloat(volumeString);
        } catch (NumberFormatException e) {
            throw new SchedulerException(ChatUtil.VOLUME_NOT_NUMBER);
        }

        if(volume < 0.0)
            throw new SchedulerException(ChatUtil.MINUS_VOLUME);

        return volume;
    }

    public static int getSoundLengthFromString(String soundLengthString) throws SchedulerException {
        int soundLength;
        try {
            soundLength = Integer.parseInt(soundLengthString);
        } catch (NumberFormatException e) {
            throw new SchedulerException(ChatUtil.SOUND_LENGTH_NOT_NUMBER);
        }

        if(soundLength < 1)
            throw new SchedulerException(ChatUtil.SOUND_LENGTH_LESS_THAN_ONE);

        return soundLength;
    }
}
