package main;

import implementation.AnswerGuide;
import vo.QuestionAnswerDTO;

import java.sql.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private final static String JDBC_URL = "jdbc:postgresql://localhost:5432/answer_guide";
    private final static String USER_NAME = "postgres";
    private final static String PASSWORD = "admin";

    private final static String MODEL_PATH = "build/pytorch_models/bert-large-portuguese-cased/bert-large-portuguese-cased.pt";
    private final static String VOCABULARY_PATH = "build/pytorch_models/bert-large-portuguese-cased/vocab.txt";

    public static void main(String[] args) throws Exception {

        List<QuestionAnswerDTO> questionAnswerDTOList = getQuestionAnswerDTOListFromDb();

        AnswerGuide answerGuide = AnswerGuide.getInstance(MODEL_PATH, VOCABULARY_PATH);

        questionAnswerDTOList = answerGuide.createEmbbedings(questionAnswerDTOList);

        for (QuestionAnswerDTO questionAnswerDTO : questionAnswerDTOList) {
            updateQuestionAnswerDTO(questionAnswerDTO);
        }

        questionAnswerDTOList = getQuestionAnswerDTOListFromDbWithEmbeddings();

        QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO("quem lutou pela igualdade racial dos Estados Unidos?");

        questionAnswerDTO = answerGuide.createEmbbedings(questionAnswerDTO);

        List<QuestionAnswerDTO> questionAnswerDTOListTop5 = answerGuide.calculateEuclideanDistance(questionAnswerDTO, questionAnswerDTOList);
        questionAnswerDTOListTop5 = questionAnswerDTOListTop5.stream().limit(5).collect(Collectors.toList());

        System.out.println(questionAnswerDTOListTop5.get(0).getAnswer());
        System.out.println(questionAnswerDTOListTop5.get(1).getAnswer());
        System.out.println(questionAnswerDTOListTop5.get(2).getAnswer());
        System.out.println(questionAnswerDTOListTop5.get(3).getAnswer());
        System.out.println(questionAnswerDTOListTop5.get(4).getAnswer());

    }

    private static List<QuestionAnswerDTO> getQuestionAnswerDTOListFromDb() {

        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        String sql = "SELECT id, pergunta FROM perguntas_respostas";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String pergunta = resultSet.getString("pergunta");
                QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO(id, pergunta);
                questionAnswerDTOList.add(questionAnswerDTO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return questionAnswerDTOList;
    }

    private static List<QuestionAnswerDTO> getQuestionAnswerDTOListFromDbWithEmbeddings() {

        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        String sql = "SELECT * FROM perguntas_respostas";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String pergunta = resultSet.getString("pergunta");
                String resposta = resultSet.getString("resposta");
                String representacao = resultSet.getString("representacao").replace("{", "").replace("}", "");
                List<Double> representacaoList = Arrays.asList(representacao.split(",")).stream().map(element -> Double.parseDouble(element)).collect(Collectors.toList());
                QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO(id, pergunta, resposta, representacaoList);
                questionAnswerDTOList.add(questionAnswerDTO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return questionAnswerDTOList;
    }

    private static void updateQuestionAnswerDTO(QuestionAnswerDTO questionAnswerDTO) {

        String sql = "UPDATE perguntas_respostas SET representacao = ARRAY" +  questionAnswerDTO.getRepresentation().toString() + " WHERE id = '" + questionAnswerDTO.getId() + "'";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL, USER_NAME, PASSWORD);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}