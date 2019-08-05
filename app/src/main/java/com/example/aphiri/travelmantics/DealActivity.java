package com.example.aphiri.travelmantics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText textTitle;
    EditText textPrice;
    EditText textDescription;
    TravelDeal deal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        FirebaseUtil.openFbReference(getString(R.string.deal_repository));
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference =FirebaseUtil.mDatabaseReference;


        textTitle = findViewById(R.id.textTitle);
        textPrice = findViewById(R.id.textPrice);
        textDescription = findViewById(R.id.textDescription);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if ( deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        textTitle.setText(deal.getTitle());
        textDescription.setText(deal.getDescription());
        textPrice.setText(deal.getPrice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        return  true;
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
}
