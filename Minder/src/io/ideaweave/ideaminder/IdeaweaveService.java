package io.ideaweave.ideaminder;

import java.util.List;

import io.ideaweave.models.Idea;
import io.ideaweave.models.IdeaWithOwner;
import io.ideaweave.models.Profile;
import io.ideaweave.models.Tag;
import io.ideaweave.models.User;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.*;

public interface IdeaweaveService {

	@POST("/auth/login")
	Observable<ResultMessage> login(@Body User user);
	
	@GET("/auth/signout")
	void logout();
	
	@GET("/challenges")
	void getChallenges(Callback<List<Challenge>> challenges);
	
	@GET("/ideas/{id}")
	void getIdea(@Path("id") String idea_id, Callback<Idea> idea);
	
	@PUT("/ideas/{id}")
	Observable<ResultMessage> updateIdea(@Header("Authorization") String authorization, @Path("id") String idea_id, @Body Idea idea);
	
	@DELETE("/ideas/{id}")
	Observable<ResultMessage> deleteIdea(@Header("Authorization") String authorization, @Path("id") String idea_id);
	
	@GET("/ideas/{id}/tags")
	void getIdeaTagList(@Path("id") String idea_id, Callback<List<String>> tags);
	
	@GET("/ideas/{id}/ratings")
	void getIdeaRatings(@Path("id") String idea_id, Callback<List<Number>> ratings);
	
	@GET("/profile/{id}/ideas")
	void getMyIdeas(@Header("Authorization") String authorization, @Path("id") String user_id, Callback<List<Idea>> myIdeas);
	
	@GET("/profile/{id}/likes")
	void getLikedIdeas(@Header("Authorization") String authorization, @Path("id") String user_id, Callback<List<IdeaWithOwner>> likedIdeas);
	
	@POST("/ideas")
	Observable<ResultMessage> createIdea(@Header("Authorization") String authorization, @Body Idea ideaBody);
	
	@GET("/ideas/popular")
	void popularIdea(@Header("Authorization") String authorization, Callback<IdeaWithOwner> idea);
	
	@PUT("/ideas/{id}/like")
	Observable<ResultMessage> likeIdea(@Header("Authorization") String authorization, @Path("id") String idea_id, @Query("liker") String liker);
	
	@DELETE("/ideas/{id}/like")
	Observable<ResultMessage> deleteLikeIdea(@Header("Authorization") String authorization, @Path("id") String idea_id);
	
	//dynamic query?
	@PUT("/ideas/{id}/dislike")
	Observable<ResultMessage> dislikeIdea(@Header("Authorization") String authorization, @Path("id") String idea_id, @Query("disliker") String disliker);
	
	@GET("/profile/{id}")
	void getProfile(@Path("id") String project_id, Callback<Profile> profile);

	@GET("/tags")
	void getTags(Callback<List<Tag>> tags);
	
	@POST("/tags")
	Observable<ResultMessage> createTag(@Body Tag tag);
}
