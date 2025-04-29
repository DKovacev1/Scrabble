package hr.java.scrabble.validations;

import hr.java.scrabble.config.ConfigReader;
import hr.java.scrabble.config.jndi.ConfigurationKey;
import hr.java.scrabble.utils.BasicDialogUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

@NoArgsConstructor
public class ApiWordValidation implements WordValidation {

    @Override
    public boolean isWordValid(String word) {
        if (word.length() > 1 && Boolean.parseBoolean(ConfigReader.getValue(ConfigurationKey.DO_API_VALIDATIONS))) {
            HttpGet request = new HttpGet(ConfigReader.getValue(ConfigurationKey.API_URL) + word);
            request.addHeader(ConfigReader.getValue(ConfigurationKey.X_API_KEY), ConfigReader.getValue(ConfigurationKey.API_KEY));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    System.err.println("Failed to fetch data. HTTP error code: " + statusCode);
                    return false;
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String json = EntityUtils.toString(entity);

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(json);

                    // Map JSON fields to ApiResponse object
                    String definition = jsonObject.getString("definition");
                    boolean valid = jsonObject.getBoolean("valid");

                    // Create ApiResponse object
                    ApiWordValidation.ApiResponse apiResponse = new ApiResponse(definition, word, valid);

                    // Print ApiResponse object
                    //System.out.println("ApiResponse: " + apiResponse);

                    // You can return the ApiResponse object or extract required data from it
                    if (!apiResponse.isValid()) {
                        BasicDialogUtility.showDialog("Word validation error", "Word '" + word + "' is not valid!");
                        return false;
                    }
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else
            return true;

        return true;
    }


    @Setter
    @Getter
    public static class ApiResponse{
        private String definition;
        private String word;
        private boolean valid;

        public ApiResponse(String definition, String word, boolean valid) {
            this.definition = definition;
            this.word = word;
            this.valid = valid;
        }

        @Override
        public String toString() {
            return "ApiResponse{" +
                    ", word='" + word + '\'' +
                    ", valid=" + valid +
                    '}';
        }
    }

}
