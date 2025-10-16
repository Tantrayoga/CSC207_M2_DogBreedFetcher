package dogapi;

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
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException("Breed must be non-empty");
        }

        final String normalized = breed.trim().toLowerCase(Locale.ROOT);
        final String url;
        try {
            url = "https://dog.ceo/api/breed/"
                    + java.net.URLEncoder.encode(normalized, java.nio.charset.StandardCharsets.UTF_8.name())
                    + "/list";
        } catch (Exception e) {
            // (Extremely rare) encoding issue
            throw new BreedNotFoundException("Failed to encode breed name", e);
        }

        Request request = new Request.Builder().url(url).build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) {
                throw new BreedNotFoundException("API call failed with HTTP " + resp.code());
            }

            String body = resp.body().string();
            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "error");

            if ("error".equalsIgnoreCase(status)) {
                String msg = json.optString("message", "Breed not found");
                throw new BreedNotFoundException(msg);
            }

            JSONArray arr = json.optJSONArray("message");
            if (arr == null) {
                throw new BreedNotFoundException("Unexpected API response shape");
            }

            List<String> subBreeds = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                subBreeds.add(arr.getString(i));
            }
            return subBreeds;
        } catch (BreedNotFoundException e) {
            throw e; // keep the exact exception type per assignment contract
        } catch (IOException | org.json.JSONException e) {
            // Convert any IO/JSON failure to the required exception type
            throw new BreedNotFoundException("Failed to fetch sub-breeds", e);
        }
    }
}
