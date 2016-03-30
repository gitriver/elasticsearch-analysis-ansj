package org.ansj.elasticsearch.index.tokenizer;

import static org.ansj.elasticsearch.index.config.AnsjElasticConfigurator.filter;
import static org.ansj.elasticsearch.index.config.AnsjElasticConfigurator.init;
import static org.ansj.elasticsearch.index.config.AnsjElasticConfigurator.pstemming;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.ansj.lucene.util.AnsjTokenizer;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettings;

/**
 * Created by zhangqinghua on 14-9-3.
 */
public class AnsjQueryTokenizerFactory extends AbstractTokenizerFactory {

	// @Inject
	// public AnsjQueryTokenizerFactory(Index index, IndexSettingsService
	// indexSettings, @Assisted String name,
	// @Assisted Settings settings) throws IOException {
	// super(index, indexSettings.getSettings(), name, settings);
	// init(indexSettings.getSettings(), settings);
	// }
	//
	// @Override
	// public Tokenizer create() {
	// // TODO Auto-generated method stub
	// return new AnsjTokenizer(new ToAnalysis(), filter, pstemming);
	// }
	@Inject
	public AnsjQueryTokenizerFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name,
			@Assisted Settings settings) throws IOException {
		super(index, indexSettings, name, settings);
		init(indexSettings, settings);
	}

	@Override
	public Tokenizer create(Reader reader) {
		return new AnsjTokenizer(new ToAnalysis(new BufferedReader(reader)), reader, filter, pstemming);
	}

	// @Override
	// public Tokenizer create(Reader reader) {
	// return new AnsjTokenizer(new ToAnalysis(new BufferedReader(reader)),
	// reader, filter, pstemming);
	// }

}