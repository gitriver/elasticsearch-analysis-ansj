package org.ansj.elasticsearch.plugin;

import org.ansj.elasticsearch.index.AnsjAnalysisBinderProcessor;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

public class AnalysisAnsjPlugin extends Plugin {
	private final Settings settings;

	public AnalysisAnsjPlugin(Settings settings) {
		this.settings = settings;
	}

	@Override
	public String name() {
		return "analysis-ansj";
	}

	@Override
	public String description() {
		return "ansj analysis";
	}

	// @Override
	// public Collection<Module> nodeModules() {
	// return Collections.<Module>singletonList(new IKIndicesAnalysisModule());
	// }

	public static class ConfiguredExampleModule extends AbstractModule {
		@Override
		protected void configure() {
		}
	}

	public void onModule(AnalysisModule module) {
		module.addProcessor(new AnsjAnalysisBinderProcessor());
	}
	// @Override public void processModule(Module module) {
	// if (module instanceof AnalysisModule) {
	// AnalysisModule analysisModule = (AnalysisModule) module;
	// analysisModule.addProcessor(new AnsjAnalysisBinderProcessor());
	// }
	// }

}