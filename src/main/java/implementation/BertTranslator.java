package implementation;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertTokenizer;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BertTranslator implements Translator<String, Double[]> {
    private List<String> tokens;
    private Vocabulary vocabulary;
    private BertTokenizer tokenizer;
    private String pathVocabulary;

    public BertTranslator(String pathVocabulary) {
        this.pathVocabulary = pathVocabulary;
    }

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Path path = Paths.get(this.pathVocabulary);
        vocabulary = DefaultVocabulary.builder()
                .optMinFrequency(1)
                .addFromTextFile(path)
                .optUnknownToken("[UNK]")
                .build();
        tokenizer = new BertTokenizer();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String input) {
        tokens = tokenizer.tokenize(input.toLowerCase());
        NDManager manager = ctx.getNDManager();
        long[] indices = tokens.stream().mapToLong(vocabulary::getIndex).toArray();
        NDArray indicesArray = manager.create(indices);
        return new NDList(indicesArray);
    }

    @Override
    public Double[] processOutput(TranslatorContext ctx, NDList list) {
        return null;
    }

    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }
}
