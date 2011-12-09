package org.springframework.data.mongodb.examples.custsvc.domain;

import java.util.ArrayList;
import java.util.List;

public class SurveyInfo {

    private List<Survey> questionsAndAnswers = new ArrayList<Survey>();

    public List<Survey> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(List<Survey> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }

    public void addQuestionsAndAnswer(Survey survey) {
        this.questionsAndAnswers.add(survey);
    }

    @Override
    public String toString() {
        return "SurveyInfo [questionsAndAnswers=" + questionsAndAnswers + "]";
    }

}
