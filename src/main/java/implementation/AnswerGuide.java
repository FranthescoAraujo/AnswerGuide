package implementation;

import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Translator;
import vo.QuestionAnswerDTO;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerGuide {
    private static AnswerGuide answerGuide;
    private ZooModel<String, Double[]> model;
    private Translator<String, Double[]> translator;

    private AnswerGuide() {}

    private AnswerGuide(String modelPath, String vocabularyPath) throws Exception {
        translator = new BertMeanTranslator(vocabularyPath);
        Criteria<String, Double[]> criteria = Criteria.builder()
                                                      .setTypes(String.class, Double[].class)
                                                      .optModelPath(Paths.get(modelPath))
                                                      .optTranslator(translator)
                                                      .optProgress(new ProgressBar())
                                                      .build();
        model = criteria.loadModel();
    }

    public static AnswerGuide getInstance(String modelPath, String vocabularyPath) throws Exception {
        if (answerGuide == null) {
            answerGuide = new AnswerGuide(modelPath, vocabularyPath);
        }
        return answerGuide;
    }

    public QuestionAnswerDTO createEmbbedings(QuestionAnswerDTO questionAnswerDTO) throws Exception {
        Double[] predictResult;

        try (Predictor<String, Double[]> predictor = model.newPredictor(translator)) {
            predictResult = predictor.predict(questionAnswerDTO.getQuestion());
        }

        List<Double> list = new ArrayList<>();
        for (Double result : predictResult) {
            list.add(result);
        }

        questionAnswerDTO.setRepresentation(list);
        return questionAnswerDTO;
    }

    public List<QuestionAnswerDTO> createEmbbedings(List<QuestionAnswerDTO> questionAnswerDTOList) throws Exception {
        Double[] predictResult;

        for (QuestionAnswerDTO vo : questionAnswerDTOList) {
            try (Predictor<String, Double[]> predictor = model.newPredictor(translator)) {
                predictResult = predictor.predict(vo.getQuestion());
            }
            List<Double> list = new ArrayList<>();
            for (Double result : predictResult) {
                list.add(result);
            }
            vo.setRepresentation(list);
        }

        return questionAnswerDTOList;
    }

    public List<QuestionAnswerDTO> calculateEuclideanDistance(QuestionAnswerDTO questionAnswerDTO, List<QuestionAnswerDTO> questionAnswerDTOList) {
        questionAnswerDTOList.forEach(dto -> dto.setEuclideanDistance(calculateEuclideanDistance(questionAnswerDTO.getRepresentation(), dto.getRepresentation())));
        return questionAnswerDTOList.stream().sorted(Comparator.comparing(QuestionAnswerDTO::getEuclideanDistance)).collect(Collectors.toList());
    }

    private double calculateEuclideanDistance(List<Double> vector1, List<Double> vector2) {
        double sumOfSquaredDifferences = 0.0;
        for (int i = 0; i < vector1.size(); i++) {
            double difference = vector1.get(i) - vector2.get(i);
            sumOfSquaredDifferences += difference * difference;
        }

        return Math.sqrt(sumOfSquaredDifferences);
    }

}
