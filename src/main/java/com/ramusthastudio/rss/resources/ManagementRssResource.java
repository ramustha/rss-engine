package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.dao.DuplicateItemDao;
import com.ramusthastudio.rss.dao.ItemDao;
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

@Path("management")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ManagementRssResource {

    @GET
    @Path("/rss/{id}")
    public Uni<PanacheEntityBase> getItemBy(@NotBlank @PathParam("id") String id) {
        return ItemDao.findById(id);
    }

    @GET
    @Path("/rss")
    public Uni<List<PanacheEntityBase>> getItem(@Context UriInfo request) {
        return ItemDao.find(request, ItemDao.class);
    }

    @GET
    @Path("/duplicate-item")
    public Uni<List<PanacheEntityBase>> getDuplicateItem(@Context UriInfo request) {
        return DuplicateItemDao.find(request, DuplicateItemDao.class);
    }
}