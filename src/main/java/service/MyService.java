package service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.Person;

@Service
public class MyService {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(MyService.class);
	RestClient client;
	public static String username = "root";
	public static String password = "root";
	private static final String elasticSearchService = "elasticsearch";
	private static final int elasticSearchClientPort = 9200;
	private static final String mysqlHostService = "mysql-service";
	private static final String mysqlPort = "1443";
	public static String mysqlUrl = "jdbc:mysql://" + mysqlHostService + ":"
			+ mysqlPort + "/moviedb?useSSL=false";

	public MyService() {
		this.client = RestClient.builder(
				new HttpHost(elasticSearchService, elasticSearchClientPort))
				.build();
	}

	public int createIndex(String indexName, String indexType)
			throws IOException {
		List<Person> list = loadData();
		for (Person person : list) {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(person);
			Response response = client.performRequest("POST",
					indexName + "/" + indexType + "/" + person.getPersId(),
					Collections.emptyMap(),
					new StringEntity(json, ContentType.APPLICATION_JSON));
		}
		return list.size();
	}

	public List<Person> search(String name, String title) throws IOException {
		RestClient client1 = RestClient.builder(
				new HttpHost(elasticSearchService, elasticSearchClientPort))
				.build();
		String query = buildSearchQuery(name, title);
		LOGGER.info(query);
		Response response = client1.performRequest("GET", "/_search",
				Collections.emptyMap(),
				new StringEntity(query, ContentType.APPLICATION_JSON));

		String resp = EntityUtils.toString(response.getEntity(), "UTF-8");

		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		JsonObject responseAsjson = parser.parse(resp).getAsJsonObject();
		JsonObject hits = responseAsjson.getAsJsonObject("hits");
		JsonArray hitsArray = hits.getAsJsonArray("hits");
		List<Person> jsonList = new ArrayList<>();
		for (int i = 0; i < hitsArray.size(); i++) {
			JsonObject h = (JsonObject) hitsArray.get(i);
			JsonObject source = h.getAsJsonObject("_source");
			ObjectMapper objMapper = new ObjectMapper();
			Person pers = objMapper.readValue(source.toString(), Person.class);
			jsonList.add(pers);
		}
		return jsonList;
	}

	private String buildSearchQuery(String name, String title) {
		StringBuilder queryBuilder = new StringBuilder(
				"{" + "\"query\":{" + "\"bool\":{\"must\":[");
		if (name != null && !name.isBlank()) {
			queryBuilder.append("{\"match\":{\"").append(Person.NAME_F)
					.append("\":\"").append(name).append("\"}},");
		}
		if (title != null && !title.isBlank()) {
			queryBuilder.append("{\"match\":{\"").append(Person.PROFESSION_F)
					.append("\":\"").append(title).append("\"}},");
		}

		String interMediateQuery = queryBuilder.toString();
		// remove the last extra comma if it's there.
		if (interMediateQuery.endsWith(",")) {
			interMediateQuery = interMediateQuery.substring(0,
					interMediateQuery.length() - 1);
		}
		String query = interMediateQuery + "]}}}";
		return query;
	}

	private List<Person> loadData() {
		String sql = "Select * from person_details";
		List<Person> list = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(mysqlUrl, username,
				password);
				PreparedStatement selectStmt = conn.prepareStatement(sql);
				ResultSet resultSet = selectStmt.executeQuery()) {
			while (resultSet.next()) {
				int persId = resultSet.getInt("persId");
				String name = resultSet.getString("name");
				String profession = resultSet.getString("profession");
				Person p = new Person(persId, name, profession);
				list.add(p);
			}
		} catch (SQLException e) {
			LOGGER.info("", e);
		}
		return list;
	}

	public boolean writeToDb(List<Person> tmdbRespList) {
		String insertEmployeeSQL = "INSERT INTO person_details(persId, name, profession) "
				+ "VALUES (?,?,?)";

		try (Connection conn = DriverManager.getConnection(mysqlUrl, username,
				password);) {
			conn.setAutoCommit(false);
			PreparedStatement insertStmt = conn
					.prepareStatement(insertEmployeeSQL);
			for (Person per : tmdbRespList) {
				insertStmt.setInt(1, per.getPersId());
				insertStmt.setString(2, per.getName());
				insertStmt.setString(3, per.getProfession());
				insertStmt.addBatch();
			}
			insertStmt.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			LOGGER.info("Error while inserting response", e);
			return false;
		}
		return true;
	}

}
