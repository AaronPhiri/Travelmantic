package com.example.aphiri.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import javax.xml.transform.Result;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText textTitle;
    EditText textPrice;
    EditText textDescription;
    TravelDeal deal;
    Button button;
    public static  final int PICTURE_RESULT = 42;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        FirebaseUtil.openFbReference(getString(R.string.deal_repository),this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference =FirebaseUtil.mDatabaseReference;


        textTitle = findViewById(R.id.textTitle);
        textPrice = findViewById(R.id.textPrice);
        textDescription = findViewById(R.id.textDescription);
        imageView = findViewById(R.id.imageDeal);
        final Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if ( deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        textTitle.setText(deal.getTitle());
        textDescription.setText(deal.getDescription());
        textPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
        button = findViewById(R.id.upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/jpeg");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY,true);

                startActivityForResult(intent1.createChooser(intent1,"Insert Picture"),PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        return  true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            StorageReference ref = FirebaseUtil.mStorageReference.child(uri.getLastPathSegment());
            ref.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getDownloadUrl().toString();
                    deal.setImageUrl(url);
                    showImage(url);
                    showImage(url);
                }

            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage(e.toString());
                }
            });
        }
    }

    private void showMessage(String e) {
        Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this,"Deal saved",Toast.LENGTH_LONG).show();
                clean();
    BackToList();
                return true;
            case R.id.delete_deal:
                deleteDeal();
                Toast.makeText(this,"Deal deleted",Toast.LENGTH_LONG).show();
                BackToList();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    private void clean() {
        textTitle.setText("");
        textPrice.setText("");
        textDescription.setText("");
    }

    private void saveDeal() {
        deal.setTitle(textTitle.getText().toString());
        deal.setPrice(textPrice.getText().toString());
        deal.setDescription(textDescription.getText().toString());
        if ( deal.getId() == null){
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }


    }
    private void deleteDeal(){
        if(deal == null){
            Toast.makeText(this,"Please Save deal before deleting",Toast.LENGTH_LONG).show();
        }
        mDatabaseReference.child(deal.getId()).removeValue();
    }
    private void BackToList(){
        Intent intent = new Intent(this,ListActivity.class);
        startActivity(intent);
    }
    private void  showImage(String url){
        if (url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(imageView.getContext()).load(url).resize(width,width*2/3).centerCrop().into(imageView);
        }
    }
}
