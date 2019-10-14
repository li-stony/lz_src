package me.lizl.hanlp;

import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.crf.CRFSegmenter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class TestMain {
    public static void main(String[] args) throws IOException {

        String[] testFiles = {
                "/Volumes/ExtDatas/datas/personas/corups/apps/2d/com.happy.zhuawawa.txt",
                "/Volumes/ExtDatas/datas/personas/corups/apps/2d/com.hengye.share.txt",
                "/Volumes/ExtDatas/datas/personas/corups/apps/2d/com.videoclips.txt"

        };

        // CRF 分词
        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
        CRFSegmenter segmenter = new CRFSegmenter();

        for(String file : testFiles) {
            String text = FileUtils.readFileToString(new File(file), Charset.forName("UTF-8"));
            Sentence sentence = analyzer.analyze(text);
            System.out.println(sentence);

            System.out.println("-- -- --");

            List<String> words = segmenter.segment(text);
            System.out.println(words);

            System.out.println("** ** ** **");
            System.out.println();
            System.out.println();
        }


    }
}
