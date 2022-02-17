package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.core.text.HtmlCompat;
import androidx.core.util.Pair;

import com.codepath.apps.restclienttemplate.utils.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parcel
public class Tweet {
    public String body = "";
    public String createdAt;
    public long id;
    public String relativeTimestamp;
    public String timestamp;
    public String nativeImageUrl;
    public HashMap<String, int[]> nativeImagePair = new HashMap<>();
    public User user;
    public int retweetCount;
    public int favoritesCount;
    public boolean retweeted = false;
    public boolean liked = false;

    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        JSONObject nativeImageDimens;
        int[] dimens;

        boolean isRetweet = jsonObject.getString("full_text").startsWith("RT");
        try {
            JSONObject mediaObject = getTwimage(jsonObject.getJSONObject("entities").getJSONArray("media"));
            if(mediaObject != null) {
                tweet.nativeImageUrl = mediaObject.getString("media_url_https");
                nativeImageDimens = mediaObject.getJSONObject("sizes").getJSONObject("medium");
                dimens = new int[]{nativeImageDimens.getInt("w"), nativeImageDimens.getInt("h")};
                tweet.nativeImagePair.put(tweet.nativeImageUrl, dimens);
            }
        } catch (JSONException ignored) {
        }
        tweet.id = jsonObject.getLong("id");
        tweet.body += isRetweet ? "RT " : "";
        tweet.body += (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getString("full_text");
        tweet.createdAt = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getString("created_at");
        tweet.user = User.fromJson((isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getJSONObject("user"));
        tweet.timestamp = TimeFormatter.getTimeStamp(tweet.createdAt);
        tweet.relativeTimestamp = TimeFormatter.getTimeDifference(tweet.createdAt);
        tweet.retweeted = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getBoolean("retweeted");
        tweet.liked = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getBoolean("favorited");
        tweet.retweetCount = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getInt("retweet_count");
        tweet.favoritesCount = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getInt("favorite_count");
        return tweet;
    }

    private static JSONObject getTwimage(JSONArray media) throws JSONException {
        for(int i = 0; i < media.length(); i++) {
            JSONObject mediaObject = media.getJSONObject(i);
            if(mediaObject.getString("type").equals("photo")) {
                return mediaObject;
            }
        }
        return null;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;

    }

    public void retweet(boolean status) {
        this.retweeted = status;
    }

    public void like(boolean status) {
        this.liked = status;
    }
}
