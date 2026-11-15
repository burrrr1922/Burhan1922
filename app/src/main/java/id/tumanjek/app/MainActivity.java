package id.tumanjek.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private GeolocationPermissions.Callback geoCallback;
    private String geoOrigin;
    private static final String APP_HOST = "tumanjek-kalkulator.hatchable.site";
    private static final int FILE_REQUEST = 2101;
    private static final int GEO_REQUEST = 2102;

    @SuppressLint("SetJavaScriptEnabled")
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(16,43,78));
        getWindow().setNavigationBarColor(android.graphics.Color.BLACK);
        webView = new WebView(this);
        setContentView(webView);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSafeBrowsingEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("https".equals(uri.getScheme()) && APP_HOST.equals(uri.getHost())) return false;
                if (APP_HOST.equals(uri.getHost())) {
                    Toast.makeText(MainActivity.this, "Koneksi aman diperlukan.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch (Exception ignored) {}
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                try { startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), FILE_REQUEST); return true; }
                catch (Exception e) { fileCallback = null; return false; }
            }
            @Override public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (!origin.startsWith("https://" + APP_HOST)) { callback.invoke(origin, false, false); return; }
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin, true, false);
                } else {
                    geoCallback = callback; geoOrigin = origin;
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GEO_REQUEST);
                }
            }
        });
        if (state == null) webView.loadUrl("https://" + APP_HOST + "/");
        else webView.restoreState(state);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST) {
            Uri[] result = resultCode == RESULT_OK && data != null && data.getData() != null ? new Uri[]{data.getData()} : null;
            if (fileCallback != null) fileCallback.onReceiveValue(result);
            fileCallback = null;
        }
    }
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == GEO_REQUEST) {
            boolean granted = false;
            for (int result : results) if (result == PackageManager.PERMISSION_GRANTED) granted = true;
            if (geoCallback != null) geoCallback.invoke(geoOrigin, granted, false);
            geoCallback = null; geoOrigin = null;
        }
    }
    @Override protected void onSaveInstanceState(Bundle out) { webView.saveState(out); super.onSaveInstanceState(out); }
    @Override public void onBackPressed() { if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
    @Override protected void onDestroy() {
        if (fileCallback != null) fileCallback.onReceiveValue(null);
        fileCallback = null; geoCallback = null;
        if (webView != null) { webView.stopLoading(); webView.destroy(); }
        super.onDestroy();
    }
}
