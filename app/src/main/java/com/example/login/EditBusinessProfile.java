package com.example.login;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EditBusinessProfile extends AppCompatActivity {
    int count=0;
    EditText edtName, edtLocation, edtEmail,edtContact;
    RushenBusinessImageAdapter adapter;
    RecyclerView rcspecial;
    Button addphotos;
    SpecialAdapter adapter1;
    ImageView Businessdisplayimage;
    RushenBusinessImageAdapter.BusinessViewHolder cvh;
    String name ;
    RecyclerView rc1;
    String location ;
    List<Specials> specials;
    String email ;
    String contactNum ;
    Connection connections;
    Statement statement;int delteid;

    ArrayList<BusinessImages> busimages;

    int UserID;
    private static final int REQUEST_CODE_GALLERY = 101;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref2 = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
        UserID = sharedPref2.getInt("businessID", -1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_profile);
        makeNice();
        specials = new ArrayList<>();
        adapter1 = new SpecialAdapter(specials);
        edtName = findViewById(R.id.editthebusinessname);
        edtLocation = findViewById(R.id.edtLocation);
        edtEmail = findViewById(R.id.edtEmail);
        edtContact = findViewById(R.id.edtContactNumber);
        addphotos = findViewById(R.id.btneditaddPhotos);
        Businessdisplayimage = findViewById(R.id.editBusinessImaeview);
        rcspecial = findViewById(R.id.ERVSpecials);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rcspecial.setLayoutManager(layoutManager);
        rcspecial.addItemDecoration(new SpaceItemDecoration(15));
        new specialsQueryAsyncTask().execute(UserID);
        setdata();
        setuprecyclers();
        new RetrieveBusinessTask().execute();
        setuprecyclers();


        adapter1.setOnClickListener(specials -> {
            try {
                SpecialAdapter.SpecialViewHolder viewHolder = (SpecialAdapter.SpecialViewHolder) rcspecial.findContainingViewHolder(specials);
                if (viewHolder != null && viewHolder.specials != null) {
                    SharedPreferences sharedPref4 = getSharedPreferences("Myprefs4", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor3 = sharedPref4.edit();
                    editor3.putInt("SpecialID", viewHolder.specials.getSpecID());
                    editor3.apply();
                    Intent intent = new Intent(EditBusinessProfile.this, Add_Update_special.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EditBusinessProfile.this, "Invalid item clicked", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                copyToClipboard(e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnClickListener(business -> {
            count++;
            cvh = (RushenBusinessImageAdapter.BusinessViewHolder) rc1.findContainingViewHolder(business);
            if (count%2!=1){cvh.btndelete.setVisibility(View.INVISIBLE);}
            if (count%2==1){
                cvh.btndelete.setVisibility(View.VISIBLE);
                if (cvh != null && cvh.business != null) {
                    cvh.btndelete.setOnClickListener(v1 -> {
                        delteid = cvh.business.imageID;
                        new Deleteimage().execute(delteid);
                    });
                }
            }


        });

    }
    private class Deleteimage extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            Connection dbconnect = com.example.login.DatabaseConnection.getInstance().getConnection();
            int name = params[0];

            String sql = "DELETE FROM businessimage WHERE businessImageID = ?;";

            try {
                PreparedStatement preparedStatement = dbconnect.prepareStatement(sql);
                preparedStatement.setInt(1, name);

                preparedStatement.executeUpdate();

                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                copyToClipboard(e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EditBusinessProfile.this, "Image deleted!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(EditBusinessProfile.this, "Error deleting image.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setuprecyclers(){
        count =0;
        busimages = new ArrayList<>();
        adapter = new RushenBusinessImageAdapter(busimages);
        rc1 = findViewById(R.id.recylerineditbus);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rc1.setLayoutManager(layoutManager);
        rc1.setAdapter(adapter);


    }

    public void AddSpecial(View view) {
        SharedPreferences sharedPref4 = getSharedPreferences("MyPrefs4", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sharedPref4.edit();
        editor3.putInt("SpecialID", -1);
        editor3.apply();
        Intent intent = new Intent(EditBusinessProfile.this, Add_Update_special.class);
        startActivity(intent);
    }

    private class specialsQueryAsyncTask extends  AsyncTask<Integer, Void, List<Specials>> {

        @Override
        protected List<Specials> doInBackground(Integer... integers) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Specials> fetchedSpecials = new ArrayList<>();

            try {
                String sql = "SELECT * FROM Specials WHERE businessID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,UserID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                {
                    int specID = resultSet.getInt("specID");
                    String name = resultSet.getString("specName");
                    String desc = resultSet.getString("specDescription");
                    int imgNum = resultSet.getInt("specImg");

                    Specials specials = new Specials(specID,name,desc,imgNum);
                    fetchedSpecials.add(specials);
                }
                resultSet.close();
                preparedStatement.close();

            } catch (Exception e) {
                Log.d("BusinessHomePage", "Number of specials: " + fetchedSpecials.size());
            }

            return fetchedSpecials;
        }

        @Override
        protected void onPostExecute(List<Specials> special)
        {
            if(specials != null)
            {
                specials.addAll(special);
                rcspecial.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
            }
        }

    }

    private class RetrieveBusinessTask extends AsyncTask<Void, Void, List<BusinessImages>> {
        @Override
        protected List<BusinessImages> doInBackground(Void... voids) {

            Connection connection = com.example.login.DatabaseConnection.getInstance().getConnection();
            List<BusinessImages> fetchedBusinesses = new ArrayList<>();

            try {
                String selectQuery = "SELECT image,businessImageID FROM businessimage WHERE businessID ="+UserID+" ";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    byte[] imageData1 = resultSet.getBytes("image");
                    int imgid = resultSet.getInt("businessImageID");

                    if (imageData1 != null && imageData1.length > 0) { // Check if imageData1 has a value
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        BusinessImages Busnesstemp = new BusinessImages(bitmap1);
                        Busnesstemp.imageID = imgid;
                        fetchedBusinesses.add(Busnesstemp);
                    }
                }

                resultSet.close();
                preparedStatement.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
            return fetchedBusinesses;
        }

        @Override
        protected void onPostExecute(List<BusinessImages> fetchedBusinesses) {
            if (fetchedBusinesses != null && !fetchedBusinesses.isEmpty()) {
                busimages.addAll(fetchedBusinesses);
                adapter.notifyDataSetChanged();
            }
        }
    }







    public void updatedetails(View view) throws SQLException {
        name = edtName.getText().toString();
        location = edtLocation.getText().toString();
        email = edtEmail.getText().toString();
        contactNum = edtContact.getText().toString();
        new InsertDataTask().execute(name,location,email,contactNum);
    }






    public void oncliskcaddphotos(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                // Convert the selected image into a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                // Convert the Bitmap into a byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // TODO: Use the byteArray as needed
                new addimagetobusiness().execute(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class addimagetobusiness extends AsyncTask<byte[], Void, Boolean> {

        // Declare EventID at the class level
        private byte[] imagebytearr;

        @Override
        protected Boolean doInBackground(byte[]... eventIDs) {
            imagebytearr = eventIDs[0];
            Connection connection = com.example.login.DatabaseConnection.getInstance().getConnection();
            String insertQuery = "INSERT INTO businessimage(businessID, image) VALUES(?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, UserID);
                preparedStatement.setBytes(2, imagebytearr);
                int rowsAffected = preparedStatement.executeUpdate();
                preparedStatement.close();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(EditBusinessProfile.this, "image added successfully", Toast.LENGTH_SHORT).show();
                setuprecyclers();
                new RetrieveBusinessTask().execute();
            } else {
                Toast.makeText(EditBusinessProfile.this, "Failed to add image", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private class InsertDataTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Connection dbconnect = com.example.login.DatabaseConnection.getInstance().getConnection();
            String name = params[0];
            String location = params[1];
            String email = params[2];
            String contactTest = params[3];

            String sql ="UPDATE Business SET Email = ?, BusinessName = ?, Location = ?, ContactNumber = ? WHERE BusinessID = ?";



            try {
                PreparedStatement preparedStatement = dbconnect.prepareStatement(sql);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4,contactTest);
                preparedStatement.setInt(5,UserID);


                preparedStatement.executeUpdate();

                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                copyToClipboard(e.getMessage());
                // Toast.makeText(CreateBusinessAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EditBusinessProfile.this, "Account Updated!", Toast.LENGTH_SHORT).show();
                setdata();
                Intent intent= new Intent(EditBusinessProfile.this,BusinessHomePage.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EditBusinessProfile.this, "Error updating account.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeNice() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        // Make the navigation bar immersive
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Change the status bar icons color to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }





    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error Message", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void setdata(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connections = DriverManager.getConnection("jdbc:mysql://156.155.64.210:3306/projectdb", "angus", "angus");

            statement = connections.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);

            String selectQuery = "SELECT * FROM Business WHERE BusinessID = '"+UserID+"'";

            ResultSet resultSet =statement.executeQuery(selectQuery);
            if (resultSet.next()) {
                String BusName = resultSet.getString("BusinessName");
                String contactnum = resultSet.getString("ContactNumber");
                String Email = resultSet.getString("Email");
                String location = resultSet.getString("Location");

                edtName.setText(BusName);
                edtContact.setText( contactnum);
                edtEmail.setText(Email);
                edtLocation.setText( location);
            } else {
                System.out.println("No user found with the specified UserID.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
