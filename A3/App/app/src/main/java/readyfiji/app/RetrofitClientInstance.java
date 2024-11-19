package readyfiji.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://disasteraidhub.com/API/"; // Use your actual server URL

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Configure Gson with lenient parsing
            Gson gson = new GsonBuilder()
                    .setLenient()  // Enable lenient mode
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Use lenient Gson instance
                    .build();
        }
        return retrofit;
    }
}
