package com.stefany.three_table_crud.Util;

public class Constants {

    //column names of student table
    public static final String TABLE_STUDENT = "student";
    public static final String STUDENT_ID = "student_id";
    public static final String STUDENT_NAME = "student_name";
    public static final String STUDENT_REGISTRATION_NUM = "student_registration_num";
    public static final String STUDENT_PHONE = "student_phone";
    public static final String STUDENT_EMAIL = "student_email";

    //column names of subject table
    public static final String TABLE_SUBJECT = "subject";
    public static final String SUBJECT_ID = "subject_id";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String SUBJECT_CODE = "subject_code";
    public static final String SUBJECT_CREDIT = "subject_credit";

    //column names of student_subject pivot table
    public static final String TABLE_STUDENT_SUBJECT = "student_subject";
    public static final String STUDENT_ID_FK = "student_id_fk";  // Corrected to match schema
    public static final String SUBJECT_ID_FK = "subject_id_fk";  // Corrected to match schema
    public static final String STUDENT_SUB_CONSTRAINT = "student_sub_unique";

    //others for general purpose key-value pair data
    public static final String TITLE = "title";
    public static final String CREATE_STUDENT = "create_student";
    public static final String UPDATE_STUDENT = "update_student";
    public static final String CREATE_SUBJECT = "create_subject";
    public static final String UPDATE_SUBJECT = "update_subject";
}
