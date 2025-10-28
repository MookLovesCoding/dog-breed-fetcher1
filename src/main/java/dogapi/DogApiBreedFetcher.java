package dogapi;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // TODO Task 1: Complete this method based on its provided documentation
        //      and the documentation for the dog.ceo API. You may find it helpful
        //      to refer to the examples of using OkHttpClient from the last lab,
        //      as well as the code for parsing JSON responses.
        // return statement included so that the starter code can compile and run.
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException(String.valueOf(breed));
        }

        final String normalized = breed.trim().toLowerCase(Locale.ROOT);

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("dog.ceo")
                .addPathSegment("api")
                .addPathSegment("breed")
                .addPathSegment(normalized)
                .addPathSegment("list")
                .build();

        Request request = new Request.Builder()
                .url(String.valueOf(url))
                .get()
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            String payload = response.body().string();
            JSONObject json = new JSONObject(payload);

            String status = json.optString("status", "");
            if (!response.isSuccessful() || !"success".equalsIgnoreCase(status)) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray msg = json.optJSONArray("message");

            List<String> result = new ArrayList<>(msg.length());
            for (int i = 0; i < msg.length(); i++) {
                result.add(msg.getString(i));
            }
            Collections.sort(result);
            return result;
        } catch (IOException | RuntimeException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}