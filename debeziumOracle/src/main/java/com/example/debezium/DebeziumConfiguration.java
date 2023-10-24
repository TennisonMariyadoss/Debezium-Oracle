package com.example.debezium;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebeziumConfiguration {
	
	@Bean
	public io.debezium.config.Configuration customConnector() throws IOException{
		File offsetStorageTempFile = File.createTempFile("offsets_",".dat");
		File dnHistoryTempFile = File.createTempFile("dbhistory",".dat");
		File schemaHistoryTempFile = File.createTempFile("schemahistory",".dat");
		return io.debezium.config.Configuration.create().with("name","any_name")
				.with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
		        .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
		        .with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
		        .with("offset.flush.interval.ms", "60000")
		        .with("topic.prefix","anyTopicName")
		        .with("database.url", "your db jdbc url")
		        .with("database.hostname", "your db host")
		        .with("database.port", "your db port")
		        .with("database.user", "your db user name")
		        .with("database.password", "your db password")
		        .with("database.dbname", "db name")
		        .with("schema.include.list", "your db schema name")
				.with("table.include.list","table name ") //eg(schemaname.Tablename)
				.with("include.schema.changes", "false")
				.with("database.allowPublicKeyRetrieval","true")
				.with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
		        .with("database.history.file.filename", dnHistoryTempFile.getAbsolutePath())
		        .with("schema.hstory.internal", "io.debezium.storage.file.history.FileSchemaHistory")
		        .with("schema.hstory.internal.file.filename", "/tmp/dbhistory.dat")
				.with("debezium.source.database.history.store.only.capture.tables.ddl","true")
				.with("schema.history.internal.store.only.captured.tables.ddl","true")
				.build();	
	}

}
