package com.example.debezium;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import static io.debezium.data.Envelope.FieldName.*;
import static io.debezium.data.Envelope.Operation;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;

@Component
public class DebeziumListener {
	
	private final Executor executor = Executors.newSingleThreadExecutor();
	private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
	private final YourService service;
	
	public DebeziumListener(Configuration customConfiguration, YourService service) {
		this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
				.using(customConfiguration.asProperties())
				.notifying(this::handleChangeEvent)
				.build();
		
		this.service = service;
	}
	
	@PostConstruct
	private void start() {
	    this.executor.execute(debeziumEngine);
	}

	@PreDestroy
	private void stop() throws IOException {
	    if (this.debeziumEngine != null) {
	        this.debeziumEngine.close();
	    }
	}
	
	private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
	    SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
	    
	    
	    System.out.println("Key =' " + sourceRecord.key() + " 'Value = '"+sourceRecord.value()+" '");
	    Struct sourceRecordChangeValue= (Struct) sourceRecord.value();

	    if (sourceRecordChangeValue != null) {
	        Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

	        if(operation != Operation.READ) {
	            String record = operation == Operation.DELETE ? BEFORE : AFTER;
	            Struct struct = (Struct) sourceRecordChangeValue.get(record);
	            Map<String, Object> payload = struct.schema().fields().stream()
	              .map(Field::name)
	              .filter(fieldName -> struct.get(fieldName) != null)
	              .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
	              .collect(toMap(Pair::getKey, Pair::getValue));
	            
	            this.service.replicateDate(payload,operation);

	        }
	    }
	}

}
