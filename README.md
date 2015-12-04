elasticsearch-analysis-ansj
----------------------------------- 
### 原始版本
在原始版本 https://github.com/4onni/elasticsearch-analysis-ansj 的基础上，参考 https://github.com/V-ea/elasticsearch-analysis-ansj-2.0 进行修改

### 修改项
* 将 org.elasticsearch.index.settings.IndexSettings 修改为 org.elasticsearch.index.settings.IndexDynamicSettings;因为elastic search 2.1中已经没有IndexSettings类
* 将jedis的版本修改为2.7.2,以兼容redis 3以上的版本，同时针对代码进行调整
* 将ansj 从2.08修改为3.0
* 将nlp-lang 从0.2修改为1.0.2
* 针对elastic search,lucene的版本进行调整，分别调整为2.1.0,5.3.1
* 针对整个pom.xml进行重新编写
