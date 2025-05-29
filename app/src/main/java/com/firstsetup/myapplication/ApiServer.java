package com.firstsetup.myapplication;

    import com.firstsetup.myapplication.model.User;

    import retrofit2.Call;
    import retrofit2.http.Body;
    import retrofit2.http.GET;
    import retrofit2.http.POST;

    import java.util.List;

    public class ApiServer {
        public interface ApiService {
            @GET("/users") // L'URL de ton serveur Node.js pour obtenir les utilisateurs
            Call<List<User>> getUsers(); // Liste des utilisateurs que tu récupères

            @POST("/register")
            Call<Void> registerUser(@Body User user);
        }
    }


