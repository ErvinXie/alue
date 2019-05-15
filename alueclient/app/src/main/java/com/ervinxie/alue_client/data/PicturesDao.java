package com.ervinxie.alue_client.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PicturesDao {

    @Query("SELECT * FROM PICTURES WHERE PictureId = :id")
    Pictures getPicturesById(int id);

    @Query("SELECT * FROM PICTURES")
    List<Pictures> getAllPictures();

    @Insert
    void insert(Pictures pictures);

    @Insert
    void insertAll(Pictures... pictures);

    @Update
    void update(Pictures... pictures);

    @Delete
    void delete(Pictures... pictures);
}
