package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.Person;
import service.MyService;

@RestController
@RequestMapping("/pers")
public class MyEndPointController {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(MyEndPointController.class);
	@Autowired
	MyService service;

	@PostMapping("/createIndex")
	public int createIndex(@RequestParam String indexName,
			@RequestParam String indexType) {
		LOGGER.info("createIndex for indexName" + indexName + " indexType"
				+ indexType);
		int totalCount = 0;
		try {
			totalCount = service.createIndex(indexName, indexType);
		} catch (IOException e) {
			LOGGER.error("Exception", e);
		}
		return totalCount;
	}

	@GetMapping("/search")
	String search(@RequestParam String name, @RequestParam String profession)
			throws IOException {
		LOGGER.info("search requested for name {}, profession {}", name,
				profession);
		List<Person> personList = service.search(name, profession);
		if (personList.isEmpty()) {
			return "No result";
		}
		return personList.stream().map(x -> x.toString())
				.collect(Collectors.joining(";"));
	}

	@PostMapping("/addToDb")
	public boolean addToDb() {
		LOGGER.info("addToDb requested");
		List<Person> persons = new ArrayList<>();
		Person p1 = new Person(1, "Tom", "Doctor");
		Person p2 = new Person(2, "Harry", "Engineer");
		Person p3 = new Person(3, "Mary", "Nurse");
		Person p4 = new Person(4, "Ana", "Politician");
		Person p5 = new Person(5, "George", "Farmer");
		persons.add(p1);
		persons.add(p2);
		persons.add(p3);
		persons.add(p4);
		persons.add(p5);
		return service.writeToDb(persons);
	}
}
