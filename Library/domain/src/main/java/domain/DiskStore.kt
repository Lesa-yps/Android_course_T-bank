package domain

import com.github.javafaker.Faker
import kotlin.random.Random
import java.util.Locale


class DiskStore : Store<Disk> {
    override fun sell(): Disk {
        val faker = Faker(Locale("ru"))

        val id = Random.nextInt(100, 1000)
        val isAvailable = true
        val name = faker.book().title()
        val type = DiskType.entries.toTypedArray().random()

        return Disk(id, isAvailable, name, type)
    }
}