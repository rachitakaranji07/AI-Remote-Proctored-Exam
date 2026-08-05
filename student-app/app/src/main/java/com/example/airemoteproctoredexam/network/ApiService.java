package com.example.airemoteproctoredexam.network;

import com.google.gson.JsonObject;
import retrofit2.http.Header;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;
public interface ApiService {

    @POST("api/auth/login")
    Call<JsonObject> login(@Body JsonObject loginData);

    @POST("api/auth/register")
    Call<JsonObject> register(@Body JsonObject registerData);
    @POST("api/exams/create")
    Call<JsonObject> createExam(
            @Header("Authorization") String token,
            @Body JsonObject examData
    );
    @GET("api/exams/")
    Call<JsonObject> getAllExams();
    @POST("api/questions/add")
    Call<JsonObject> addQuestion(
            @Header("Authorization") String token,
            @Body JsonObject questionData
    );
    @GET("api/questions/exam/{exam_id}")
    Call<JsonObject> getQuestionsByExam(
            @Path("exam_id") int examId
    );
    @DELETE("api/questions/{question_id}")
    Call<JsonObject> deleteQuestion(
            @Header("Authorization") String token,
            @Path("question_id") int questionId
    );
    @PUT("api/questions/{question_id}")
    Call<JsonObject> updateQuestion(
            @Header("Authorization") String token,
            @Path("question_id") int questionId,
            @Body JsonObject questionData
    );
    @PUT("api/exams/{exam_id}")
    Call<JsonObject> updateExam(
            @Header("Authorization") String token,
            @Path("exam_id") int examId,
            @Body JsonObject examData
    );
    @DELETE("api/exams/{exam_id}")
    Call<JsonObject> deleteExam(
            @Header("Authorization") String token,
            @Path("exam_id") int examId
    );
    @GET("api/questions/exam/{exam_id}/student")
    Call<JsonObject> getStudentQuestions(
            @Header("Authorization") String token,
            @Path("exam_id") int examId
    );
    @POST("api/submissions/submit")
    Call<JsonObject> submitExam(
            @Header("Authorization") String token,
            @Body JsonObject submissionData
    );
    @GET("api/submissions/result/{exam_id}")
    Call<JsonObject> getExamResult(
            @Header("Authorization") String token,
            @Path("exam_id") int examId
    );
}