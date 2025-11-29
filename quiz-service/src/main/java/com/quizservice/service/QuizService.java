package com.quizservice.service;

import com.quizservice.feign.QuizInterface;
import com.quizservice.model.QuestionWrapper;
import com.quizservice.model.Quiz;
import com.quizservice.model.Response;
import com.quizservice.repository.QuizRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepo;

    @Autowired
    private QuizInterface quizInterface;

    @CircuitBreaker(name = "questionService", fallbackMethod = "createQuizFallback")
    @Retry(name = "questionService")
    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        try {
            List<String> questionIds =
                    quizInterface.getQuestionsForQuiz(category, numQ).getBody();

            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setQuestionIds(questionIds);
            quizRepo.save(quiz);

            return new ResponseEntity<>("Quiz Created Successfully", HttpStatus.CREATED);
        } catch (Exception ex) {
            return createQuizFallback(category, numQ, title, ex);
        }
    }

    public ResponseEntity<String> createQuizFallback(String category, int numQ, String title, Exception ex) {
        return new ResponseEntity<>("❗ QUESTION-SERVICE unavailable — Quiz could not be created",
                HttpStatus.SERVICE_UNAVAILABLE);
    }


    @CircuitBreaker(name = "questionService", fallbackMethod = "getQuizQuestionsFallback")
    @Retry(name = "questionService")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(String quizId) {
        try {
            Quiz quiz = quizRepo.findById(quizId).orElseThrow();
            return quizInterface.getQuestionsFromId(quiz.getQuestionIds());
        } catch (Exception ex) {
            return getQuizQuestionsFallback(quizId, ex);
        }
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestionsFallback(String quizId, Exception ex) {
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
    }


    @CircuitBreaker(name = "questionService", fallbackMethod = "calculateScoreFallback")
    @Retry(name = "questionService")
    public ResponseEntity<Integer> calculateResult(String id, List<Response> responses) {
        try {
            return quizInterface.getScore(responses);
        } catch (Exception ex) {
            return calculateScoreFallback(id, responses, ex);
        }
    }

    public ResponseEntity<Integer> calculateScoreFallback(String id, List<Response> responses, Exception ex) {
        return new ResponseEntity<>(-1, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
