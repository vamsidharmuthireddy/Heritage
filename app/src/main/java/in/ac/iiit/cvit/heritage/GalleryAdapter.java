package in.ac.iiit.cvit.heritage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by HOME on 16-03-2017.
 */

public class GalleryAdapter extends BaseAdapter {

    private Activity _activity;
    private Context context;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    private int imageWidth;

    private static final String LOGTAG = "GalleryAdapter";

    public GalleryAdapter(Context _context, Activity activity, ArrayList<String> filePaths, int imageWidth) {
        this.context = _context;
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
    }

    @Override
    public int getCount() {
        return this._filePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);
        } else {
            imageView = (ImageView) convertView;
        }

        // get screen dimensions
        
        imageDownload id = new imageDownload();
        id.execute(_filePaths.get(position),imageWidth,imageWidth);
        
        /*Bitmap image = decodeFile(_filePaths.get(position), imageWidth, imageWidth);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
        imageView.setImageBitmap(image);
        */
        // image view click listener
        //imageView.setOnClickListener(new OnImageClickListener(position));

        final int _position = position;
        final ArrayList<String> ff = new ArrayList<String>(_filePaths);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(_activity, FullScreenImageActivity.class);
                i.putExtra("position", _position);

                Log.v(LOGTAG, "clicked image is " + ff.get(_position));
                _activity.startActivity(i);
            }
        });


        return imageView;
    }

    /*
    class OnImageClickListener implements OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, FullScreenViewActivity.class);
            i.putExtra("position", _postion);
            _activity.startActivity(i);
        }

    }
*/
    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    class imageDownload extends Asynctask<String,Void,Bitmap>{
        
         @Override
        protected Bitmap doInBackground(String... params) {
        
            return decodeFile(params[0],Integer.parseInt(params[1]),Integer.parseInt(params[2]));
        
        }
        
        @Override
        protected void onPostExecute(Bitmap aVoid) {
            super.onPostExecute(aVoid);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
            imageView.setImageBitmap(aVoid);
        }
    }

}
