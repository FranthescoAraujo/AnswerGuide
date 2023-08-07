package implementation;

import ai.djl.ndarray.NDList;
import ai.djl.translate.TranslatorContext;

public class BertMeanTranslator extends BertTranslator {

    public BertMeanTranslator(String pathVocabulary) {
        super(pathVocabulary);
    }

    @Override
    public Double[] processOutput(TranslatorContext ctx, NDList list) {
        int sizeX = (int) list.get(0).getShape().get(1);
        int sizeY = (int) list.get(0).getShape().get(0);
        Double[] result =  new Double[ sizeX ];
        for (int j = 0; j < sizeX; j++) {
            result[j] = 0.0;
            for (int i = 0; i < sizeY; i++) {
                result[j] += Double.parseDouble(String.valueOf(list.get(0).get(i).getFloat(j)));
            }
            result[j] /= sizeY;
        }
        return result;
    }
}
