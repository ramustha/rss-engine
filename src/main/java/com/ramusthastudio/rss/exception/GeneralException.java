package com.ramusthastudio.rss.exception;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class GeneralException {
    @Inject Logger log;

    @ServerExceptionMapper public RestResponse<ErrorMapper> mapException(RuntimeException e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException web = (WebApplicationException) e;
            Response response = web.getResponse();
            RestResponse<ErrorMapper> finalResponse = RestResponse
                    .status(response.getStatusInfo(),
                            new ErrorMapper(response.getStatus(), e.getClass().getSimpleName(), e.getMessage()));

            log.error("catch error with entity", e);
            return finalResponse;
        }
        RestResponse<ErrorMapper> finalResponse = RestResponse
                .status(Response.Status.INTERNAL_SERVER_ERROR,
                        new ErrorMapper(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getClass().getSimpleName(), e.getMessage()));

        log.fatal("unexpected error with entity", e);
        return finalResponse;
    }

    static class ErrorMapper {
        public int code;
        public String clazz;
        public String errorMessage;

        public ErrorMapper(int code, String clazz, String errorMessage) {
            this.code = code;
            this.clazz = clazz;
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() {
            return "ErrorMapper{" +
                    "code=" + code +
                    ", clazz='" + clazz + '\'' +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}
