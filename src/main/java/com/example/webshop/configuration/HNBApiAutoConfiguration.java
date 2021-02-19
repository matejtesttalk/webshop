package com.example.webshop.configuration;

import com.example.webshop.configuration.properties.HNBApiProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;


@Configuration
@EnableConfigurationProperties(HNBApiProperties.class)
public class HNBApiAutoConfiguration {

    HNBApiProperties hnbApiProperties;

    @Autowired
    HNBApiAutoConfiguration(HNBApiProperties hnbApiProperties) {
        this.hnbApiProperties = hnbApiProperties;
    }

    @Bean("hnbWebClient")
    public WebClient hnbWebClient() {
        TcpClient timeoutClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, hnbApiProperties.getTimeOutSeconds()*1000)
                .doOnConnected(
                        c -> c.addHandlerLast(new ReadTimeoutHandler(hnbApiProperties.getTimeOutSeconds()*1000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(hnbApiProperties.getTimeOutSeconds()*1000, TimeUnit.MILLISECONDS)));

        return WebClient.builder().baseUrl(hnbApiProperties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(timeoutClient)))
                .build();
    }

}
