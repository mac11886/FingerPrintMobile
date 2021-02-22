//import android.annotation.SuppressLint;
//import android.app.ProgressDialog;
//import android.net.Uri;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class BrowserScreen extends AppCompatActivity {
//
//    ActivityWebviewBinding binding;
//
//    public static final int INPUT_FILE_REQUEST_CODE = 1;
//    private ProgressDialog progressDialog;
//    private ValueCallback<Uri[]> mFilePathCallback;
//    private String mCameraPhotoPath;
//
//    @SuppressLint("SetJavaScriptEnabled")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
//
//        setSupportActionBar(binding.toolbar);
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(getString(R.string.button_register_now));
//        }
//
//        progressDialog = new ProgressDialog(WebViewActivity.this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
//
//        WebSettings webSettings = binding.webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//
//        binding.webView.setWebViewClient(new WebViewClient() {
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                final Uri uri = Uri.parse(url);
//                return handleUri(uri);
//            }
//
//            @TargetApi(Build.VERSION_CODES.N)
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                final Uri uri = request.getUrl();
//                return handleUri(uri);
//            }
//
//            private boolean handleUri(final Uri uri) {
//
//                if (uri != null) {
//
//                    Log.i("TAG", "Uri =" + uri);
//
//                    // Based on some condition you need to determine if you are going to load the url
//                    // in your web view itself or in a browser.
//                    // You can use `host` or `scheme` or any part of the `uri` to decide.
//                    if (uri.getLastPathSegment() != null) {
//                        if (uri.getLastPathSegment().contains("p1_business_part1.php")) {
//                            finish();
//                            startActivity(new Intent(WebViewActivity.this, LoginActivity.class));
//                            return false;
//                        }
//                    } else {
//                        binding.webView.loadUrl(uri.toString());
//                        return true;
//                    }
//                }
//
//                return false;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(WebViewActivity.this, "Error:" + description, Toast.LENGTH_SHORT).show();
//                view.loadUrl("about:blank");
//            }
//        });
//        binding.webView.setWebChromeClient(new WebChromeClient() {
//
//            public boolean onShowFileChooser(
//                    WebView webView, ValueCallback<Uri[]> filePathCallback,
//                    WebChromeClient.FileChooserParams fileChooserParams) {
//                if (mFilePathCallback != null) {
//                    mFilePathCallback.onReceiveValue(null);
//                }
//                mFilePathCallback = filePathCallback;
//
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    // Create the File where the photo should go
//                    File photoFile = null;
//                    try {
//                        photoFile = createImageFile();
//                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
//                    } catch (IOException ex) {
//                        // Error occurred while creating the File
//                        Log.e("TAG", "Unable to create Image File", ex);
//                    }
//
//                    // Continue only if the File was successfully created
//                    if (photoFile != null) {
//                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                Uri.fromFile(photoFile));
//                    } else {
//                        takePictureIntent = null;
//                    }
//                }
//
//                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                contentSelectionIntent.setType("image/*");
//
//                Intent[] intentArray;
//                if (takePictureIntent != null) {
//                    intentArray = new Intent[]{takePictureIntent};
//                } else {
//                    intentArray = new Intent[0];
//                }
//
//                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//
//                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
//
//                return true;
//            }
//        });
//
//        binding.webView.loadUrl("your_url");
//    }
//
//    /**
//     * More info this method can be found at
//     * http://developer.android.com/training/camera/photobasics.html
//     *
//     * @return
//     * @throws IOException
//     */
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        return File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
//            super.onActivityResult(requestCode, resultCode, data);
//            return;
//        }
//
//        Uri[] results = null;
//
//        // Check that the response is a good one
//        if (resultCode == Activity.RESULT_OK) {
//            if (data == null) {
//                // If there is not data, then we may have taken a photo
//                if (mCameraPhotoPath != null) {
//                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
//                }
//            } else {
//                String dataString = data.getDataString();
//                if (dataString != null) {
//                    results = new Uri[]{Uri.parse(dataString)};
//                }
//            }
//        }
//
//        mFilePathCallback.onReceiveValue(results);
//        mFilePathCallback = null;
//    }
//
//    @Override
//    public void onBackPressed() {
//        // TODO Auto-generated method stub
//
//        if (binding.webView.canGoBack()) {
//            binding.webView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        finish();
//        return super.onOptionsItemSelected(item);
//    }
//}