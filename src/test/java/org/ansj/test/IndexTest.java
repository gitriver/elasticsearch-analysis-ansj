package org.ansj.test;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashSet;

import org.ansj.library.UserDefineLibrary;
import org.ansj.lucene.util.PorterStemmer;
import org.ansj.lucene4.AnsjAnalysis;
import org.ansj.lucene4.AnsjIndexAnalysis;
import org.ansj.util.MyStaticValue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class IndexTest {
	@Test
	public void test() throws IOException {
		MyStaticValue.userLibrary = "G:/ansj/dic/user/default.dic";
		MyStaticValue.stopwordLibrary = "G:/stopwords_all.txt";
		Token nt = new Token();
		Analyzer ca = new AnsjAnalysis(false);
		Reader sentence = new StringReader(
				"于是乎,只有他冒着生命危险，挨家挨户地把邻居们叫醒。");
		TokenStream ts = ca.tokenStream("sentence", sentence);

		System.out.println("start: " + (new Date()));
		long before = System.currentTimeMillis();
		while (ts.incrementToken()) {
			System.out.println(ts.getAttribute(CharTermAttribute.class));
		}
		ts.close();
		long now = System.currentTimeMillis();
		System.out.println("time: " + (now - before) / 1000.0 + " s");
	}

	public void indexTest() throws CorruptIndexException, LockObtainFailedException, IOException, ParseException {
		MyStaticValue.userLibrary = "G:/ansj/dic/user/default.dic";
		HashSet<String> hs = new HashSet<String>();
		hs.add("的");
		Analyzer analyzer = new AnsjIndexAnalysis(hs, false);
		Directory directory = null;
		IndexWriter iwriter = null;
		String text = "云浮安庆路298号";

//		UserDefineLibrary.insertWord("安庆路", "n", 1000);

		IndexWriterConfig ic = new IndexWriterConfig(Version.LUCENE_44, analyzer);
		// 建立内存索引对象
		directory = new RAMDirectory();
		iwriter = new IndexWriter(directory, ic);
		addContent(iwriter, text);
		iwriter.commit();
		iwriter.close();

		System.out.println("索引建立完毕");

		Analyzer queryAnalyzer = new AnsjAnalysis(hs, true);
		;

		System.out.println("index ok to search!");
		search(queryAnalyzer, directory, "\"安庆路\"");

	}

	private void search(Analyzer queryAnalyzer, Directory directory, String queryStr)
			throws CorruptIndexException, IOException, ParseException {
		IndexSearcher isearcher;

		DirectoryReader directoryReader = IndexReader.open(directory);
		// 查询索引
		isearcher = new IndexSearcher(directoryReader);
		QueryParser tq = new QueryParser(Version.LUCENE_44, "text", queryAnalyzer);
		Query query = tq.parse(queryStr);
		System.out.println(query);
		TopDocs hits = isearcher.search(query, 5);
		System.out.println(queryStr + ":共找到" + hits.totalHits + "条记录!");
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			int docId = hits.scoreDocs[i].doc;
			Document document = isearcher.doc(docId);
			System.out.println(toHighlighter(queryAnalyzer, query, document));
		}
	}

	/**
	 * 高亮设置
	 * 
	 * @param query
	 * @param doc
	 * @param field
	 * @return
	 */
	private String toHighlighter(Analyzer analyzer, Query query, Document doc) {
		String field = "text";
		try {
			SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
			Highlighter highlighter = new Highlighter(simpleHtmlFormatter, new QueryScorer(query));
			TokenStream tokenStream1 = analyzer.tokenStream("text", new StringReader(doc.get(field)));
			String highlighterStr = highlighter.getBestFragment(tokenStream1, doc.get(field));
			return highlighterStr == null ? doc.get(field) : highlighterStr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void addContent(IndexWriter iwriter, String text) throws CorruptIndexException, IOException {
		Document doc = new Document();
		doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED));
		iwriter.addDocument(doc);
	}

	@Test
	public void poreterTest() {
		PorterStemmer ps = new PorterStemmer();
		System.out.println(ps.stem("apache"));
	}
}
