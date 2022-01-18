package com.ramusthastudio.rss.helper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class QueryFilter {
    public static final Map<String, String> OPERANDS;

    static {
        OPERANDS = Maps.newHashMap();
        OPERANDS.put("eq", "=");
        OPERANDS.put("ne", "<>");
        OPERANDS.put("gt", ">");
        OPERANDS.put("lt", "<");
        OPERANDS.put("ge", ">=");
        OPERANDS.put("le", "<=");
        OPERANDS.put("ntn", "NOT NULL");
        OPERANDS.put("nll", "IS NULL");
        OPERANDS.put("lk", "LIKE");
        OPERANDS.put("nlk", "NOT LIKE");
        OPERANDS.put("in", "IN");
        OPERANDS.put("nin", "NOT IN");
        OPERANDS.put("or", "OR");
        OPERANDS.put("and", "AND");
    }

    private QueryFilter() {
    }


    @SuppressWarnings("unchecked")
    public static Object findPossibleType(String fieldCandidate, Object value, Class<?> entity) {
        if (entity == null) {
            return null;
        }

        try {
            Field field = entity.getDeclaredField(fieldCandidate);
            Class<?> fieldDeclaringClass = field.getType();
            if (fieldDeclaringClass.getEnumConstants() != null) {
                List<Object> enumConstantList = Lists.newArrayList();
                for (Object enumConstant : fieldDeclaringClass.getEnumConstants()) {
                    if (value instanceof List) {
                        List<Object> objectList = (List<Object>) value;
                        enumConstantList.addAll(objectList.stream()
                                .map(v -> enumConstant.toString().equals(v) ? enumConstant : null)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    } else {
                        if (enumConstant.toString().equals(value)) {
                            return enumConstant;
                        }
                    }
                }

                if (!enumConstantList.isEmpty()) {
                    return enumConstantList;
                }
            }
        } catch (Exception ignored) {
            System.err.println("no enums found");
        }

        try {
            Field declaredField = entity.getDeclaredField(fieldCandidate);
            Class<?> type = declaredField.getType();

            String dateFormat = "yyyy-MM-dd";
            String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

            if (value.toString().length() == 10) {
                if (type.isAssignableFrom(Date.class)) {
                    return new SimpleDateFormat(dateFormat).parse(value.toString());
                } else if (type.isAssignableFrom(LocalDate.class)) {
                    return LocalDate.from(DateTimeFormatter.ofPattern(dateFormat).parse(value.toString()));
                } else if (type.isAssignableFrom(LocalDateTime.class)) {
                    return LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimeFormat).parse(String.format("%sT00:00:00", value)));
                } else if (type.isAssignableFrom(Instant.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
                            .withZone(ZoneId.from(ZoneOffset.UTC));
                    return Instant.from(formatter.parse(String.format("%sT00:00:00", value)));
                } else if (type.isAssignableFrom(ZonedDateTime.class)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
                            .withZone(ZoneId.from(ZoneOffset.UTC));
                    return ZonedDateTime.from(formatter.parse(String.format("%sT00:00:00", value)));
                }
            }

            if (type.isAssignableFrom(Date.class)) {
                return new SimpleDateFormat(dateTimeFormat).parse(value.toString());
            } else if (type.isAssignableFrom(LocalDate.class)) {
                return LocalDate.from(DateTimeFormatter.ofPattern(dateTimeFormat).parse(value.toString()));
            } else if (type.isAssignableFrom(LocalDateTime.class)) {
                return LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimeFormat).parse(value.toString()));
            } else if (type.isAssignableFrom(Instant.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
                        .withZone(ZoneId.from(ZoneOffset.UTC));
                return Instant.from(formatter.parse(value.toString()));
            } else if (type.isAssignableFrom(ZonedDateTime.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat)
                        .withZone(ZoneId.from(ZoneOffset.UTC));
                return ZonedDateTime.from(formatter.parse(value.toString()));
            }
        } catch (Exception ignored) {
            System.err.println("no datetime found");
            return findPossibleType(fieldCandidate, value, entity.getSuperclass());
        }
        return null;
    }

    public static Map<String, Object> generateQuery(UriInfo request, Class<?> entity) {
        Map<String, Object> result = Maps.newHashMap();

        MultivaluedMap<String, String> query = request.getQueryParameters();
        result.put("order", query.getOrDefault("order", Collections.singletonList("asc")).get(0));
        result.put("index", Integer.parseInt(query.getOrDefault("index", Collections.singletonList("0")).get(0)));
        result.put("size", Integer.parseInt(query.getOrDefault("size", Collections.singletonList("20")).get(0)));

        List<String> finalQuery = Lists.newArrayList();
        Map<String, Object> parameters = Maps.newHashMap();

        for (Map.Entry<String, List<String>> entry : query.entrySet()) {
            String field = entry.getKey();

            if (field.equals("sort") || field.equals("order") || field.equals("index") || field.equals("size")) {
                continue;
            }

            String operand = QueryFilter.OPERANDS.keySet().stream()
                    .filter(k -> field.contains(String.format("-%s", k)))
                    .findFirst()
                    .orElse(null);

            String finalField;
            if (operand == null) {
                operand = "eq";
                finalField = field;
            } else {
                finalField = field.substring(0, field.indexOf(operand) - 1);
            }

            if (operand.equals("in") || operand.equals("nin")) {
                finalQuery.add(String.format("%s %s :%s", finalField, QueryFilter.OPERANDS.get(operand), finalField));
                Object enumType = findPossibleType(finalField, entry.getValue(), entity);
                parameters.put(finalField, Objects.requireNonNullElse(enumType, entry.getValue().stream()
                        .map(e -> Lists.newArrayList(e.split(","))).findFirst().orElse(Lists.newArrayList())));
            } else if (operand.equals("lk") || operand.equals("nlk")) {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    finalQuery.add(String.format("lower(%s) %s :%s", finalField, QueryFilter.OPERANDS.get(operand), finalField + i));
                    Object enumType = findPossibleType(finalField, entry.getValue().get(i), entity);
                    parameters.put(finalField + i, Objects.requireNonNullElse(enumType, "%" + entry.getValue().get(i).toLowerCase() + "%"));
                }
            } else {
                for (int i = 0; i < entry.getValue().size(); i++) {
                    finalQuery.add(String.format("%s %s :%s", finalField, QueryFilter.OPERANDS.get(operand), finalField + i));
                    Object enumType = findPossibleType(finalField, entry.getValue().get(i), entity);
                    parameters.put(finalField + i, Objects.requireNonNullElse(enumType, entry.getValue().get(i)));
                }
            }
        }

        result.put("query", String.join(" AND ", finalQuery));
        result.put("parameters", parameters);

        if (query.get("sort") != null) {
            String sort = query.get("sort").get(0);
            boolean isAscending = result.get("order").equals("asc");
            result.put("sort", Sort.by(sort,
                    isAscending ? Sort.Direction.Ascending : Sort.Direction.Descending));
        } else {
            result.put("sort", Sort.empty());
        }

        return result;
    }
}
