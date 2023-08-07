package vo;

import java.util.List;

public class QuestionAnswerDTO {

    private String id;
    private String question;
    private String answer;
    private List<Double> representation;
    private Double euclideanDistance;

    public QuestionAnswerDTO(String question) {
        this.question = question;
    }

    public QuestionAnswerDTO(String id, String question) {
        this.id = id;
        this.question = question;
    }

    public QuestionAnswerDTO(String id, String question, String answer, List<Double> representation) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.representation = representation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<Double> getRepresentation() {
        return representation;
    }

    public void setRepresentation(List<Double> representation) {
        this.representation = representation;
    }

    public Double getEuclideanDistance() {
        return euclideanDistance;
    }

    public void setEuclideanDistance(Double euclideanDistance) {
        this.euclideanDistance = euclideanDistance;
    }
}
