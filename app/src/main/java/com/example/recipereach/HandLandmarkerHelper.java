//package com.example.recipereach;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Matrix;
//import android.media.MediaMetadataRetriever;
//import android.net.Uri;
//import android.os.SystemClock;
//import android.util.Log;
//
//import androidx.annotation.VisibleForTesting;
//import androidx.camera.core.ImageProxy;
//
//import com.google.mediapipe.framework.image.BitmapImageBuilder;
//import com.google.mediapipe.framework.image.MPImage;
//import com.google.mediapipe.tasks.core.BaseOptions;
//import com.google.mediapipe.tasks.core.Delegate;
//import com.google.mediapipe.tasks.vision.core.RunningMode;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
//import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
//
//import java.io.IOException;
//
//public class HandLandmarkerHelper {
//
//    private static final String TAG = "HandLandmarkerHelper";
//    private static final String MP_HAND_LANDMARKER_TASK = "hand_landmarker.task";
//
//    private static final int DELEGATE_CPU = 0;
//    private static final int DELEGATE_GPU = 1;
//    private static final float DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F;
//    private static final float DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F;
//    private static final float DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F;
//    private static final int DEFAULT_NUM_HANDS = 1;
//    private static final int OTHER_ERROR = 0;
//    private static final int GPU_ERROR = 1;
//
//    private HandLandmarker handLandmarker;
//    private float minHandDetectionConfidence = DEFAULT_HAND_DETECTION_CONFIDENCE;
//    private float minHandTrackingConfidence = DEFAULT_HAND_TRACKING_CONFIDENCE;
//    private float minHandPresenceConfidence = DEFAULT_HAND_PRESENCE_CONFIDENCE;
//    private int maxNumHands = DEFAULT_NUM_HANDS;
//    private int currentDelegate = DELEGATE_CPU;
//    private RunningMode runningMode = RunningMode.IMAGE;
//    private Context context;
//    private LandmarkerListener handLandmarkerHelperListener;
//
//    public HandLandmarkerHelper(Context context, LandmarkerListener handLandmarkerHelperListener) {
//        this.context = context;
//        this.handLandmarkerHelperListener = handLandmarkerHelperListener;
//        setupHandLandmarker();
//    }
//
//    public void clearHandLandmarker() {
//        if (handLandmarker != null) {
//            handLandmarker.close();
//            handLandmarker = null;
//        }
//    }
//
//    public boolean isClose() {
//        return handLandmarker == null;
//    }
//
//    public void setupHandLandmarker() {
//        BaseOptions.Builder baseOptionBuilder = BaseOptions.builder();
//
//        switch (currentDelegate) {
//            case DELEGATE_CPU:
//                baseOptionBuilder.setDelegate(Delegate.CPU);
//                break;
//            case DELEGATE_GPU:
//                baseOptionBuilder.setDelegate(Delegate.GPU);
//                break;
//        }
//
//        baseOptionBuilder.setModelAssetPath(MP_HAND_LANDMARKER_TASK);
//
//        if (runningMode == RunningMode.LIVE_STREAM && handLandmarkerHelperListener == null) {
//            throw new IllegalStateException(
//                    "handLandmarkerHelperListener must be set when runningMode is LIVE_STREAM.");
//        }
//
//        try {
//            BaseOptions baseOptions = baseOptionBuilder.build();
//            HandLandmarker.HandLandmarkerOptions.Builder optionsBuilder =
//                    HandLandmarker.HandLandmarkerOptions.builder()
//                            .setBaseOptions(baseOptions)
//                            .setMinHandDetectionConfidence(minHandDetectionConfidence)
//                            .setMinTrackingConfidence(minHandTrackingConfidence)
//                            .setMinHandPresenceConfidence(minHandPresenceConfidence)
//                            .setNumHands(maxNumHands)
//                            .setRunningMode(runningMode);
//
//            if (runningMode == RunningMode.LIVE_STREAM) {
//                optionsBuilder
//                        .setResultListener(this::returnLivestreamResult)
//                        .setErrorListener(this::returnLivestreamError);
//            }
//
//            HandLandmarker.HandLandmarkerOptions options = optionsBuilder.build();
//            handLandmarker = HandLandmarker.createFromOptions(context, options);
//        } catch (IllegalStateException e) {
//            if (handLandmarkerHelperListener != null) {
//                handLandmarkerHelperListener.onError("Hand Landmarker failed to initialize. See error logs for details");
//            }
//            Log.e(TAG, "MediaPipe failed to load the task with error: " + e.getMessage());
//        } catch (RuntimeException e) {
//            if (handLandmarkerHelperListener != null) {
//                handLandmarkerHelperListener.onError("Hand Landmarker failed to initialize. See error logs for details", GPU_ERROR);
//            }
//            Log.e(TAG, "Image classifier failed to load model with error: " + e.getMessage());
//        }
//    }
//
//    private void returnLivestreamError(RuntimeException e) {
//        returnLivestreamError(e.hashCode(),e.getMessage());
//    }
//
//    public void detectLiveStream(ImageProxy imageProxy, boolean isFrontCamera) {
//        if (runningMode != RunningMode.LIVE_STREAM) {
//            throw new IllegalArgumentException("Attempting to call detectLiveStream while not using RunningMode.LIVE_STREAM");
//        }
//
//        long frameTime = SystemClock.uptimeMillis();
//
//        Bitmap bitmapBuffer = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
//        try {
//            bitmapBuffer.copyPixelsFromBuffer(imageProxy.getPlanes()[0].getBuffer());
//        } finally {
//            imageProxy.close();
//        }
//
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
//
//        if (isFrontCamera) {
//            matrix.postScale(-1f, 1f, imageProxy.getWidth(), imageProxy.getHeight());
//        }
//
//        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), matrix, true);
//
//        MPImage mpImage = new BitmapImageBuilder(rotatedBitmap).build();
//        detectAsync(mpImage, frameTime);
//    }
//
//    @VisibleForTesting
//    public void detectAsync(MPImage mpImage, long frameTime) {
//        if (handLandmarker != null) {
//            handLandmarker.detectAsync(mpImage, frameTime);
//        }
//    }
//
//    public ResultBundle detectVideoFile(Uri videoUri, long inferenceIntervalMs) {
//        if (runningMode != RunningMode.VIDEO) {
//            throw new IllegalArgumentException("Attempting to call detectVideoFile while not using RunningMode.VIDEO");
//        }
//
//        long startTime = SystemClock.uptimeMillis();
//        boolean didErrorOccurred = false;
//
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(context, videoUri);
//
//        String videoLengthMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        Bitmap firstFrame = retriever.getFrameAtTime(0);
//        Integer width = firstFrame != null ? firstFrame.getWidth() : null;
//        Integer height = firstFrame != null ? firstFrame.getHeight() : null;
//
//        if (videoLengthMs == null || width == null || height == null) return null;
//
//        long videoLength = Long.parseLong(videoLengthMs);
//        int numberOfFrameToRead = (int) (videoLength / inferenceIntervalMs);
//
//        ResultBundle resultBundle = new ResultBundle();
//        for (int i = 0; i <= numberOfFrameToRead; i++) {
//            long timestampMs = i * inferenceIntervalMs;
//
//            Bitmap frame = retriever.getFrameAtTime(timestampMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
//            if (frame != null) {
//                if (frame.getConfig() != Bitmap.Config.ARGB_8888) {
//                    frame = frame.copy(Bitmap.Config.ARGB_8888, false);
//                }
//
//                MPImage mpImageFrame = new BitmapImageBuilder(frame).build();
//                HandLandmarkerResult detectionResult = handLandmarker.detectForVideo(mpImageFrame, timestampMs);
//                if (detectionResult != null) {
//                    resultBundle.getResults().add(detectionResult);
//                } else {
//                    didErrorOccurred = true;
//                    if (handLandmarkerHelperListener != null) {
//                        handLandmarkerHelperListener.onError("ResultBundle could not be returned in detectVideoFile");
//                    }
//                }
//            } else {
//                didErrorOccurred = true;
//                if (handLandmarkerHelperListener != null) {
//                    handLandmarkerHelperListener.onError("Frame at specified time could not be retrieved when detecting in video.");
//                }
//            }
//        }
//
//        try {
//            retriever.release();
//        } catch (IOException e) {
//            Log.e(TAG, "Failed to set data source", e);
//            return null;
//        }
//
//        long inferenceTimePerFrameMs = (SystemClock.uptimeMillis() - startTime) / numberOfFrameToRead;
//
//        return didErrorOccurred ? null : resultBundle;
//    }
//
//    public ResultBundle detectImage(Bitmap image) {
//        if (runningMode != RunningMode.IMAGE) {
//            throw new IllegalArgumentException("Attempting to call detectImage while not using RunningMode.IMAGE");
//        }
//
//        long startTime = SystemClock.uptimeMillis();
//        MPImage mpImage = new BitmapImageBuilder(image).build();
//
//        HandLandmarkerResult landmarkResult = handLandmarker.detect(mpImage);
//        if (landmarkResult != null) {
//            long inferenceTimeMs = SystemClock.uptimeMillis() - startTime;
//            return new ResultBundle(landmarkResult, inferenceTimeMs, image.getHeight(), image.getWidth());
//        }
//
//        if (handLandmarkerHelperListener != null) {
//            handLandmarkerHelperListener.onError("Hand Landmarker failed to detect.");
//        }
//        return null;
//    }
//
//    private void returnLivestreamResult(HandLandmarkerResult result, MPImage input) {
//        long finishTimeMs = SystemClock.uptimeMillis();
//        long inferenceTime = finishTimeMs - result.timestampMs();
//
//        if (handLandmarkerHelperListener != null) {
//            handLandmarkerHelperListener.onResults(new ResultBundle(result, inferenceTime, input.getHeight(), input.getWidth()));
//        }
//    }
//
//    private void returnLivestreamError(int errorCode, String message) {
//        if (handLandmarkerHelperListener != null) {
//            handLandmarkerHelperListener.onError(message, errorCode);
//        }
//    }
//
//    public interface LandmarkerListener {
//        void onError(String errorMessage);
//        void onError(String errorMessage, int errorCode);
//        void onResults(ResultBundle result);
//    }
//}
