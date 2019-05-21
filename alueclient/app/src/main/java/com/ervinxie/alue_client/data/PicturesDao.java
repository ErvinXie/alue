package com.ervinxie.alue_client.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PicturesDao {

    @Query("SELECT * FROM PICTURES WHERE Id = :id")
    Pictures getPicturesById(String id);

    @Query("SELECT * FROM PICTURES ORDER BY PictureId ")
    List<Pictures> getAllPictures();

    @Query("SELECT COUNT(*) FROM PICTURES")
    int getPicturesAmount();

    @Query("DELETE FROM PICTURES")
    void delete();


    @Insert
    void insert(Pictures pictures);

    @Insert
    void insertAll(Pictures... pictures);

    @Update
    void update(Pictures... pictures);

    @Delete()
    void delete(Pictures... pictures);

}
