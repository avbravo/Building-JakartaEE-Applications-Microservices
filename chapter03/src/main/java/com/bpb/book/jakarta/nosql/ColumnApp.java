package com.bpb.book.jakarta.nosql;

import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.mapping.column.ColumnTemplate;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import java.util.Optional;

import static jakarta.nosql.column.ColumnDeleteQuery.delete;

public class ColumnApp {

    public static void main(String[] args) throws InterruptedException {

        try (SeContainer container = SeContainerInitializer
                .newInstance().initialize()) {

            God diana = new God(1L, "Diana", "Hunt");

            ColumnTemplate template =
                    container.select(ColumnTemplate.class)
                            .get();

            template.insert(diana);

            final Optional<God> god = template.find(God.class, 1L);
            System.out.println("query : " + god);

            ColumnDeleteQuery deleteQuery = delete().from("God")
                    .where("_id").eq(1L).build();

            template.delete(deleteQuery);

            System.out.println("query again: " +
                    template.find(God.class, 1L));
        }
        System.exit(0);
    }
}
