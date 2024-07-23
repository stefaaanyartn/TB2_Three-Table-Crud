package com.stefany.three_table_crud.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.stefany.three_table_crud.Model.Subject;
import com.stefany.three_table_crud.Model.TakenSubject;

import java.util.ArrayList;
import java.util.List;

import static com.stefany.three_table_crud.Util.Constants.*;

public class TakenSubjectQueryImplementation implements QueryContract.TakenSubjectQuery {

    private DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

    @Override
    public void createTakenSubject(int studentId, int subjectId, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(STUDENT_ID_FK, studentId);
        contentValues.put(SUBJECT_ID_FK, subjectId);

        try {
            long rowCount = sqLiteDatabase.insertOrThrow(TABLE_STUDENT_SUBJECT, null, contentValues);

            if (rowCount>0)
                response.onSuccess(true);
            else
                response.onFailure("Subject assign failed");

        } catch (SQLiteException e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    @Override
    public void readAllTakenSubjectByStudentId(int studentId, QueryResponse<List<Subject>> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String QUERY = "SELECT s." + SUBJECT_ID + ", s." + SUBJECT_NAME + ", s." + SUBJECT_CODE + ", s." + SUBJECT_CREDIT + " "
                + "FROM " + TABLE_SUBJECT + " as s "
                + "JOIN " + TABLE_STUDENT_SUBJECT + " as ss ON s." + SUBJECT_ID + " = ss." + SUBJECT_ID_FK + " "
                + "WHERE ss." + STUDENT_ID_FK + " = ?";

        Cursor cursor = null;
        try {
            List<Subject> subjectList = new ArrayList<>();
            // Jalankan query dengan parameter
            cursor = sqLiteDatabase.rawQuery(QUERY, new String[]{String.valueOf(studentId)});

            // Periksa apakah kolom yang diperlukan ada
            int idIndex = cursor.getColumnIndex(SUBJECT_ID);
            int nameIndex = cursor.getColumnIndex(SUBJECT_NAME);
            int codeIndex = cursor.getColumnIndex(SUBJECT_CODE);
            int creditIndex = cursor.getColumnIndex(SUBJECT_CREDIT);

            if (idIndex == -1 || nameIndex == -1 || codeIndex == -1 || creditIndex == -1) {
                response.onFailure("One or more columns not found in query result.");
                return;
            }

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(idIndex);
                    String subjectName = cursor.getString(nameIndex);
                    int subjectCode = cursor.getInt(codeIndex);
                    double subjectCredit = cursor.getDouble(creditIndex);

                    Subject subject = new Subject(id, subjectName, subjectCode, subjectCredit);
                    subjectList.add(subject);
                } while (cursor.moveToNext());

                response.onSuccess(subjectList);
            } else {
                response.onFailure("There are no subjects assigned to this student.");
            }
        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqLiteDatabase.close();
        }
    }



    @Override

    public void readAllSubjectWithTakenStatus(int studentId, QueryResponse<List<TakenSubject>> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        // Query SQL dengan placeholder
        String QUERY = "SELECT s." + SUBJECT_ID + ", s." + SUBJECT_NAME + ", s." + SUBJECT_CODE + ", s." + SUBJECT_CREDIT + ", ss." + STUDENT_ID_FK + " "
                + "FROM " + TABLE_SUBJECT + " as s "
                + "LEFT JOIN " + TABLE_STUDENT_SUBJECT + " as ss ON s." + SUBJECT_ID + " = ss." + SUBJECT_ID_FK + " "
                + "AND ss." + STUDENT_ID_FK + " = ?";

        Cursor cursor = null;
        try {
            List<TakenSubject> takenSubjectList = new ArrayList<>();
            // Jalankan query dengan parameter
            cursor = sqLiteDatabase.rawQuery(QUERY, new String[]{String.valueOf(studentId)});

            // Periksa apakah kolom yang diperlukan ada
            int idIndex = cursor.getColumnIndex(SUBJECT_ID);
            int nameIndex = cursor.getColumnIndex(SUBJECT_NAME);
            int codeIndex = cursor.getColumnIndex(SUBJECT_CODE);
            int creditIndex = cursor.getColumnIndex(SUBJECT_CREDIT);
            int studentIdIndex = cursor.getColumnIndex(STUDENT_ID_FK);

            if (idIndex == -1 || nameIndex == -1 || codeIndex == -1 || creditIndex == -1 || studentIdIndex == -1) {
                response.onFailure("One or more columns not found in query result.");
                return;
            }

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(idIndex);
                    String subjectName = cursor.getString(nameIndex);
                    int subjectCode = cursor.getInt(codeIndex);
                    double subjectCredit = cursor.getDouble(creditIndex);

                    // Periksa apakah mata pelajaran diambil
                    boolean isTaken = cursor.getInt(studentIdIndex) > 0;

                    TakenSubject takenSubject = new TakenSubject(id, subjectName, subjectCode, subjectCredit, isTaken);
                    takenSubjectList.add(takenSubject);
                } while (cursor.moveToNext());

                response.onSuccess(takenSubjectList);
            } else {
                response.onFailure("There are no subjects assigned to this student.");
            }
        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    @Override
    public void deleteTakenSubject(int studentId, int subjectId, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            long rowCount = sqLiteDatabase.delete(TABLE_STUDENT_SUBJECT,
                    STUDENT_ID_FK + " =? AND " + SUBJECT_ID_FK + " =? ",
                    new String[]{String.valueOf(studentId), String.valueOf(subjectId)});

            if (rowCount>0)
                response.onSuccess(true);
            else
                response.onFailure("Assigned subject deletion failed");

        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }
}
