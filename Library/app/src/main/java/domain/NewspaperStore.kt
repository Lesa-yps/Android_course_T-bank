package domain

import com.github.javafaker.Faker
import kotlin.random.Random
import java.util.Locale


class NewspaperStore: Store<Newspaper> {
    override fun sell(): Newspaper {
        val faker = Faker(Locale("ru"))

        val id = Random.nextInt(100, 1000)
        val isAvailable = true
        val name = faker.book().title()
        val issueNumber = Random.nextInt(1, 5000)
        val month = Random.nextInt(1, 13)

        return Newspaper(id, isAvailable, name, issueNumber, month)
    }
}