package com.ramusthastudio.rss.helper;

import io.smallrye.mutiny.Uni;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

public class HttpServerLogger {
    @Inject Logger log;

    @ServerRequestFilter
    public Uni<Void> requestFilter(HttpServerRequest request, UriInfo uriInfo) {
        MultiMap headers = request.headers();
        HttpMethod method = request.method();
        String hostAddress = request.remoteAddress().toString();
        String path = uriInfo.getPath();
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

        headers.forEach(h -> log.debugf("incoming header %s:%s", h.getKey(), h.getValue()));
        log.debugf("incoming hostAddress %s", hostAddress);
        log.debugf("incoming method %s", method);
        log.debugf("incoming path  %s", path);
        queryParameters.forEach((k, v) -> log.debugf("incoming queryPath %s:%s", k, v));
        request.body(bufferAsyncResult -> log.debugf("incoming body %s", bufferAsyncResult.result().toJson()));

        return Uni.createFrom().nullItem();
    }

    @ServerResponseFilter
    public Uni<Void> responseFilter(HttpServerResponse response) {
        MultiMap headers = response.headers();

        headers.forEach(h -> log.debugf("outgoing header %s:%s", h.getKey(), h.getValue()));
        log.debugf("outgoing status code %s %s", response.getStatusCode(), response.getStatusMessage());

        return Uni.createFrom().nullItem();
    }
}
