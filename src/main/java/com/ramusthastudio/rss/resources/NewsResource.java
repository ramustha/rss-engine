package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.dao.NewsDao;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("news")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NewsResource {

    @GET
    @Path("/{id}")
    public Uni<PanacheEntityBase> getNewsBy(@NotBlank @PathParam("id") String id) {
        return NewsDao.findById(id);
    }

    @GET
    @Path("/search")
    public Uni<List<PanacheEntityBase>> getNews(@Context UriInfo request) {
        return NewsDao.find(request, NewsDao.class);
    }
}
