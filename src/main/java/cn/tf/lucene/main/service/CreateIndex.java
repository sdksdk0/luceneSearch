package cn.tf.lucene.main.service;

import cn.tf.lucene.main.vo.HtmlBean;
import cn.tf.lucene.main.vo.HtmlBeanUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


@Service
public class CreateIndex {
    public static String dataDir ="D:/luceneData/wz";
    public static final String indexDir="D:/DBIndex/index";
    @Test
    public void createIndex(){
        Directory dir = null;
        try {
            dir = FSDirectory.open(new File(indexDir));
            Analyzer an = new IKAnalyzer();
            IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_9,an);
            conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(dir,conf);
            Collection<File> files = FileUtils.listFiles(new File(dataDir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            RAMDirectory rdir = new RAMDirectory();
            IndexWriterConfig conf1 = new IndexWriterConfig(Version.LUCENE_4_9,an);
            conf1.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter ramWriter = new IndexWriter(rdir,conf1);
            int count=0;
            for (File f :files){

                HtmlBean bean = HtmlBeanUtil.createBean(f);
                Document doc =new Document();
                if(bean==null){
                    continue;
                }
                count++;
                doc.add(new StringField("title",bean.getTitle(), Field.Store.YES));
                doc.add(new StringField("url",bean.getUrl(), Field.Store.YES));
                doc.add(new TextField("context", bean.getContext(), Field.Store.YES));
                ramWriter.addDocument(doc);
                if(count==50){
                    ramWriter.close();
                    writer.addIndexes(rdir);
                    rdir=new RAMDirectory();
                    IndexWriterConfig conf2 = new IndexWriterConfig(Version.LUCENE_4_9,an);
                    conf2.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                    ramWriter = new IndexWriter(rdir,conf2);
                    count=0;
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void search(){
        try {
            Directory dir = FSDirectory.open(new File(CreateIndex.indexDir));
            MultiFieldQueryParser mq = new MultiFieldQueryParser(Version.LUCENE_4_9,new String[]{"title","context"},new IKAnalyzer());
            Query query = mq.parse("java");
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs td = searcher.search(query, 10);
            System.out.println(td.totalHits);
            for (ScoreDoc sd:td.scoreDocs){
                int docId = sd.doc;
                reader.document(docId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
