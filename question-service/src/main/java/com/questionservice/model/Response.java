package com.questionservice.model;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Data
public class Response {
	
	@Id
    private String id;
    private String response;
}