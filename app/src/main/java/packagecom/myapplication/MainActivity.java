package packagecom.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle; import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface; import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils; import org.opencv.core.Core; import org.opencv.core.CvType;
import org.opencv.core.Mat; import org.opencv.core.MatOfPoint; import org.opencv.core.Point;
import org.opencv.core.Rect; import org.opencv.core.Scalar; import org.opencv.imgproc.Imgproc;

import java.util.ArrayList; import java.util.List;

import static packagecom.myapplication.R.drawable.outputimage;


public class MainActivity extends AppCompatActivity {
    Mat firstMat, secondMat, outputImageMat, outputMat = new Mat();
    Bitmap firstBitmap, secondBitmap;
    ImageView firstImage, secondImage, outputImage;
    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.v("OpenCV", "OpenCV loaded successfully");
                }
                break;
                default: {
                    Log.v("OpenCV", "OpenCV loaded fail");
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstImage = (ImageView) findViewById(R.id.imageinput1);
        secondImage = (ImageView) findViewById(R.id.imageinput2);
        outputImage = (ImageView) findViewById(R.id.imageoutput);


        firstBitmap = drawableToBitmap(R.drawable.wptest);
        secondBitmap = drawableToBitmap(R.drawable.wp2);

        firstImage.setImageBitmap(firstBitmap);
        secondImage.setImageBitmap(secondBitmap);
        outputImage.setImageResource(R.drawable.outputimage);

        firstMat = setBitmapToMat(firstBitmap);
        secondMat = setBitmapToMat(secondBitmap);
        outputImageMat = new Mat(secondMat.size(), CvType.CV_8UC1);

      Core.compare(firstMat, secondMat, outputImageMat, Core.CMP_LE);

       


        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.cvtColor(outputImageMat, outputMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.findContours(outputMat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            Imgproc.rectangle(outputImageMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width,
                    rect.y + rect.height), new Scalar(0, 0, 255), 2);
        }
        outputImage.setImageBitmap(setOutputMatToBitmap(outputImageMat));

    }
    Bitmap drawableToBitmap(int drawable) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        return BitmapFactory.decodeResource(getResources(), drawable, opt);
    }

    Bitmap setOutputMatToBitmap(Mat mat) {
        Bitmap scale = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
        Utils.matToBitmap(mat, scale); return scale; }
        Mat setBitmapToMat (Bitmap bitmap){
            Mat mat = new Mat();

            Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bmp32, mat);
            return mat;
        }


}

