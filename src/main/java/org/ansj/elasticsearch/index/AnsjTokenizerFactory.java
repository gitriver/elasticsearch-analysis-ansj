package org.ansj.elasticsearch.index;

import static org.ansj.elasticsearch.index.config.AnsjElasticConfigurator.filter;
import static org.ansj.elasticsearch.index.config.AnsjElasticConfigurator.init;
import static org.ansj.elasticsearch.index.config.AnsjElasticConfigurator.pstemming;

import org.ansj.lucene.util.AnsjTokenizer;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class AnsjTokenizerFactory extends AbstractTokenizerFactory {

	@Inject
	 public AnsjTokenizerFactory(Index index, IndexSettingsService indexSettings,@Assisted String name, @Assisted Settings settings) {
		super(index, indexSettings.getSettings(), name, settings);
		init(indexSettings.getSettings(), settings);
	}

//	@Override
//	public Tokenizer create(Reader reader) {
//		return new AnsjTokenizer(new IndexAnalysis(new BufferedReader(reader)), reader, filter, pstemming);
//	}

    @Override
    public Tokenizer create() {
        // TODO Auto-generated method stub
        return new AnsjTokenizer(new IndexAnalysis(), filter, pstemming);
    }

}
