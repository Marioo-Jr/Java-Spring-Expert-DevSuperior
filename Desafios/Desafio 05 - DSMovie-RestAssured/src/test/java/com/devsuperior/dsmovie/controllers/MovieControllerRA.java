package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {

	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String adminToken, clientToken, invalidToken;
	private Long existingMovieId, nonExistingMovieId;
	private String movieTitle;

	private Map<String, Object> postMovieInstance;

	@BeforeEach
	public void setup() throws JSONException {
		baseURI = "http://localhost:8080";

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto";

		movieTitle = "Star Wars";

		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Novo Filme de Teste");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}

	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
			.get("/movies")
		.then()
			.statusCode(200);
	}

	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		given()
			.get("/movies?title={movieTitle}", movieTitle)
		.then()
			.statusCode(200)
			.body("content.title", hasItems(
					"Star Wars: A Guerra dos Clones",
					"Star Wars: Episódio I - A Ameaça Fantasma"));
	}

	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		existingMovieId = 1L;

		given()
			.get("/movies/{id}", existingMovieId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("score", is(4.5F))
			.body("count", is(2))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		nonExistingMovieId = 100L;

		given()
			.get("/movies/{id}", nonExistingMovieId)
		.then()
			.statusCode(404)
			.body("error", equalTo("Recurso não encontrado"))
			.body("status", equalTo(404));
	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		postMovieInstance.put("title", "      ");
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie.toString())
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(422)
			.body("errors.message", hasItems("Campo requerido"));
	}

	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newMovie.toString())
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
	}

	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(newMovie.toString())
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}
}
