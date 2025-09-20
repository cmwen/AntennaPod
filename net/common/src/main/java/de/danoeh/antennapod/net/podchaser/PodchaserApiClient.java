package de.danoeh.antennapod.net.podchaser;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.danoeh.antennapod.net.podchaser.model.Episode;
import de.danoeh.antennapod.net.podchaser.model.EpisodeList;
import de.danoeh.antennapod.net.podchaser.model.GraphQlResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PodchaserApiClient {
    private static final String TAG = "PodchaserApiClient";
    private static final String API_URL = "https://api.podchaser.com/graphql";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson = new Gson();

    public PodchaserApiClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String requestAccessToken(String clientId, String clientSecret) throws IOException {
        String query = "mutation { requestAccessToken( input: { grant_type: CLIENT_CREDENTIALS, client_id: \""
                + clientId + "\", client_secret: \"" + clientSecret
                + "\"} ) { access_token } }";

        RequestBody body = RequestBody.create(gson.toJson(new GraphQlQuery(query)), JSON);
        Request request = new Request.Builder().url(API_URL).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            Log.d(TAG, "requestAccessToken response: " + responseBody);

            Type type = new TypeToken<GraphQlResponse<AccessTokenResponse>>() {}.getType();
            GraphQlResponse<AccessTokenResponse> gqlResponse = gson.fromJson(responseBody, type);

            if (gqlResponse != null && gqlResponse.data != null && gqlResponse.data.requestAccessToken != null) {
                return gqlResponse.data.requestAccessToken.accessToken;
            } else {
                throw new IOException("Could not parse access token from response");
            }
        }
    }

    public List<Episode> searchEpisodes(String accessToken, String searchTerm) throws IOException {
        if (TextUtils.isEmpty(accessToken)) {
            throw new IllegalArgumentException("Access token cannot be empty");
        }

        String query = "query { episodes(searchTerm: \\\"" + searchTerm
                + "\\\") { data { podcast { title imageUrl } title, airDate, description, audioUrl, length } } }";

        RequestBody body = RequestBody.create(gson.toJson(new GraphQlQuery(query)), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            Log.d(TAG, "searchEpisodes response: " + responseBody);

            Type type = new TypeToken<GraphQlResponse<EpisodesResponse>>() {}.getType();
            GraphQlResponse<EpisodesResponse> gqlResponse = gson.fromJson(responseBody, type);

            if (gqlResponse != null && gqlResponse.data != null && gqlResponse.data.episodes != null) {
                return gqlResponse.data.episodes.data;
            } else {
                return null;
            }
        }
    }

    private static class GraphQlQuery {
        final String query;

        GraphQlQuery(String query) {
            this.query = query;
        }
    }

    private static class AccessTokenResponse {
        RequestAccessToken requestAccessToken;
    }

    private static class RequestAccessToken {
        @SerializedName("access_token")
        String accessToken;
    }

    private static class EpisodesResponse {
        EpisodeList episodes;
    }
}
