package com.example.shehab.socialnetwork;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shehab.socialnetwork.utils.BitmapUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity2 extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {

    private static final String TAG = MainActivity2.class.getSimpleName();

    public static final String IMAGE_NAME = "dog.jpg";

    public static final int SELECT_GALLERY_IMAGE = 101;
    private StorageReference postImagesReference;

    private String savecurrentdate,savecurrenttime,PostRamdomNme,downloadUrl,current_user_id;
    private DatabaseReference postRef,userRef;
    private long countpost=0;
    private FirebaseAuth mAuth;
    private String discription;
    private Uri ImageUri;

    @BindView(R.id.image_preview)
    ImageView imagePreview;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        postImagesReference= FirebaseStorage.getInstance().getReference();
        postRef= FirebaseDatabase.getInstance().getReference().child("posts");
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_title_main));

        loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if (id == R.id.action_save) {
            saveImageToGallery();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            // clear bitmap memory
            originalImage.recycle();
            finalImage.recycle();
            finalImage.recycle();

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage);
            bitmap.recycle();

            Toast.makeText(this, ""+originalImage, Toast.LENGTH_SHORT).show();
            // render selected image thumbnails
            filtersListFragment.prepareThumbnail(originalImage);

            ImageUri = data.getData();
        }
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, SELECT_GALLERY_IMAGE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /*
    * saves image to camera gallery
    * */
    private void saveImageToGallery() {


        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {


                            final String path = MediaStore.Images.Media.insertImage(getBaseContext().getContentResolver(), finalImage,"_profile.jpg", null);



                            if (!TextUtils.isEmpty(path)) {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                        .setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                openImage(path);

                                            }
                                        });

                                snackbar.show();



                                Toast.makeText(getApplicationContext(), ""+path, Toast.LENGTH_SHORT).show();


                                Calendar callForDate=Calendar.getInstance();
                                SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                                savecurrentdate=currentdate.format(callForDate.getTime());


                                Calendar callForTime=Calendar.getInstance();
                                SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
                                savecurrenttime=currentTime.format(callForTime.getTime());

                                PostRamdomNme=savecurrentdate+savecurrenttime;

                                StorageReference filepath=postImagesReference.child("post image").child(Uri.parse(path).getPathSegments() +PostRamdomNme + ".jpg");
                                filepath.putFile(Uri.parse(path)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                                    {

                                        if(task.isSuccessful())
                                        {
                                            downloadUrl=task.getResult().getDownloadUrl().toString();
                                            Toast.makeText(MainActivity2.this, "the photo Uploaded successfully ...", Toast.LENGTH_SHORT).show();
                                            saveInformationtoDatabase();



                                            postRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.exists()) {
                                                        countpost = dataSnapshot.getChildrenCount();
                                                    } else {
                                                        countpost = 0;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    String userFullname = dataSnapshot.child("fullName").getValue().toString();
                                                    String userprofilimage = dataSnapshot.child("profilimage").getValue().toString();
                                                    HashMap postMap = new HashMap();
                                                    postMap.put("uid", current_user_id);
                                                    postMap.put("date", savecurrentdate);
                                                    postMap.put("time", savecurrenttime);
                                                    postMap.put("uid", current_user_id);
                                                    postMap.put("postimage", downloadUrl);
                                                    postMap.put("postprofileimag", userprofilimage);
                                                    postMap.put("fullname", userFullname);
                                                    postMap.put("counter", countpost);

                                                    postRef.child(current_user_id + PostRamdomNme).updateChildren(postMap)
                                                            .addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful()) {
                                                                        sendUserTomainActivity();
                                                                        Toast.makeText(MainActivity2.this, "post update successfully... ", Toast.LENGTH_SHORT).show();


                                                                        Intent topostdicriptionIntent=new Intent(getApplicationContext(),DicriptionPosts.class);
                                                                        topostdicriptionIntent.putExtra("idposts",current_user_id+PostRamdomNme);
                                                                        topostdicriptionIntent.putExtra("postimage",downloadUrl);
                                                                        startActivity(topostdicriptionIntent);


                                                                    } else {

                                                                        Toast.makeText(MainActivity2.this, "error occurred..", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });



                                        }
                                        else
                                        {
                                            String message=task.getException().getMessage();
                                            Toast.makeText(MainActivity2.this, "Error occurred"+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });




                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Unable to save image!", Snackbar.LENGTH_LONG);

                                snackbar.show();




                                Calendar callForDate=Calendar.getInstance();
                                SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                                savecurrentdate=currentdate.format(callForDate.getTime());


                                Calendar callForTime=Calendar.getInstance();
                                SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
                                savecurrenttime=currentTime.format(callForTime.getTime());

                                PostRamdomNme=savecurrentdate+savecurrenttime;

                                StorageReference filepath=postImagesReference.child("post image").child(ImageUri.getPathSegments() +PostRamdomNme + ".jpg");
                                filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                                    {

                                        if(task.isSuccessful())
                                        {
                                            downloadUrl=task.getResult().getDownloadUrl().toString();

                                            Toast.makeText(MainActivity2.this, "the photo Uploaded successfully ...", Toast.LENGTH_SHORT).show();
                                            saveInformationtoDatabase();

                                        }
                                        else
                                        {
                                            String message=task.getException().getMessage();
                                            Toast.makeText(MainActivity2.this, "Error occurred"+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
















     }










    private void saveInformationtoDatabase() {


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    countpost = dataSnapshot.getChildrenCount();
                } else {
                    countpost = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userFullname = dataSnapshot.child("fullName").getValue().toString();
                String userprofilimage = dataSnapshot.child("profilimage").getValue().toString();
                HashMap postMap = new HashMap();
                postMap.put("uid", current_user_id);
                postMap.put("date", savecurrentdate);
                postMap.put("time", savecurrenttime);
                postMap.put("uid", current_user_id);
                postMap.put("postimage", downloadUrl);
                postMap.put("postprofileimag", userprofilimage);
                postMap.put("fullname", userFullname);
                postMap.put("counter", countpost);

                postRef.child(current_user_id + PostRamdomNme).updateChildren(postMap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Intent topostdicriptionIntent=new Intent(getApplicationContext(),DicriptionPosts.class);
                                    topostdicriptionIntent.putExtra("idposts",current_user_id+PostRamdomNme);
                                    topostdicriptionIntent.putExtra("postimage",downloadUrl);
                                    startActivity(topostdicriptionIntent);

                                    Toast.makeText(MainActivity2.this, "post update successfully... ", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(MainActivity2.this, "error occurred..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendUserTomainActivity() {
        Intent mainActivityintent=new Intent(getApplicationContext(),MainActivity.class);
        mainActivityintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityintent);
        finish();
    }
        // opening image in default image viewer app
        private void openImage (String path){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(path), "image/*");
            startActivity(intent);
        }
    }

