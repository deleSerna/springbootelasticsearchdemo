open module movie {
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.boot;
	requires spring.web;
	requires spring.core;
	requires spring.beans;
	requires org.slf4j;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.core;
	requires java.sql;
	requires java.net.http;
	requires lombok;
	requires elasticsearch.rest.client;
	requires org.apache.httpcomponents.httpcore;
	requires gson;
}
