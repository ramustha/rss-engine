package com.ramusthastudio.rss.resources;

import com.ramusthastudio.rss.dao.ChannelDao;
import com.ramusthastudio.rss.dao.NewsDao;
import com.ramusthastudio.rss.helper.QueryFilter;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
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
import java.util.Map;

@Path("news")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NewsResource {

    @GET
    @Path("{id}")
    public Uni<ChannelDao> getNewsBy(@NotBlank @PathParam("id") String id) {
        return NewsDao.findById(id);
    }

    @GET
    @Path("search")
    @SuppressWarnings("unchecked")
    public Uni<List<PanacheEntityBase>> getNews(@Context UriInfo request) {
        Map<String, Object> map = QueryFilter.generateQuery(request, NewsDao.class);
        if (map.get("sort") == null) {
            return NewsDao
                    .find(map.get("query").toString(), (Map<String, Object>) map.get("parameters"))
                    .filter("deletedFilter", Parameters.with("isDeleted", false))
                    .page((int) map.get("index"), (int) map.get("size")).list()
                    .onFailure().recoverWithUni(() -> Uni.createFrom().item(List.of()));
        }
        return NewsDao
                .find(map.get("query").toString(), (Sort) map.get("sort"), (Map<String, Object>) map.get("parameters"))
                .filter("deletedFilter", Parameters.with("isDeleted", false))
                .page((int) map.get("index"), (int) map.get("size")).list()
                .onFailure().recoverWithUni(() -> Uni.createFrom().item(List.of()));
    }
}
