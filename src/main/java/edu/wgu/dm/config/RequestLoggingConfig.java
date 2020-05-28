package edu.wgu.dm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Value("${wgu.request.logging.includeHeaders:false}")
    private boolean includeHeaders;

    @Value("${wgu.request.logging.includeQueryString:true}")
    private boolean includeQueryString;

    @Value("${wgu.request.logging.includePayload:false}")
    private boolean includePayload;

    @Value("${wgu.request.logging.includeClientInfo:false}")
    private boolean includeClientInfo;

    @Value("${wgu.request.logging.maxPayloadLenght:2000}")
    private int maxPayloadLenght;

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(includeQueryString);
        filter.setIncludePayload(includePayload);
        filter.setIncludeHeaders(includeHeaders);
        filter.setIncludeClientInfo(includeClientInfo);
        filter.setMaxPayloadLength(maxPayloadLenght);
        return filter;
    }
}
