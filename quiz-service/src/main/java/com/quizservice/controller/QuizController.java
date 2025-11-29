package com.quizservice.controller;

import com.quizservice.model.QuestionWrapper;
import com.quizservice.model.QuizDto;
import com.quizservice.model.Response;
import com.quizservice.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    
    @PostMapping("create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizDto quizDto) {
        return quizService.createQuiz(
                quizDto.getCategoryName(),
                quizDto.getNumQuestions(),
                quizDto.getTitle()
        );
    }

   
    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(
            @PathVariable String id) {

        return quizService.getQuizQuestions(id);
    }

    
    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz(
            @PathVariable String id,
            @RequestBody List<Response> responses) {

        
        return quizService.calculateResult(id, responses);
    }
}
