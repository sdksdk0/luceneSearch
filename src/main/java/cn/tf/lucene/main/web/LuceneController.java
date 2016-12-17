package cn.tf.lucene.main.web;

import cn.tf.lucene.main.service.CreateIndex;
import cn.tf.lucene.main.util.PageUtil;
import cn.tf.lucene.main.vo.HtmlBean;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Controller
public class LuceneController {
    @Autowired
    private CreateIndex index;
    @RequestMapping("/index.do")
    public String createIndex(){
        File file = new File(CreateIndex.indexDir);
        if(file.exists()){
            file.delete();
            file.mkdirs();
        }
        index.createIndex();
        return "index.jsp";
    }
    @RequestMapping("/search.do")
    public String search(String keywords,int num,Model model)throws Exception{
        System.out.println(keywords);
            Directory dir = FSDirectory.open(new File(CreateIndex.indexDir));
            IKAnalyzer analyzer = new IKAnalyzer();
            MultiFieldQueryParser mq = new MultiFieldQueryParser(Version.LUCENE_4_9,new String[]{"title","context"},analyzer);
            Query query = mq.parse(keywords);
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs td = searcher.search(query, 10*num);
            ScoreDoc[] scoreDocs = td.scoreDocs;
            System.out.println(td.totalHits);
            int count=td.totalHits;
            PageUtil<HtmlBean> page = new PageUtil<HtmlBean>(num+"",10+"",count);
            List<HtmlBean> ls = new ArrayList<HtmlBean>();
            for (int i = (num-1)*10; i <Math.min(num*10,count) ; i++) {
                ScoreDoc sd = scoreDocs[i];
                int docId = sd.doc;
                Document document = reader.document(docId);
                HtmlBean hb = new HtmlBean();
                SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<font color=\"red\">","</font>" );
                QueryScorer qs = new QueryScorer(query);
                Highlighter highlighter = new Highlighter(formatter,qs);
                String title = highlighter.getBestFragment(analyzer,"title",document.get("title"));
                String context = highlighter.getBestFragments(analyzer.tokenStream("context", document.get("context")), document.get("context"), 3, "...");
                hb.setContext(context);
                hb.setTitle(title);
                hb.setUrl(document.get("url"));
                ls.add(hb);
            }
            page.setList(ls);
            model.addAttribute("page",page);
           model.addAttribute("keywords",keywords);
        return "search.jsp";
    }
}
