package com.example.fixture;

import com.example.model.Post;
import net.datafaker.Faker;

import java.util.List;
import java.util.stream.IntStream;

public final class PostTestDataFactory {

    private static final Faker FAKER = new Faker();

    private PostTestDataFactory() {
    }

    public static Post createPost() {
        return new Post(
                0L,
                FAKER.book().title(),
                FAKER.lorem().paragraph(),
                List.of(
                        FAKER.programmingLanguage().name(),
                        FAKER.programmingLanguage().name()
                ),
                0L,
                0L,
                null
        );
    }

    public static List<Post> createMany(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createPost())
                .toList();
    }
}