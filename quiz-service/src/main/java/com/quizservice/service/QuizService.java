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
    QuizRepository quizRepo;

    @Autowired
    QuizInterface quizInterface;

    // ---------------- CREATE QUIZ ----------------
    @CircuitBreaker(name = "questionService", fallbackMethod = "createQuizFallback")
    @Retry(name="questionService")
    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<String> questionIds = quizInterface.getQuestionsForQuiz(category, numQ).getBody();

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questionIds);
        quizRepo.save(quiz);

        return ResponseEntity.status(HttpStatus.CREATED).body("Quiz Created Successfully");
    }

    public ResponseEntity<String> createQuizFallback(String category, int numQ, String title, Throwable ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("❗ QUESTION-SERVICE DOWN — Quiz cannot be created");
    }

    // ---------------- FETCH QUIZ QUESTIONS ---------
    @CircuitBreaker(name = "questionService", fallbackMethod = "getQuizFallback")
    @Retry(name="questionService")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(String quizId) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow();
        return quizInterface.getQuestionsFromId(quiz.getQuestionIds());
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizFallback(String quizId, Throwable ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Collections.emptyList());
    }

    // ---------------- SUBMIT QUIZ -------------------
    @CircuitBreaker(name = "questionService", fallbackMethod = "submitQuizFallback")
    @Retry(name="questionService")
    public ResponseEntity<Integer> calculateResult(String id, List<Response> responses) {
        return quizInterface.getScore(responses);
    }

    public ResponseEntity<Integer> submitQuizFallback(String id, List<Response> responses, Throwable ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(-1);
    }
}
