package com.example.p1.Database.PersonDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersonDao {
    @Query("SELECT * FROM Person")
    List<Person> getAllPersons();

    @Insert
    void insertPerson(Person person);
}
