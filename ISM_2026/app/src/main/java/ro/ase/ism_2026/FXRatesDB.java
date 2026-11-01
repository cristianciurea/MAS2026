package ro.ase.ism_2026;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FXRate.class}, version = 1, exportSchema = false)
public abstract class FXRatesDB extends RoomDatabase {

    public static final String DB_NAME = "fxrates.db";
    private static FXRatesDB instance;

    public static FXRatesDB getInstance(Context context)
    {
        if (instance==null)
            instance = Room.databaseBuilder(context, FXRatesDB.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }

    public abstract FXRatesDAO getFXRatesDao();
}
