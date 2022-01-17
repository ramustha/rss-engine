package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.dao.ChannelDao;
import com.ramusthastudio.rss.helper.QueryFilter;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

@Path("management")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ManagementChannelResource {

    @POST
    @Path("channel")
    public Uni<ChannelDao> addChannel(@Valid ChannelDao request) {
        return Panache.withTransaction(request::persist);
    }

    @GET
    @Path("channel/{id}")
    public Uni<ChannelDao> getChannelBy(@NotBlank @PathParam("id") String id) {
        return ChannelDao.findById(id);
    }

    @DELETE
    @Path("channel/{id}")
    public Uni<Response> deleteChannelBy(@NotBlank @PathParam("id") String id) {
        return Panache.withTransaction(() -> ChannelDao.deleteById(id)
                .map(e -> e ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build()));
    }

    @PUT
    @Path("channel/{id}")
    public Uni<ChannelDao> updateChannel(@NotBlank @PathParam("id") String id,
                                         @Valid ChannelDao request) {
        return Panache.withTransaction(() -> ChannelDao.findById(id)
                .map(entity -> {
                    ChannelDao channelEntity = (ChannelDao) entity;
                    channelEntity.title = request.title;
                    channelEntity.description = request.description;
                    channelEntity.category = request.category;
                    channelEntity.language = request.language;
                    channelEntity.link = request.link;
                    channelEntity.deleted = request.deleted;
                    return channelEntity;
                }).onFailure().recoverWithNull());
    }

    @GET
    @Path("channel")
    @SuppressWarnings("unchecked")
    public Uni<List<ChannelDao>> getChannel(@Context UriInfo request) {
        Map<String, Object> map = QueryFilter.generateQuery(request, ChannelDao.class);
        if (map.get("sort") == null) {
            return ChannelDao
                    .find(map.get("query").toString(), (Map<String, Object>) map.get("parameters"))
                    .filter("deletedFilter", Parameters.with("isDeleted", false))
                    .page((int) map.get("index"), (int) map.get("size")).list();
        }
        return ChannelDao
                .find(map.get("query").toString(), (Sort) map.get("sort"), (Map<String, Object>) map.get("parameters"))
                .filter("deletedFilter", Parameters.with("isDeleted", false))
                .page((int) map.get("index"), (int) map.get("size")).list();
    }
}
