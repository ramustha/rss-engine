package com.ramusthastudio.rss.helper;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Provider
public class HttpClientLogger implements ClientResponseFilter, ClientRequestFilter {
    @Inject Logger log;

    @Override
    public void filter(ClientRequestContext ctx) {
        MultivaluedMap<String, Object> headers = ctx.getHeaders();
        Map<String, Cookie> cookies = ctx.getCookies();
        MediaType mediaType = ctx.getMediaType();
        String method = ctx.getMethod();
        Object payload = ctx.hasEntity() ? ctx.getEntity() : "";
        String path = ctx.getUri().toString();

        headers.forEach((k, v) -> log.debugf("client request Header %s:%s", k, v));
        cookies.forEach((k, v) -> log.debugf("client request Cookie %s:%s", k, v));

        log.debugf("client request %s, %s", method, mediaType);
        log.debugf("client request %s", path);
        log.debugf("client request %s", payload);
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        MultivaluedMap<String, String> headers = responseContext.getHeaders();
        Map<String, NewCookie> cookies = responseContext.getCookies();
        MediaType mediaType = responseContext.getMediaType();
        String payload = responseContext.hasEntity() ? IOUtils.toString(new InputStreamReader(responseContext.getEntityStream())) : "";
        int status = responseContext.getStatus();
        String path = requestContext.getUri().toString();

        if (responseContext.hasEntity()) {
            // copy again
            responseContext.setEntityStream(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));
        }

        headers.forEach((k, v) -> log.debugf("client response Header %s:%s", k, v));
        cookies.forEach((k, v) -> log.debugf("client response Cookie %s:%s", k, v));

        log.debugf("client response %s %s", status, mediaType);
        log.debugf("client response %s", payload);
        log.debugf("client response %s", path);
    }
}
