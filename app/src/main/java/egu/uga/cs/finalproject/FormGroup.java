package egu.uga.cs.finalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FormGroup extends AppCompatActivity {

    FirebaseAuth fAuth;
    String groupID;
    String groupTitle;

    EditText groupName;
    TextView groupCode;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formgroup);


        groupName = (EditText) findViewById(R.id.groupName);
        groupCode = (TextView) findViewById(R.id.groupCode);


        button = (Button) findViewById(R.id.button2);
        fAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                groupTitle = groupName.getText().toString();
                groupID = "" + System.currentTimeMillis();

                if (TextUtils.isEmpty(groupTitle)) {
                    groupName.setError("Group name is required");
                    return; //dont precede further

                } else {
                    createGroup(groupID, groupTitle);
                    groupCode.setText(groupID);
                }
            }
        }); //end of button
    }


    private void createGroup(final String groupID, String groupName) {

         HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("groupId", "" +groupID);
        hashMap.put("groupName", "" + groupName);
        hashMap.put("createdBy","" + fAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", fAuth.getUid());
                        hashMap1.put("role","creator");
                        hashMap1.put("timestamp", groupID);

                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                        ref1.child(groupID).child("Participants").child(fAuth.getUid())
                                .setValue(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(FormGroup.this,"Group created", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

    }

}


