package ro.ase.ism_2026;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface FXRatesDAO {

    @Insert
    long insert(FXRate fxRate);

    @Insert
    long[] insert(List<FXRate> fxRates);

    @Query("select * from fxrates")
    List<FXRate> getAll();

    @Delete
    void delete(FXRate fxRate);

    @Query("delete from fxrates")
    void deleteAll();

    @Query("select * from fxrates where date=:date")
    List<FXRate> getAllByDate(String date);
}
