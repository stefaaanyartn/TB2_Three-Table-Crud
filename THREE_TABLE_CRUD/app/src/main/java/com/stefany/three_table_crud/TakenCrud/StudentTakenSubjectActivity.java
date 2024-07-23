package com.stefany.three_table_crud.TakenCrud;

import static com.stefany.three_table_crud.Util.Constants.STUDENT_ID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stefany.three_table_crud.Database.QueryContract;
import com.stefany.three_table_crud.Database.QueryResponse;
import com.stefany.three_table_crud.Database.StudentQueryImplementation;
import com.stefany.three_table_crud.Database.TableRowCountQueryImplementation;
import com.stefany.three_table_crud.Database.TakenSubjectQueryImplementation;
import com.stefany.three_table_crud.Model.Student;
import com.stefany.three_table_crud.Model.Subject;
import com.stefany.three_table_crud.Model.TableRowCount;
import com.stefany.three_table_crud.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentTakenSubjectActivity extends AppCompatActivity implements TakenSubjectCrudListener {

    private TextView nameTextView;
    private TextView registrationNumTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private ImageView actionAddSubject;

    private RecyclerView recyclerView;
    private TextView noDataFoundTextView;
    private TextView studentCountTextView;
    private TextView subjectCountTextView;
    private TextView takenSubjectCountTextView;

    private int studentId;
    private final List<Subject> takenSubjectList = new ArrayList<>();
    private TakenSubjectListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_taken_subject);

        // Inisialisasi UI
        initialization();

        // Pastikan ActionBar ada
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        studentId = getIntent().getIntExtra(STUDENT_ID, -1);

        // Inisialisasi adapter dan RecyclerView
        adapter = new TakenSubjectListAdapter(this, studentId, takenSubjectList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // Tampilkan informasi mahasiswa
        showStudentInfo();

        // Set listener untuk tombol tambah mata pelajaran
        if (actionAddSubject != null) {
            actionAddSubject.setOnClickListener(v -> {
                Intent intent = new Intent(StudentTakenSubjectActivity.this, SubjectAssignActivity.class);
                intent.putExtra(STUDENT_ID, studentId);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTableRowCount();
        showTakenSubjectList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onTakenSubjectUpdated(boolean isUpdated) {
        showTableRowCount();
    }

    private void showStudentInfo() {
        QueryContract.StudentQuery query = new StudentQueryImplementation();
        query.readStudent(studentId, new QueryResponse<Student>() {
            @Override
            public void onSuccess(Student student) {
                if (student != null) {
                    nameTextView.setText(student.getName());
                    registrationNumTextView.setText(String.valueOf(student.getRegistrationNumber()));
                    emailTextView.setText(student.getEmail());
                    phoneTextView.setText(student.getPhone());
                } else {
                    showToast("Student not found.");
                }
            }

            @Override
            public void onFailure(String message) {
                showToast(message);
            }
        });
    }

    private void showTakenSubjectList() {
        QueryContract.TakenSubjectQuery query = new TakenSubjectQueryImplementation();
        query.readAllTakenSubjectByStudentId(studentId, new QueryResponse<List<Subject>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Subject> data) {
                recyclerView.setVisibility(View.VISIBLE);
                noDataFoundTextView.setVisibility(View.GONE);

                takenSubjectList.clear();
                takenSubjectList.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                recyclerView.setVisibility(View.GONE);
                noDataFoundTextView.setVisibility(View.VISIBLE);
                noDataFoundTextView.setText(message);
            }
        });
    }

    private void showTableRowCount() {
        QueryContract.TableRowCountQuery query = new TableRowCountQueryImplementation();
        query.getTableRowCount(new QueryResponse<TableRowCount>() {
            @Override
            public void onSuccess(TableRowCount data) {
                if (data != null) {
                    studentCountTextView.setText(getString(R.string.student_count, data.getStudentRow()));
                    subjectCountTextView.setText(getString(R.string.subject_count, data.getSubjectRow()));
                    takenSubjectCountTextView.setText(getString(R.string.taken_subject_count, data.getTakenSubjectRow()));
                } else {
                    showToast("Table row count data is null.");
                }
            }

            @Override
            public void onFailure(String message) {
                studentCountTextView.setText(getString(R.string.table_row_count_failed));
                subjectCountTextView.setText(message);
                takenSubjectCountTextView.setText("");
            }
        });
    }

    private void initialization() {
        nameTextView = findViewById(R.id.nameTextView);
        registrationNumTextView = findViewById(R.id.registrationNumTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        actionAddSubject = findViewById(R.id.action_add_subject);

        recyclerView = findViewById(R.id.recyclerView);
        noDataFoundTextView = findViewById(R.id.noDataFoundTextView);

        studentCountTextView = findViewById(R.id.studentCount);
        subjectCountTextView = findViewById(R.id.subjectCount);
        takenSubjectCountTextView = findViewById(R.id.takenSubjectCount);
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
