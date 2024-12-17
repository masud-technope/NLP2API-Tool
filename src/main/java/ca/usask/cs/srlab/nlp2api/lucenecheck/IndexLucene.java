
/******
 *
 * @author MasudRahman
 * Creates an index for a corpus where the corpus is a list of code files.
 *
 *
 */

package ca.usask.cs.srlab.nlp2api.lucenecheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ca.usask.cs.srlab.nlp2api.config.StaticData;

public class IndexLucene {

    String repoName;
    String index;
    String docs;
    int totalIndexed = 0;

    @Deprecated
    public IndexLucene(String repoName) {
        this.index = StaticData.EXP_HOME + "/lucene/index/" + repoName;
        this.docs = StaticData.EXP_HOME + "/javadoc/" + repoName;
    }

    public IndexLucene(String indexFolder, String docsFolder) {
        this.index = indexFolder;
        this.docs = docsFolder;
    }

    protected void makeIndexFolder(String repoName) {
        new File(this.index + "/" + repoName).mkdir();
        this.index = this.index + "/" + repoName;
    }

    public void indexCorpusFiles() {
        try {
            Directory dir = FSDirectory.open(new File(index).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(dir, config);
            indexDocs(writer, new File(this.docs));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearIndexFiles() {
        // clearing index files
        File[] files = new File(this.index).listFiles();
        for (File f : files) {
            f.delete();
        }
        System.out.println("Index cleared successfully.");
    }

    protected void indexDocs(IndexWriter writer, File file) {
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocs(writer, new File(file, files[i]));
                    }
                }
            } else {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    return;
                }
                try {
                    Document doc = new Document();

                    Field pathField = new StringField("path", file.getPath(),
                            Field.Store.YES);
                    doc.add(pathField);

                    doc.add(new TextField("contents", new BufferedReader(
                            new InputStreamReader(fis, "UTF-8"))));


                    writer.addDocument(doc);
                    totalIndexed++;

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                } catch (CorruptIndexException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String docs = StaticData.EXP_HOME + "/dataset/answer-norm-code-ext";
        String index = StaticData.EXP_HOME + "/dataset/answer-norm-code-ext-index";
        IndexLucene indexer = new IndexLucene(index, docs);
        indexer.indexCorpusFiles();
        System.out.println("Total indexed:" + indexer.totalIndexed);
        long end = System.currentTimeMillis();
        System.out.println("Time elapsed:" + (end - start) / 1000 + " s");
    }
}
