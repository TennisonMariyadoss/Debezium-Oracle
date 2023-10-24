package com.example.debezium;

import java.util.Map;

import org.springframework.stereotype.Service;

import io.debezium.data.Envelope.Operation;

@Service
public class YourService {
	
	public void replicateDate(Map<String,Object> insertedData, Operation operation ) {
		
		// write code insert the capture data to other db or log
	}

}
