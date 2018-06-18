package io.futurestud.retrofit1.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import io.futurestud.retrofit1.R;
import io.futurestud.retrofit1.api.model.AccessToken;
import io.futurestud.retrofit1.api.model.GitHubRepo;
import io.futurestud.retrofit1.api.service.GitHubClient;
import io.futurestud.retrofit1.ui.adapter.GitHubRepoAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private String clientId = "1afb9e321a92e7b46a41";
    private String clientSecret = "1b89b66f7b4e7527b1685ffbc68c1154fb0717a5";
    private String redirectUri = "futurestudio://callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.pagination_list);

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&scope=repo&redirect_uri=" + redirectUri));
        startActivity(intent);

//        showPublicGithubRepo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)){

            String code = uri.getQueryParameter("code");

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("https://github.com/")
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            GitHubClient client = retrofit.create(GitHubClient.class);
            Call<AccessToken> accessTokenCall = client.getAccessToken(
                    clientId,
                    clientSecret,
                    code
            );

            accessTokenCall.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "No", Toast.LENGTH_SHORT).show();

                }
            });

            Toast.makeText(this, "Wow", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPublicGithubRepo() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        GitHubClient client = retrofit.create(GitHubClient.class);
        Call<List<GitHubRepo>> call = client.reposForUser("fs-opensource");

        call.enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                List<GitHubRepo> repos = response.body();

                listView.setAdapter(new GitHubRepoAdapter(MainActivity.this, repos));
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

}


