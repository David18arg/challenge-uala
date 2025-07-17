package com.david.cityapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.david.cityapp.data.local.dao.CityDao
import com.david.cityapp.domain.model.City

@Database(
    entities = [City::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE cities_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        cityId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        country TEXT NOT NULL,
                        lat REAL NOT NULL,
                        lon REAL NOT NULL,
                        isFavorite INTEGER NOT NULL,
                        searchableName TEXT NOT NULL,
                        UNIQUE(name, country)
                    )
                """.trimIndent())

                // Copy data from old table to new table
                database.execSQL("""
                    INSERT INTO cities_new (id, cityId, name, country, lat, lon, isFavorite, searchableName)
                    SELECT id, id as cityId, name, country, lat, lon, isFavorite, 
                           LOWER(name || ',' || country) as searchableName 
                    FROM cities
                """.trimIndent())

                // Remove old table
                database.execSQL("DROP TABLE cities")

                // Rename new table
                database.execSQL("ALTER TABLE cities_new RENAME TO cities")

                // Create index on searchableName for better search performance
                database.execSQL("CREATE INDEX index_cities_searchableName ON cities (searchableName)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cities_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}