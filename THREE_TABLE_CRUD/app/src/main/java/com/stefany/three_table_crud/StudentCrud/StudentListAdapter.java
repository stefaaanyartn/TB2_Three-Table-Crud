package com.stefany.three_table_crud.StudentCrud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stefany.three_table_crud.R;
import com.stefany.three_table_crud.Database.*;
import com.stefany.three_table_crud.StudentCrud.StudentCrudListener;
import com.stefany.three_table_crud.StudentCrud.StudentUpdateDialogFragment;
import com.stefany.three_table_crud.TakenCrud.StudentTakenSubjectActivity;
import com.stefany.three_table_crud.Model.Student;

import java.util.List;

import static com.stefany.three_table_crud.Util.Constants.*;

public class StudentListAdapter extends RecyclerView.Adapter<StudentViewHolder> {

    private Context context;
    private List<Student> studentList;
    private StudentCrudListener listener;

    StudentListAdapter(Context context, List<Student> studentList, StudentCrudListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_card_view, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        final Student student = studentList.get(position);

        holder.nameTextView.setText(student.getName());
        holder.registrationNumTextView.setText(String.valueOf(student.getRegistrationNumber()));
        holder.emailTextView.setText(student.getEmail());
        holder.phoneTextView.setText(student.getPhone());

        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentUpdateDialogFragment dialogFragment = StudentUpdateDialogFragment.newInstance(student, "Update Student", new StudentCrudListener() {
                    @Override
                    public void onStudentListUpdate(boolean inUpdated) {
                        listener.onStudentListUpdate(inUpdated);
                    }
                });
                dialogFragment.show(((StudentListActivity) context).getSupportFragmentManager(), UPDATE_STUDENT);
            }
        });

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(student.getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentTakenSubjectActivity.class);
                intent.putExtra(STUDENT_ID, student.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    private void showConfirmationDialog(final int studentId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete this student?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        QueryContract.StudentQuery query = new StudentQueryImplementation();
                        query.deleteStudent(studentId, new QueryResponse<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                if(data) {
                                    Toast.makeText(context, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                                    listener.onStudentListUpdate(true);
                                }
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
