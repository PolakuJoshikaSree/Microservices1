package com.questionservice.repository;

import com.questionservice.model.Question;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepo extends MongoRepository<Question, String> {

    List<Question> findByCategory(String category);

    @Aggregation(pipeline = {
    	    "{ $match: { category: ?0 } }",
    	    "{ $sample: { size: ?1 } }",
    	    "{ $project: { _id: 1 } }"
    	})
    	List<Question> findRandomQuestionsByCategory(String category, int numQ);


}