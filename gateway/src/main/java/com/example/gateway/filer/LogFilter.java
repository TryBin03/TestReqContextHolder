package com.example.gateway.filer;/*
package com.trybin.gateway.filer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.trybin.common.baidu.Handle.BaiduHandle;
import com.trybin.common.core.constant.CacheConstants;
import com.trybin.common.core.utils.IpUtils;
import com.trybin.common.core.utils.StringUtils;
import com.trybin.gateway.Properties.IgnoreWhiteProperties;
import com.trybin.system.api.RemoteLogService;
import com.trybin.system.api.domain.SysOperLog;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

*/
/**
 * 操作日志过滤器
 * @author: TryBin
 * @date: 2022/5/21 1:18:13
 *//*

@Slf4j
@Component
public class LogFilter implements GlobalFilter, Ordered {

    // 排除过滤的 uri 地址，nacos自行添加
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Autowired
    private RemoteLogService remoteLogService;

    @Autowired
    private BaiduHandle baiduHandle;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public int getOrder() {
        return -119;
    }


    */
/**
     * @param exchange
     * @param chain
     * @return
     *//*

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        // 跳过不需要验证的路径
        boolean matches = StringUtils.matches(url, ignoreWhite.getWhites());

        Route route = getGatewayRoute(exchange);

        String ipAddress = IpUtils.getIpAddr(request);

        SysOperLog sysOperLog = new SysOperLog();
        sysOperLog.setSchema(request.getURI().getScheme());
        sysOperLog.setRequestMethod(request.getMethodValue());
        sysOperLog.setRequestPath(url);
        sysOperLog.setTargetServer(route.getId());
        sysOperLog.setRequestTime(new Date());
        sysOperLog.setIp(ipAddress);
        sysOperLog.setOperUserName(matches ? "-" : request.getHeaders().get(CacheConstants.DETAILS_USERNAME).get(0));
        sysOperLog.setOperAddresses(matches ? "-" : baiduHandle.ipLocating(ipAddress));
        sysOperLog.setStatus("0");
        MediaType mediaType = request.getHeaders().getContentType();

        if(MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType) || MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)){
            return writeBodyLog(exchange, chain, sysOperLog);
        }else{
            return writeBasicLog(exchange, chain, sysOperLog);
        }
    }

    private Mono<Void> writeBasicLog(ServerWebExchange exchange, GatewayFilterChain chain, SysOperLog sysOperLog) {
        StringBuilder builder = new StringBuilder();
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ","));
        }
        sysOperLog.setRequestBody(builder.toString());

        //获取响应体
        ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, sysOperLog);

        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .then(Mono.fromRunnable(() -> {
                    CompletableFuture.runAsync(()-> {
                        remoteLogService.saveLog(sysOperLog);
                    });
                }));
    }

    private Mono<Void> writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, SysOperLog sysOperLog) {
        ServerRequest serverRequest = ServerRequest.create(exchange,messageReaders);

        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                .flatMap(body ->{
                    sysOperLog.setRequestBody(body);
                    return Mono.just(body);
                });

        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());

        headers.remove(HttpHeaders.CONTENT_LENGTH);

        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

        return bodyInserter.insert(outputMessage,new BodyInserterContext())
                .then(Mono.defer(() -> {
                    // 重新封装请求
                    ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);

                    // 记录响应日志
                    ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, sysOperLog);

                    // 记录普通的
                    return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                            .then(Mono.fromRunnable(() -> {
                                CompletableFuture.runAsync(() -> {
                                    remoteLogService.saveLog(sysOperLog);
                                });
                            }));
                }));

    }

    */
/**
     * 请求装饰器，重新计算 headers
     *//*

    private ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers,
                                                       CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    */
/**
     * 记录响应日志
     * 通过 DataBufferFactory 解决响应体分段传输问题。
     *//*

    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange, SysOperLog sysOperLog) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();

        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Date responseTime = new Date();
                    sysOperLog.setResponseTime(responseTime);
                    // 计算执行时间
                    long executeTime = (responseTime.getTime() - sysOperLog.getRequestTime().getTime());

                    sysOperLog.setExecuteTime(executeTime);

                    // 获取响应类型，如果是 json 就打印
                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);


                    if (ObjectUtil.equal(this.getStatusCode(), HttpStatus.OK)
                            && StringUtil.isNotBlank(originalResponseContentType)
                            && originalResponseContentType.contains("application/json")) {

                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);

                            // 释放掉内存
                            DataBufferUtils.release(join);
                            String responseResult = new String(content, StandardCharsets.UTF_8);

                            sysOperLog.setResponseData(responseResult);

                            return bufferFactory.wrap(content);
                        }));
                    }
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
    }

    */
/**
     * 打印日志
     *//*

    private void writeAccessLog(SysOperLog sysOperLog) {
        log.info(sysOperLog.toString());
    }


    private Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }
}
*/
