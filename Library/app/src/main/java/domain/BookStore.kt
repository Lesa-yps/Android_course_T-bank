package domain

import com.github.javafaker.Faker
import kotlin.random.Random
import java.util.Locale


class BookStore : Store<Book> {
    override fun sell(): Book {
        val faker = Faker(Locale("ru"))

        val id = Random.nextInt(100, 1000)
        val isAvailable = true
        val title = faker.book().title()
        val author = faker.book().author()
        val pages = Random.nextInt(100, 1500)

        return Book(id, isAvailable, title, pages, author)
    }
}
