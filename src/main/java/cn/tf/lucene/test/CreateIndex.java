package cn.tf.lucene.test;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by asus on 2016/12/16.
 */
public class CreateIndex {

    public static final String indexDir="D:/DBIndex/index";
    public static final String dataDir="D:/luceneData";


    @Test
    public void createIndex(){

        Directory  dir= null;
        try {
            dir = FSDirectory.open(new File(indexDir));

            Analyzer  analyzer=new StandardAnalyzer(Version.LUCENE_4_9);
            IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_4_9,analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer=new IndexWriter(dir,config);
            File file=new File(dataDir);
            File[] files= file.listFiles();
            for(File f:files){

                Document doc = new Document();
                doc.add(new StringField("title", f.getName(), Field.Store.YES));
                doc.add(new TextField("content", FileUtils.readFileToString(f), Field.Store.YES));
                doc.add(new LongField("lastModify", f.lastModified(), Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
