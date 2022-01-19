package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.dao.ChannelDao;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
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
    public Uni<List<PanacheEntityBase>> getChannel(@Context UriInfo request) {
        return ChannelDao.getFilter(request, ChannelDao.class);
    }
}
