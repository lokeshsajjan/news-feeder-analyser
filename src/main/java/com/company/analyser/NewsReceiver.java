package com.company.analyser;

import static com.company.utility.Util.getIntConfig;

import com.company.nio.NioTcpServer;
import com.company.utility.Util;


/**
 * @author lsajjan
 *
 */
public class NewsReceiver {

    public static void main(String[] args) throws Exception {
        try (
                final NioTcpServer server = new NioTcpServer(8080, new NewsAnalyser(getIntConfig("numberOfWorkers", 5)));
        ) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Util.close(server), "shutdown-hook"));
            server.start();
        }
    }
}
