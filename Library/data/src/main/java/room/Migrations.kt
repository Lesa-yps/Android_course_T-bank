package room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // для таблицы books
        database.execSQL(
            "ALTER TABLE books ADD COLUMN addedDate INTEGER NOT NULL DEFAULT 0"
        )

        // для таблицы newspapers
        database.execSQL(
            "ALTER TABLE newspapers ADD COLUMN addedDate INTEGER NOT NULL DEFAULT 0"
        )

        // для таблицы disks
        database.execSQL(
            "ALTER TABLE disks ADD COLUMN addedDate INTEGER NOT NULL DEFAULT 0"
        )
    }
}