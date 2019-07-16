package com.company.feeder;

import static com.company.utility.Util.getIntConfig;

import com.company.nio.NioTcpClient;
import com.company.utility.Util;

/**
 * @author lsajjan
 *
 */
public class NewsFeeder {

	public static void main(String[] args) throws Exception {
		try (final NioTcpClient client = new NioTcpClient("localhost", 8080,
				new NewsFeed(getIntConfig("newsProducingRateInMillis", 1000)))) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> Util.close(client), "shutdown-hook"));
			client.start();
		}
	}
}
