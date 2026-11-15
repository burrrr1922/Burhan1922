# TUMANJEK Android

Aplikasi Android TUMANJEK menggunakan Android WebView yang membuka layanan resmi:
https://tumanjek-kalkulator.hatchable.site

Aplikasi membatasi navigasi internal ke domain HTTPS resmi TUMANJEK. Tautan eksternal dibuka di browser. JavaScript dan penyimpanan lokal diperlukan untuk fungsi aplikasi. Izin lokasi hanya diminta saat fitur lokasi dipakai; pemilih gambar dipakai Admin saat mengganti logo atau wallpaper.

## Build APK

GitHub Actions membangun APK debug yang dapat dipasang langsung:
1. Buka tab Actions.
2. Pilih workflow **Build TUMANJEK Android APK**.
3. Unduh artifact **TUMANJEK-Android-APK** setelah status berhasil.
4. Ekstrak ZIP dan pasang `app-debug.apk` pada Android.

Package ID: `id.tumanjek.app`
Minimum Android: 6.0 (API 23)
Target Android: 15 (API 35)

APK debug ditandatangani otomatis dengan debug keystore GitHub runner. Untuk distribusi Play Store atau rilis resmi berkelanjutan, gunakan signing key rilis pribadi dan simpan hanya di GitHub Secrets, bukan di repositori.
