package org.ansj.elasticsearch.index.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.ansj.elasticsearch.pubsub.redis.AddTermRedisPubSub;
import org.ansj.elasticsearch.pubsub.redis.RedisPoolBuilder;
import org.ansj.elasticsearch.pubsub.redis.RedisUtils;
import org.ansj.lucene.util.FileDeEncrypt;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.nlpcn.commons.lang.util.IOUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class AnsjElasticConfigurator {
	public static ESLogger logger = Loggers.getLogger("ansj-analyzer");
	private static boolean loaded = false;
	public static Set<String> filter;
	public static boolean pstemming = false;
	public static Environment environment;
	public static String ANSJ_CONFIG_PATH = "ansj/library.properties";
	public static String DEFAULT_STOP_FILE_LIB_PATH = "ansj/dic/stopwords_all.txt";

	public static Properties prop = new Properties();

	public static void init(Settings indexSettings, Settings settings) throws IOException {
		if (isLoaded()) {
			return;
		}
		environment = new Environment(indexSettings);
		initConfigPath(settings);
		logger.info("enabled_stop_filter: {}", environment.settings().get("enabled_stop_filter"));
		boolean enabledStopFilter = settings.getAsBoolean("enabled_stop_filter", true);

		if (enabledStopFilter) {
			loadFilter(settings);
		}
		try {
			preheat();
			logger.info("ansj分词器预热完毕，可以使用!");
		} catch (Exception e) {
			logger.error("ansj分词预热失败，请检查路径");
		}
		initRedis(settings);
		setLoaded(true);
	}

	private static void initRedis(final Settings settings) {
		if (null == settings.get("redis.ip")) {
			logger.info("没有找到redis相关配置!");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				RedisPoolBuilder redisPoolBuilder = new RedisPoolBuilder();
				int maxIdle = settings.getAsInt("redis.pool.maxidle", redisPoolBuilder.getMaxIdle());
				int maxWait = settings.getAsInt("redis.pool.maxwait", redisPoolBuilder.getMaxWait());
				boolean testOnBorrow = settings.getAsBoolean("redis.pool.testonborrow",
						redisPoolBuilder.isTestOnBorrow());
				logger.debug("maxIdle:" + maxIdle + ",maxWait:" + maxWait + ",testOnBorrow:" + testOnBorrow);

				String ipAndport = settings.get("redis.ip", redisPoolBuilder.getIpAddress());
				int port = settings.getAsInt("redis.port", redisPoolBuilder.getPort());
				String channel = settings.get("redis.channel", "ansj_term");
				logger.debug("ip:" + ipAndport + ",port:" + port + ",channel:" + channel);

				JedisPool pool = redisPoolBuilder.setMaxIdle(maxIdle).setMaxWait(maxWait).setTestOnBorrow(testOnBorrow)
						.setIpAddress(ipAndport).setPort(port).jedisPool();
				RedisUtils.setJedisPool(pool);
				final Jedis jedis = RedisUtils.getConnection();
				logger.debug("pool:" + (pool == null) + ",jedis:" + (jedis == null));
				logger.info("redis守护线程准备完毕,ip:{},port:{},channel:{}", ipAndport, port, channel);
				jedis.subscribe(new AddTermRedisPubSub(), new String[] { channel });
				RedisUtils.closeConnection(jedis);

			}
		}).start();

	}

	private static void preheat() {
		ToAnalysis.parse("一个词");
	}

	private static void initConfigPath(Settings settings) throws IOException {
		// 是否提取词干
		pstemming = settings.getAsBoolean("pstemming", false);
		// ansj配置文件相对于es plugin目录的相对路径
		String ansj_config_path = environment.settings().get("ansj_config", ANSJ_CONFIG_PATH);
		// es plugin目录路径
		String pluginPath = environment.pluginsFile().getAbsolutePath();
		// 初始化MyStaticValue
		logger.info("pluginPath:" + pluginPath);
		logger.info("ansjConfigPath:" + ansj_config_path);
		InputStream inputStream = new FileInputStream(new File(pluginPath, ansj_config_path));
		prop.load(inputStream);
		MyStaticValue.init(pluginPath, prop);
	}

	private static void loadFilter(Settings settings) {
		Set<String> filters = new HashSet<String>();
		String stopLibraryPath = prop.getProperty("stopwordLibrary");
		if (stopLibraryPath == null) {
			return;
		}
		File stopLibrary = new File(environment.pluginsFile().getAbsolutePath(), stopLibraryPath);
		logger.info("停止词典路径:{}", stopLibrary.getAbsolutePath());
		if (!stopLibrary.isFile()) {
			logger.info("Can't find the file:" + stopLibraryPath + ", no such file or directory exists!");
			emptyFilter();
			setLoaded(true);
			return;
		}

		BufferedReader br;
		int count = 0;
		try {
			FileDeEncrypt deEncrypt = new FileDeEncrypt("STP FILE DE-ENCRYPT");
			br = deEncrypt.decryptFile(stopLibrary.getAbsolutePath());
//			br = IOUtil.getReader(stopLibrary.getAbsolutePath(), "UTF-8");
			String temp = null;
			while ((temp = br.readLine()) != null) {
				filters.add(temp);
				count++;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		filter = filters;
		logger.info("ansj停止词典加载完毕!停用词词数: {}", count);
	}

	private static void emptyFilter() {
		filter = new HashSet<String>();
	}

	public static boolean isLoaded() {
		return loaded;
	}

	public static void setLoaded(boolean loaded) {
		AnsjElasticConfigurator.loaded = loaded;
	}

}
