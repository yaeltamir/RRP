////
////import android.content.Context;
////import android.graphics.Bitmap;
////import android.graphics.Rect;
////import org.opencv.android.Utils;
////import org.opencv.core.CvType;
////import org.opencv.core.Mat;
////import org.opencv.core.Scalar;
////import org.opencv.core.Size;
////import org.opencv.imgproc.Imgproc;
////import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
////import com. google. mediapipe. tasks. vision. handlandmarker. HandLandmarker. HandLandmarkerOptions;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
////import com.google.mediapipe.tasks.core.BaseOptions;
////
////import java.util.ArrayList;
////import java.util.List;
////import android.graphics.Bitmap;
////import android.graphics.Rect;
////import org.opencv.android.Utils;
////import org.opencv.core.Mat;
////import org.opencv.core.Size;
////import org.opencv.imgproc.Imgproc;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
////import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;
////
////import java.util.ArrayList;
////import java.util.List;
////import android.graphics.Bitmap;
////import android.graphics.Rect;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
////import com.google.mediapipe.tasks.vision.core.MPImage;
////import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.UUID;
////import android.graphics.Bitmap;
////import org.opencv.android.Utils;
////import org.opencv.core.Core;
////import org.opencv.core.Mat;
////import org.opencv.core.Scalar;
////import org.opencv.core.Size;
////import org.opencv.imgproc.Imgproc;
////
////import com.google.mediapipe.framework.image.BitmapImageBuilder;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerOptions;
////import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
////
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.UUID;
////
////public class ImageProcessor {
////
////    public static HashMap<String, Object> processImage(Bitmap bitmap) {
////        // הכנת האובייקט לתוצאה
////        HashMap<String, Object> output = new HashMap<>();
////        List<int[]> bboxes = new ArrayList<>();
////        List<List<float[]>> handLandmarks = new ArrayList<>();
////
////        output.put("bboxes", bboxes);
////        output.put("hand_landmarks", handLandmarks);
////        output.put("united_bbox", null);
////        output.put("user_id", UUID.randomUUID().toString());
////
////        HashMap<String, Object> meta = new HashMap<>();
////        meta.put("age", null);
////        meta.put("gender", null);
////        meta.put("race", null);
////        output.put("meta", meta);
////
////        // הגדרת OpenCV לעיבוד תמונה
////        Mat imageFrame = new Mat();
////        Utils.bitmapToMat(bitmap, imageFrame);
////        Imgproc.cvtColor(imageFrame, imageFrame, Imgproc.COLOR_RGBA2RGB);
////
////        int width = imageFrame.width();
////        int height = imageFrame.height();
////
////        try {
////            // הגדרת HandLandmarker
////            HandLandmarkerOptions options = HandLandmarkerOptions.builder()
//////                    .setBaseOptions(HandLandmarkerOptions.builder())
////                    .setMinHandDetectionConfidence(0.5f)
////                    .setNumHands(2)
////                    .build();
////
////            HandLandmarker handLandmarker = HandLandmarker.createFromOptions(,options);
////            HandLandmarkerResult result = handLandmarker.detect(BitmapImageBuilder.buildFromMat(imageFrame));
////
////            if (result != null && !result.landmarks().isEmpty()) {
////                for (int i = 0; i < result.landmarks().size(); i++) {
////                    List<float[]> landmarks = new ArrayList<>();
////                    float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
////                    float xMax = Float.MIN_VALUE, yMax = Float.MIN_VALUE;
////
////                    for (float[] landmark : result.landmarks().get(i)) {
////                        float x = landmark[0] * width;
////                        float y = landmark[1] * height;
////
////                        landmarks.add(new float[]{x, y});
////                        xMin = Math.min(xMin, x);
////                        yMin = Math.min(yMin, y);
////                        xMax = Math.max(xMax, x);
////                        yMax = Math.max(yMax, y);
////                    }
////
////                    handLandmarks.add(landmarks);
////                    int[] bbox = new int[]{
////                            (int) xMin, (int) yMin,
////                            (int) (xMax - xMin), (int) (yMax - yMin)
////                    };
////                    bboxes.add(bbox);
////                }
////
////                // יצירת תיבת גבול מאוחדת אם יש שתי ידיים
////                if (bboxes.size() == 2) {
////                    int unitedXMin = Math.min(bboxes.get(0)[0], bboxes.get(1)[0]);
////                    int unitedYMin = Math.min(bboxes.get(0)[1], bboxes.get(1)[1]);
////                    int unitedXMax = Math.max(
////                            bboxes.get(0)[0] + bboxes.get(0)[2],
////                            bboxes.get(1)[0] + bboxes.get(1)[2]
////                    );
////                    int unitedYMax = Math.max(
////                            bboxes.get(0)[1] + bboxes.get(0)[3],
////                            bboxes.get(1)[1] + bboxes.get(1)[3]
////                    );
////
////                    output.put("united_bbox", new int[]{
////                            unitedXMin, unitedYMin,
////                            unitedXMax - unitedXMin, unitedYMax - unitedYMin
////                    });
////                }
////            }
////
////            handLandmarker.close();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        return output;
////    }
////}
//
//import android.content.Context;
//import android.graphics.Bitmap;
//
//import androidx.core.content.ContextCompat;
//
//import org.opencv.core.Mat;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//
//import com.google.mediapipe.framework.image.BitmapImageBuilder;
//import com.google.mediapipe.tasks.core.BaseOptions;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker.HandLandmarkerOptions;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
//import com.google.mediapipe.framework.image.MPImage;
//import android.graphics.Bitmap;
//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;
//
//import com.google.mediapipe.framework.image.MPImage;
//import com.google.mediapipe.framework.image.MPImageFactory;
//import com.google.mediapipe.tasks.core.BaseOptions;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerOptions;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
//
//public class MediaPipeProcessor {
//
//    private HandLandmarker handLandmarker;
//
//    // אתחול של MediaPipe HandLandmarker
//    public MediaPipeProcessor() {
//        try {
//            BaseOptions baseOptions = BaseOptions.builder()
//                    .setModelAssetPath("hand_landmarker.task") // ודא שהקובץ נמצא בתיקיית assets
//                    .build();
//
//            HandLandmarkerOptions options = HandLandmarkerOptions.builder()
//                    .setBaseOptions(baseOptions)
//                    .setMinHandDetectionConfidence(0.5f) // ערך ביטחון לזיהוי
//                    .setNumHands(2) // מספר הידיים שברצונך לזהות
//                    .build();
//
//            handLandmarker = HandLandmarker.createFromOptions(options);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // עיבוד תמונה
//    public HandLandmarkerResult processImage(Mat imageFrame) {
//        // שינוי צבע התמונה ל-RGB
//        Mat rgbImage = new Mat();
//        Imgproc.cvtColor(imageFrame, rgbImage, Imgproc.COLOR_BGR2RGB);
//
//        // המרת Mat ל-Bitmap
//        Bitmap bitmap = Bitmap.createBitmap(rgbImage.width(), rgbImage.height(), Bitmap.Config.ARGB_8888);
//        org.opencv.android.Utils.matToBitmap(rgbImage, bitmap);
//
//        // המרת Bitmap ל-MPImage
//        MPImage mpImage = MPImageFactory.createFromBitmap(bitmap);
//
//        // עיבוד התמונה באמצעות MediaPipe
//        HandLandmarkerResult results = null;
//        try {
//            results = handLandmarker.detect(mpImage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return results;
//    }
//
//    // סגירת HandLandmarker
//    public void close() {
//        if (handLandmarker != null) {
//            handLandmarker.close();
//        }
//    }
//}
