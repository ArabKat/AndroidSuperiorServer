package com.superiorshoes.ooguro.androidsuperiorserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.superiorshoes.ooguro.androidsuperiorserver.Common.Common;
import com.superiorshoes.ooguro.androidsuperiorserver.Interface.ItemClickListener;
import com.superiorshoes.ooguro.androidsuperiorserver.Model.Category;
import com.superiorshoes.ooguro.androidsuperiorserver.Model.Cloth;
import com.superiorshoes.ooguro.androidsuperiorserver.ViewHolder.ClothViewHolder;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class ClothList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;

    FloatingActionButton fab;

    // Firebase
    FirebaseDatabase db;
    DatabaseReference clothList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";

    FirebaseRecyclerAdapter<Cloth,ClothViewHolder> adapter;

    // Add New Cloth
    MaterialEditText ssName,ssDescription,ssPrice,ssDiscount;
    FButton btnSelect,btnUpload;

    Cloth newCloth;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_list);

        // Firebase
        db= FirebaseDatabase.getInstance();
        clothList = db.getReference("Cloths");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.recycler_cloth);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddClothDialog();
            }
        });

        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty())
            loadListCloth(categoryId);
    }

    private void showAddClothDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClothList.this);
        alertDialog.setTitle("Add new Cloth");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_cloth_layout,null);

        ssName = add_menu_layout.findViewById(R.id.ssName);
        ssDescription = add_menu_layout.findViewById(R.id.ssDescription);
        ssPrice = add_menu_layout.findViewById(R.id.ssPrice);
        ssDiscount = add_menu_layout.findViewById(R.id.ssDiscount);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        // Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();  // Lets user select image from Gallery and save Uri of this image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        // Set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                //Here, the new category is created
                if(newCloth != null)
                {
                    clothList.push().setValue(newCloth);
                    Snackbar.make(rootLayout,"New category " + newCloth.getName()+ " was added",Snackbar.LENGTH_SHORT)
                            .show();
                }

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        alertDialog.show();
    }

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(ClothList.this, "Uploaded!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Set value for newCategory if image upload and we can get download link
                                    newCloth = new Cloth();
                                    newCloth.setName(ssName.getText().toString());
                                    newCloth.setDescription(ssDescription.getText().toString());
                                    newCloth.setPrice(ssPrice.getText().toString());
                                    newCloth.setDiscount(ssDiscount.getText().toString());
                                    newCloth.setMenuId(categoryId);
                                    newCloth.setImage(uri.toString());



                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ClothList.this, "", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                                    taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);

    }


    private void loadListCloth(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Cloth, ClothViewHolder>(
                Cloth.class,
                R.layout.cloth_item,
                ClothViewHolder.class,
                clothList.orderByChild("menuId").equalTo(categoryId)

        ) {
            @Override
            protected void populateViewHolder(ClothViewHolder viewHolder, Cloth model, int position) {
                viewHolder.cloth_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.cloth_image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Code late
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data !=null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateClothDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteCloth(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCloth(String key) {
        clothList.child(key).removeValue();
    }

    private void showUpdateClothDialog(final String key, final Cloth item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClothList.this);
        alertDialog.setTitle("Edit Cloth");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_cloth_layout,null);

        ssName = add_menu_layout.findViewById(R.id.ssName);
        ssDescription = add_menu_layout.findViewById(R.id.ssDescription);
        ssPrice = add_menu_layout.findViewById(R.id.ssPrice);
        ssDiscount = add_menu_layout.findViewById(R.id.ssDiscount);

        // Set default value for view
        ssName.setText(item.getName());
        ssDescription.setText(item.getDescription());
        ssPrice.setText(item.getPrice());
        ssDiscount.setText(item.getDiscount());


        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        // Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();  // Lets user select image from Gallery and save Uri of this image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        // Set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                //Here, the new category is created

                    // Update information
                    item.setName(ssName.getText().toString());
                    item.setPrice(ssPrice.getText().toString());
                    item.setDiscount(ssDiscount.getText().toString());
                    item.setDescription(ssDescription.getText().toString());

                    clothList.child(key).setValue(item);

                    clothList.push().setValue(newCloth);
                    Snackbar.make(rootLayout,"Category " + item.getName()+ " was edited!",Snackbar.LENGTH_SHORT)
                            .show();

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        alertDialog.show();
    }

    private void changeImage(final Cloth item) {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(ClothList.this, "Uploaded!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Set value for newCategory if image upload and we can get download link
                                    item.setImage(uri.toString());


                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ClothList.this, "", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                                    taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }

    }


}
