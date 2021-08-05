package pan.bo.yu.textfirebasestorage;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
private Button b1,b2,b3,b4;
private ImageView v1,v2;
private int PICK_CONT_REQUEST=1;
private Uri uri;
private String data_list;
   TextView textView;
StorageReference storageReference,pic_storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 =findViewById(R.id.button3);
        b4 =findViewById(R.id.button4);
        v1 = findViewById(R.id.imageView1);
        v2 = findViewById(R.id.imageView2);

        //連線到FirebaseStorage
      storageReference =FirebaseStorage.getInstance().getReference();

      //按鈕一  打開手機內部照片 會用到 onActivityResult
     b1.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent =new Intent();
             intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
             intent.setType("image/*");
             intent.setAction(intent.ACTION_GET_CONTENT);
             startActivityForResult(intent,1);

         }
     });
    //按鈕二 上傳檔案
     b2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pic_storage = storageReference.child("m4."+data_list);   //m4.是上傳檔案名稱
             pic_storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     textView.setText("上傳成功!!");
                 }
             });

         }
     });
    //按鈕三 讀取檔案
     b3.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pic_storage = storageReference.child("m4."+data_list);
             final File file;
             try {
                 file = File.createTempFile("image","png");
                 pic_storage.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                         v2.setImageURI(Uri.fromFile(file));
                         textView.setText("讀取成功");
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         textView.setText("讀取失敗");
                     }
                 });
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     });
    //按鈕四 刪除檔案
     b4.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pic_storage = storageReference.child("m4."+data_list);
             pic_storage.delete();
             textView.setText("刪除成功");
             v1.setImageURI(null);
             v2.setImageURI(null);
         }
     });


}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //以下IF 打開內部檔案後的動作
        if(requestCode==PICK_CONT_REQUEST){
            uri =data.getData();
            v1.setImageURI(uri);
            //下三行 讀取檔案的副檔名  傳給data_list
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
            data_list=mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}