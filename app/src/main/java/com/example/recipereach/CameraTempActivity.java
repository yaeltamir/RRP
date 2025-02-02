package com.example.recipereach;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.recipereach.activities.GuideActivity;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker.HandLandmarkerOptions;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;

import com.example.recipereach.GesturePredictor.labelNames;

public class CameraTempActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private HandLandmarker handLandmarker;
    private OverlayView overlayView;
    private Button startButton, endButton;
    private ScrollView scrollView;
    private TextView recipe;
    private ImageButton homeButton, editButton, btnOpenGuide,deleteBtn; //, deleteButton;
    private boolean initialized = false;
    private ProcessCameraProvider cameraProvider;
    private GesturePredictor gesturePredictor;
    private String username,recipeName,recipeIngredients ,recipeInstructions,recipeNotes;
    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_temp);

        // Retrieve recipe details from the intent
        recipeName = getIntent().getStringExtra("RECIPE_NAME");
        recipeIngredients = getIntent().getStringExtra("RECIPE_INGREDIENTS");
        recipeInstructions = getIntent().getStringExtra("RECIPE_INSTRUCTIONS");
        recipeNotes = getIntent().getStringExtra("RECIPE_NOTES");
        String recipeId = getIntent().getStringExtra("RECIPE_ID");
        username = getIntent().getStringExtra("USERNAME");

        // Initialize UI components
        previewView = findViewById(R.id.view_finder);
        cameraExecutor = Executors.newSingleThreadExecutor();
        overlayView = findViewById(R.id.overlayView);
        startButton = findViewById(R.id.startButton);
        endButton = findViewById(R.id.endButton);
        editButton = findViewById(R.id.editButton);
        homeButton = findViewById(R.id.home_button);
        scrollView = findViewById(R.id.scrollView);
        recipe = findViewById(R.id.receipeText);
        btnOpenGuide = findViewById(R.id.guideButton);
        deleteBtn = findViewById(R.id.deleteButton);

        setFullRecipe();

        // Log retrieved values for debugging
        Log.i("Recipe", recipeName == null ? "No name" : recipeName);
        Log.i("Username", username == null ? "No name" : username);
        Log.i("Recipe", recipeId == null ? "No ID" : recipeId);

        // Initialize gesture predictor model
        try {
            gesturePredictor = new GesturePredictor(getApplicationContext(), "gesture_model.tflite");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Start gesture recognition when clicking start button
        startButton.setOnClickListener(v -> {
            // Disable buttons before starting
            homeButton.setEnabled(false);
            homeButton.setAlpha(0.25f);
            btnOpenGuide.setEnabled(false);
            btnOpenGuide.setAlpha(0.25f);
            editButton.setEnabled(false);
            editButton.setAlpha(0.25f);
            deleteBtn.setEnabled(false);
            deleteBtn.setAlpha(0.25f);
            startButton.setEnabled(false);
            startButton.setAlpha(0.25f);

            // Check camera permission
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraTempActivity.this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                if (!initialized) setHandLandmarker();
                startCamera();
            }
        });

        // Stop gesture recognition when clicking end button
        endButton.setOnClickListener(v -> stopCamera());

        // Open the edit recipe activity
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(CameraTempActivity.this, EditRecipeActivity.class);
            intent.putExtra("RECIPE_ID", recipeId);
            intent.putExtra("RECIPE_NAME", recipeName);
            intent.putExtra("INGREDIENTS", recipeIngredients);
            intent.putExtra("INSTRUCTIONS", recipeInstructions);
            intent.putExtra("NOTES", recipeNotes);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        // Confirm and delete recipe
        deleteBtn.setOnClickListener(v -> new AlertDialog.Builder(CameraTempActivity.this)
                .setTitle("אישור מחיקה")
                .setMessage("האם אתה בטוח שברצונך למחוק את המתכון?")
                .setPositiveButton("אישור", (dialog, which) -> {
                    deleteRecipe(recipeId);
                    Toast.makeText(CameraTempActivity.this, "המתכון נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CameraTempActivity.this, HomeViewActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                })
                .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                .show());

        // Open the guide activity
        btnOpenGuide.setOnClickListener(v -> {
            Intent intent = new Intent(CameraTempActivity.this, GuideActivity.class);
            intent.putExtra("RECIPE_ID", recipeId);
            intent.putExtra("RECIPE_NAME", recipeName);
            intent.putExtra("INGREDIENTS", recipeIngredients);
            intent.putExtra("INSTRUCTIONS", recipeInstructions);
            intent.putExtra("NOTES", recipeNotes);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        // Return to the home screen
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CameraTempActivity.this, HomeViewActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }

    // Formats and sets the full recipe text with styling for titles and content.
    private void setFullRecipe() {

        // Create a SpannableStringBuilder for the formatted recipe
        SpannableStringBuilder designedRecipe = new SpannableStringBuilder();

        // Format the recipe name (Main title: bold, underlined, large size, and centered)
        SpannableString recipeTitle = new SpannableString(recipeName + "\n\n");
        recipeTitle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, recipeName.length(), 0);
        recipeTitle.setSpan(new UnderlineSpan(), 0, recipeName.length(), 0);
        recipeTitle.setSpan(new RelativeSizeSpan(1.5f), 0, recipeName.length(), 0);
        designedRecipe.append(recipeTitle);

        // Format the "Ingredients:" title
        SpannableString ingredientsTitle = new SpannableString("מרכיבים:\n");
        ingredientsTitle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ingredientsTitle.length(), 0);
        ingredientsTitle.setSpan(new RelativeSizeSpan(1.2f), 0, ingredientsTitle.length(), 0);
        designedRecipe.append(ingredientsTitle);

        // Add the ingredients (regular text)
        designedRecipe.append(recipeIngredients + "\n\n");

        // Format the "Instructions:" title
        SpannableString instructionsTitle = new SpannableString("אופן ההכנה:\n");
        instructionsTitle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, instructionsTitle.length(), 0);
        instructionsTitle.setSpan(new RelativeSizeSpan(1.2f), 0, instructionsTitle.length(), 0);
        designedRecipe.append(instructionsTitle);

        // Add the instructions (regular text)
        designedRecipe.append(recipeInstructions + "\n\n");

        // Add additional notes if available
        if (recipeNotes != null && !recipeNotes.isEmpty()) {
            SpannableString notesTitle = new SpannableString("הערות נוספות:\n");
            notesTitle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, notesTitle.length(), 0);
            notesTitle.setSpan(new RelativeSizeSpan(1.2f), 0, notesTitle.length(), 0);
            designedRecipe.append(notesTitle);

            // Add the notes (regular text)
            designedRecipe.append(recipeNotes);
        }

        // Set the formatted text in the TextView
        recipe.setText(designedRecipe);
        Log.i("fullRecipe", designedRecipe.toString());

        // If needed, center the entire text
        // recipe.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    // Handles the result of a permission request, specifically for camera access.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request code matches the camera permission request
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Initialize the HandLandmarker and start the process
            setHandLandmarker();
            startButton.performClick();
        } else {
            Log.e("Permission", "Camera permission denied.");
        }
    }

    // Initializes and configures the HandLandmarker for hand tracking.
    public void setHandLandmarker() {
        try {
            // Configure the HandLandmarker options
            HandLandmarkerOptions options = HandLandmarkerOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setModelAssetPath("hand_landmarker.task")
                            .build())
                    .setNumHands(1)
                    .setRunningMode(RunningMode.IMAGE)
                    .build();

            // Create and initialize the HandLandmarker instance
            handLandmarker = HandLandmarker.createFromOptions(this, options);
            initialized = true;
            Log.i("HandLandmarker", "finish setting handLandmarker");
        } catch (Exception e) {
            Log.e("MediaPipe", "Error initializing HandLandmarker", e);
        }
    }

    // Initializes and starts the camera with a front-facing lens and image analysis.
    private void startCamera() {
        Log.i("Camera", "start camera");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Get the camera provider instance
                cameraProvider = cameraProviderFuture.get();

                // Select the front-facing camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

                // Set up the camera preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Configure image analysis with backpressure strategy
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

                // Set analyzer to process frames
                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                // Bind the camera to the lifecycle
                Camera camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

                // Update UI visibility
                previewView.setVisibility(View.VISIBLE);


            } catch (Exception e) {
                Log.e("Camera", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Stops the camera, unbinds all use cases, and resets UI elements.
    private void stopCamera() {
        if (cameraProvider != null) {
            // Unbind all camera use cases and reset provider
            cameraProvider.unbindAll();
            cameraProvider = null;

            // Update UI visibility
            previewView.setVisibility(View.INVISIBLE);


            // Enable all buttons
            homeButton.setEnabled(true);
            homeButton.setAlpha(1f);
            btnOpenGuide.setEnabled(true);
            btnOpenGuide.setAlpha(1f);
            editButton.setEnabled(true);
            editButton.setAlpha(1f);
            deleteBtn.setEnabled(true);
            deleteBtn.setAlpha(1f);
            startButton.setEnabled(true);
            startButton.setAlpha(1f);

            Log.i("Camera", "Camera stopped");
        }
        overlayView.clear();
    }

    // Analyzes an image frame, detects hand landmarks, and processes the results.
    private void analyzeImage(@NonNull ImageProxy image) {
        try {
            Log.i("Analyze", "start analyzing");

            MPImage mpImage = createMPImage(image);
            if (handLandmarker == null) {
                Log.e("Analyze", "handLandmarker is not initialized");
                return;
            }

            // Perform hand landmarks detection
            ImageProcessingOptions imgProcessingOpts = ImageProcessingOptions.builder().build();
            HandLandmarkerResult result = handLandmarker.detect(mpImage, imgProcessingOpts);

            // Process detected landmarks
            printLandmarks(result.landmarks());
            visualizeOnScreen(result.landmarks());
            predictGesture(result.landmarks());

        } catch (Exception e) {
            Log.e("Analyze", "Error analyzing image", e);
        } finally {
            // Release resources
            image.close();
        }
    }

    // Predicts the gesture based on detected hand landmarks and triggers an action.
    private void predictGesture(List<List<NormalizedLandmark>> handLandmarks) {
        if (handLandmarks.isEmpty())
            return;

        // Convert landmarks to float array for processing
        List<float[]> landmarks = normalizedLandmarktoFloatArray(handLandmarks.get(0));

        // Predict gesture using the model
        labelNames prediction = gesturePredictor.predictGesture(landmarks);
        String result = String.valueOf(prediction);

        Log.i("predict", "prediction: " + result);

        // Perform corresponding action based on prediction
        perfomAction(prediction);
    }

    // Perform actions based on detected hand gesture
    private void perfomAction(labelNames prediction) {
        // Executing actions based on prediction, updating UI accordingly
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Fetching the current text size in SP (scale-independent pixels)
                float textSize = recipe.getTextSize(); // Getting text size in SP
                Log.i("predict", "text size  " + textSize);

                // Converting a fixed value from SP to PX (pixels)
                float valueInSp = 18; // The value in SP
                float valueInPx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, // Desired unit (SP)
                        valueInSp,                 // Value in SP
                        getResources().getDisplayMetrics() // Device's display metrics
                );
                Log.i("predict", "sp=18 in px= " + valueInPx);

                // Switch based on the predicted label (gesture)
                float textSizeToSp = -1;
                switch (prediction) {
                    case PALM:
                        // Increasing the text size if it is below 150 SP
                        if (recipe.getTextSize() < 150) {
                            // Getting current text size in SP and increasing it
                            float currentSize = recipe.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
                            recipe.setTextSize(currentSize + 1);
                            Log.i("predict", "text size after palm: " + recipe.getTextSize());
                        }
                        break;
                    case GRIP:
                        // Decreasing the text size if it is above 47 SP
                        if (recipe.getTextSize() > 47) {
                            // Getting current text size in SP and decreasing it
                            float currentSize = recipe.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
                            recipe.setTextSize(currentSize - 1);
                            Log.i("predict", "text size after grip: " + recipe.getTextSize());
                        }
                        break;
                    case LIKE:
                        // Scrolling the view upwards to reveal more content
                        scrollView.smoothScrollBy(0, -recipe.getLineHeight());
                        break;
                    case POINT:
                        // Stopping the camera (specific behavior for this gesture)
                        stopCamera();
                        overlayView.clear();
                        break;
                    case DISLIKE:
                        // Scrolling the view downwards to reveal more content
                        scrollView.smoothScrollBy(0, recipe.getLineHeight());
                        break;
                    default:
                        // Default case where no action is taken
                        break;
                }
            }
        });
    }

    // Convert MediaPipe landmarks to float arrays
    private List<float[]> normalizedLandmarktoFloatArray(List<NormalizedLandmark> handLandmarks){
        List<float[]> convertedHandLandmarks = new ArrayList<>();

        // Converting each landmark to an array of floats representing x and y coordinates
        for (NormalizedLandmark handLandmark : handLandmarks) {
            float[] landmark = {handLandmark.x(), handLandmark.y()}; // Storing x and y coordinates of each landmark
            convertedHandLandmarks.add(landmark); // Adding converted landmark to the list
        }
        return convertedHandLandmarks; // Returning the list of landmarks as float arrays
    }

    // Print hand landmarks to the log
    private void printLandmarks(List<List<NormalizedLandmark>> handLandmarks) {
        if (handLandmarks.isEmpty())
            return;
        int i = 0;
        for (NormalizedLandmark landmark : handLandmarks.get(0)) {
            Log.d("MediaPipe", "Landmark " + i + ":(" + landmark.x() + ", " + landmark.y() + ")");
            i++;
        }
    }

    // Function to visualize hand landmarks on the screen
    private void visualizeOnScreen(List<List<NormalizedLandmark>> handLandmarks){
        // Check if handLandmarks list is empty, if so, clear the overlay view
        if(handLandmarks.isEmpty()) {
            runOnUiThread(() -> overlayView.clear());
            return;
        }

        List<PointF> points = new ArrayList<>();

        // Iterate through the landmarks to convert normalized coordinates to screen coordinates
        for (NormalizedLandmark landmark : handLandmarks.get(0)) {
            // Retrieve normalized x and y coordinates for the landmark
            float xBn = landmark.x();
            float yBn = landmark.y();

            // Get the location of previewView on screen (used for positioning the points correctly)
            int[] loc = new int[2];
            previewView.getLocationOnScreen(loc);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) previewView.getLayoutParams();

            // Calculate screen coordinates by scaling the normalized coordinates with previewView size
            float x = xBn * params.width;
            float y = yBn * params.height + loc[1];

            // Log the calculated screen coordinates and layout information for debugging purposes
            Log.i("LOC", "calculated= (" + x + "," + y + ")");
            Log.i("LOC", "pv loc= (" + loc[0] + "," + loc[1] + ")");
            Log.i("LOC", "pv lay= (w:" + params.width + ",h:" + params.height + ")");
            Log.i("LOC", "pv size= (w:" + previewView.getWidth() + ",h:" + previewView.getHeight() + ")");

            // Add the point to the list of points
            points.add(new PointF(x, y));
        }

        // Update the overlay view with the list of points on the UI thread
        runOnUiThread(() -> overlayView.setPoints(points));
    }

    // Function to create an MPImage object from an ImageProxy (rotating and flipping the bitmap)
    private MPImage createMPImage(ImageProxy imageProxy) {
        // Convert the ImageProxy to a Bitmap
        Bitmap bitmap = convertImageProxyToBitmap(imageProxy);

        // Create a matrix to rotate and flip the bitmap (based on the rotation degree from the image)
        Matrix matrix = new Matrix();
        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
        matrix.postScale(-1f, 1f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);

        // Create a new rotated bitmap using the transformation matrix
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        // Return a new MPImage built from the rotated bitmap
        return new BitmapImageBuilder(rotatedBitmap).build();
    }

    // Function to convert ImageProxy to Bitmap (from YUV format to ARGB_8888 format)
    private Bitmap convertImageProxyToBitmap(ImageProxy imageProxy) {
        // Retrieve the planes (Y, U, V) from the ImageProxy
        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();

        // Check if the planes array contains the required 3 planes (Y, U, V)
        if (planes.length < 3) {
            throw new IllegalStateException("ImageProxy does not contain all required planes.");
        }

        // Extract the Y, U, V planes' byte buffers
        ByteBuffer yBuffer = planes[0].getBuffer(); // Y
        ByteBuffer uBuffer = planes[1].getBuffer(); // U
        ByteBuffer vBuffer = planes[2].getBuffer(); // V

        // Get the size of each plane's buffer
        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        // Combine the Y, U, V data into a single byte array (NV21 format)
        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // Convert the NV21 data to a YuvImage, then compress it into a JPEG byte array
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, imageProxy.getWidth(), imageProxy.getHeight()), 100, outputStream);
        byte[] jpegBytes = outputStream.toByteArray();

        // Decode the JPEG byte array into a Bitmap
        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
    }

    // Override the onDestroy method to clean up resources (e.g., shut down the camera executor)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    // Function to delete a recipe document from Firestore by its recipeId
    public void deleteRecipe(String recipeId){
        // Get an instance of FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete the recipe document from the "Recipes" collection using the recipeId
        db.collection("Recipes")
                .document(recipeId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document deleted successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document", e));
    }
}


