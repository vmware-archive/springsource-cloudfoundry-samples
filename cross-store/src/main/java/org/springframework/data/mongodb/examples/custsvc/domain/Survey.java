package org.springframework.data.mongodb.examples.custsvc.domain;

public class Survey {

    String question;

    String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Survey [question=" + question + ", answer=" + answer + "]";
    }

}
