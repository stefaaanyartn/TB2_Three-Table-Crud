package com.stefany.three_table_crud.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stefany.three_table_crud.Util.MyApp;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "student-db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper databaseHelper;

    // Declare constants for table and column names
    private static final String TABLE_STUDENT = "student";
    private static final String STUDENT_ID = "student_id";
    private static final String STUDENT_NAME = "student_name";
    private static final String STUDENT_REGISTRATION_NUM = "student_registration_num";
    private static final String STUDENT_PHONE = "student_phone";
    private static final String STUDENT_EMAIL = "student_email";

    private static final String TABLE_SUBJECT = "subject";
    private static final String SUBJECT_ID = "subject_id";
    private static final String SUBJECT_NAME = "subject_name";
    private static final String SUBJECT_CODE = "subject_code";
    private static final String SUBJECT_CREDIT = "subject_credit";

    private static final String TABLE_STUDENT_SUBJECT = "student_subject";
    private static final String STUDENT_ID_FK = "student_id_fk";
    private static final String SUBJECT_ID_FK = "subject_id_fk";
    private static final String STUDENT_SUB_CONSTRAINT = "student_sub_constraint";

    private DatabaseHelper() {
        super(MyApp.context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance() {

        if (databaseHelper == null) {
            synchronized (DatabaseHelper.class){ //thread safe singleton
                if (databaseHelper == null)
                    databaseHelper = new DatabaseHelper();
            }
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + " ("
                + STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUDENT_NAME + " TEXT NOT NULL, "
                + STUDENT_REGISTRATION_NUM + " INTEGER NOT NULL UNIQUE, "
                + STUDENT_PHONE + " TEXT, " //nullable
                + STUDENT_EMAIL + " TEXT " //nullable
                + ")";
        sqLiteDatabase.execSQL(CREATE_STUDENT_TABLE);

        String CREATE_SUBJECT_TABLE = "CREATE TABLE " + TABLE_SUBJECT + "("
                + SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SUBJECT_NAME + " TEXT NOT NULL, "
                + SUBJECT_CODE + " INTEGER NOT NULL UNIQUE, "
                + SUBJECT_CREDIT + " REAL" //nullable
                + ")";

        String CREATE_TAKEN_SUBJECT_TABLE = "CREATE TABLE " + TABLE_STUDENT_SUBJECT + "("
                + STUDENT_ID_FK + " INTEGER NOT NULL, "
                + SUBJECT_ID_FK + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + STUDENT_ID_FK + ") REFERENCES " + TABLE_STUDENT + "(" + STUDENT_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "FOREIGN KEY (" + SUBJECT_ID_FK + ") REFERENCES " + TABLE_SUBJECT + "(" + SUBJECT_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CONSTRAINT " + STUDENT_SUB_CONSTRAINT + " UNIQUE (" + STUDENT_ID_FK + "," + SUBJECT_ID_FK + ")"
                + ")";

        sqLiteDatabase.execSQL(CREATE_SUBJECT_TABLE);
        sqLiteDatabase.execSQL(CREATE_TAKEN_SUBJECT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_SUBJECT);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly())
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}