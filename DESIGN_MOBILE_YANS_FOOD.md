# SPESIFIKASI LENGKAP DESAIN UI, UX & ANIMASI
# YAN'S FOOD MOBILE ADMIN COMPANION (ANDROID KOTLIN)

---

| Atribut | Detail Spesifikasi |
| :--- | :--- |
| **Nama Aplikasi** | Yan's Food Mobile Admin Companion |
| **Platform & Bahasa** | Android Native (Kotlin 1.9+, Min SDK 24, Target SDK 34) |
| **Arsitektur Antarmuka** | Native Android XML Layout + Material Design 3 + ViewBinding |
| **Prinsip Utama** | **Bebas AI Slop**: Fungsional, Membumi, Ergonomis Satu Tangan, 60 FPS Mulus |
| **Target Perangkat** | Smartphone Android (RAM 3GB - 8GB, Layar 6.0" - 6.7" rasio 19:9 hingga 20:9) |

---

## 1. Filosofi Desain (Anti-AI Slop Manifesto)

Aplikasi ini dirancang khusus untuk operasional nyata bisnis kuliner UMKM Indonesia:
1. **Tidak Ada Efek Gimmick / Berat:** Menolak pemakaian *glassmorphism* buram bertingkat, gradien neon RGB, atau ilustrasi 3D melayang tak berguna yang menghabiskan memori RAM dan memperlambat waktu respon di HP Android.
2. **Harmonisasi Brand Yan's Food:** Mempertahankan palet warna asli gerai (Terakota `#C1440E`, Amber Gold `#D6A253`, Latar *Warm Off-White* `#F8F6F2`, Teks Arang `#1C1917`).
3. **Ergonomi Ibu Jari (*Thumb Zone First*):** 80% aksi penting (navigasi tab, tombol filter, switch stok, dan tombol simpan) diletakkan di bagian bawah layar agar dapat dioperasikan dengan mudah saat pemilik memegang HP dengan satu tangan di pasar atau di dapur.
4. **Sinkronisasi CRUD Multi-Platform Nyata:** Setiap aksi *Create, Read, Update, Delete* di HP Android terhubung ke satu database terpusat via REST API Laravel, sehingga perubahan langsung tampak seketika di Web Publik (`/menu`) dan Web Kasir Gerai (`/admin/pos`).

### 1.1 Diagram & Skenario Integrasi CRUD Lintas Platform
```
           ┌──────────────────────────────────────────────┐
           │        DATABASE TERPUSAT VPS BIZNET GIO      │
           │             (MySQL: yans_food)               │
           └──────────────────────┬───────────────────────┘
                                  │
    ┌─────────────────────────────┼─────────────────────────────┐
    ▼                             ▼                             ▼
┌─────────────────────────┐ ┌─────────────────────────┐ ┌─────────────────────────┐
│       WEB PUBLIK        │ │     WEB ADMIN KASIR     │ │    APLIKASI ANDROID     │
│   (Katalog Pelanggan)   │ │  (Terminal POS Gerai)   │ │   (Ponsel Pintar Owner) │
│  https://yansfoods...   │ │  https://yansfoods...   │ │    Retrofit REST API    │
└─────────────────────────┘ └─────────────────────────┘ └─────────────────────────┘
```

* **CREATE (Tambah Menu di HP):** Owner input foto & harga menu baru di HP ➔ Seketika muncul di katalog Web Publik & tombol POS kasir gerai.
* **UPDATE (Ubah Status/Stok di HP):** Switch stok dimatikan di HP ➔ Seketika Web Publik menampilkan badge *"Habis"* dan kasir web terkunci tidak bisa menjual produk tersebut.
* **DELETE (Hapus Biaya di HP):** Catatan pengeluaran dihapus di HP ➔ Seketika laporan keuangan di Web Admin ter-update otomatis.
* **READ (Feed Penjualan Live):** Kasir gerai menyelesaikan transaksi di web kasir ➔ Omset dan struk di HP Owner seketika bertambah secara *real-time*.

---

## 2. Sistem Desain UI & Token Resosrce XML

### 2.1 Palet Warna Resmi (`res/values/colors.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Brand Primary Colors (Terakota Hangat Yan's Food) -->
    <color name="brand_primary">#C1440E</color>
    <color name="brand_primary_variant">#8F3209</color>
    <color name="brand_primary_light">#FFF1EB</color>

    <!-- Brand Secondary (Amber / Emas Kuliner) -->
    <color name="brand_accent">#D6A253</color>
    <color name="brand_accent_light">#FEF8EC</color>

    <!-- Surfaces & Backgrounds (Adem di Mata, Tidak Silau) -->
    <color name="bg_screen">#F8F6F2</color>          <!-- Warm Off-White -->
    <color name="bg_card">#FFFFFF</color>            <!-- Kartu Putih Bersih -->
    <color name="bg_subtle">#F1ECE4</color>          <!-- Abu-abu Krem untuk Shimmer & Divider -->

    <!-- Text Hierarchy (Tingkat Keterbacaan Tinggi) -->
    <color name="text_primary">#1C1917</color>       <!-- Stone 900 (Judul & Angka) -->
    <color name="text_secondary">#78716C</color>     <!-- Stone 500 (Deskripsi & Subtitle) -->
    <color name="text_tertiary">#A8A29E</color>      <!-- Stone 400 (Waktu & Keterangan Tipis) -->
    <color name="text_on_primary">#FFFFFF</color>

    <!-- Status Indicators (Feedback Fungsional) -->
    <color name="status_success">#059669</color>      <!-- Hijau Tersedia / Laba Surplus -->
    <color name="status_success_bg">#ECFDF5</color>
    <color name="status_warning">#D97706</color>      <!-- Kuning Peringatan Stok Menipis -->
    <color name="status_warning_bg">#FFFBEB</color>
    <color name="status_danger">#E11D48</color>       <!-- Merah Stok Habis / Pengeluaran Biaya -->
    <color name="status_danger_bg">#FFF1F2</color>

    <!-- Border & Dividers -->
    <color name="border_soft">#E7E1D8</color>
    <color name="border_input">#D4CDC5</color>
    <color name="ripple_dark">#20C1440E</color>
</resources>
```

### 2.2 Dimensi, Margin & Corner Radius (`res/values/dimens.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Spacing Standard Grid 4dp/8dp -->
    <dimen name="space_4">4dp</dimen>
    <dimen name="space_8">8dp</dimen>
    <dimen name="space_12">12dp</dimen>
    <dimen name="space_16">16dp</dimen>
    <dimen name="space_20">20dp</dimen>
    <dimen name="space_24">24dp</dimen>
    <dimen name="space_32">32dp</dimen>

    <!-- Radius Kartu & Komponen -->
    <dimen name="radius_pill">100dp</dimen>
    <dimen name="radius_chip">8dp</dimen>
    <dimen name="radius_card">16dp</dimen>
    <dimen name="radius_button">12dp</dimen>
    <dimen name="radius_thumbnail">12dp</dimen>

    <!-- Skala Tipografi (SP) -->
    <dimen name="text_xs">11sp</dimen>
    <dimen name="text_sm">12sp</dimen>
    <dimen name="text_base">14sp</dimen>
    <dimen name="text_lg">16sp</dimen>
    <dimen name="text_xl">18sp</dimen>
    <dimen name="text_2xl">22sp</dimen>
    <dimen name="text_display">26sp</dimen>

    <!-- Elevasi Shadow Hemat Baterai & GPU -->
    <dimen name="elevation_flat">0dp</dimen>
    <dimen name="elevation_card">1dp</dimen>
    <dimen name="elevation_floating">4dp</dimen>
</resources>
```

---

## 3. Spesifikasi UX & Alur Interaksi Pengguna (User Experience)

### 3.1 Ergonomi Wilayah Jangkauan Ibu Jari (*Thumb Zone*)
```
┌─────────────────────────────────┐
│ [ AREA SULIT DIJANGKAU ]        │ -> Hanya untuk Logo & Judul Layar
│ Logo Toko & Status Bar          │
├─────────────────────────────────┤
│ [ AREA SEDANG DIJANGKAU ]       │ -> Teks ringkasan & Grafik visual
│ Kartu KPI & Status Toko         │
├─────────────────────────────────┤
│ [ AREA SANGAT NYAMAN (60%) ]    │ -> Interaksi utama pengguna
│ Daftar Menu, Pencarian, Chips,  │
│ Sakelar Stok, Tombol FAB (+)    │
│ & Navigasi Bawah (Bottom Bar)   │
└─────────────────────────────────┘
```

### 3.2 Alur UX 1: Live Monitoring & Gestur Refresh
1. Pengguna membuka aplikasi ➔ Otomatis masuk ke **Dashboard** tanpa jeda login berkat token sesi lokal.
2. Kartu angka KPI Omset dan Laba langsung beranimasi bergulir (*number count-up*).
3. Pengguna menarik layar dari atas ke bawah (*Swipe to Refresh*):
   * Ikon putar berputar dengan warna `@color/brand_primary`.
   * Saat data selesai diperbarui, ponsel memberikan **getaran haptik lembut (*Light Vibration - 15ms*)** sebagai konfirmasi bahwa data sudah paling mutakhir.

### 3.3 Alur UX 2: Kontrol Stok Cepat (*Quick Stock Control*)
1. Pemilik usaha membuka tab **Menu**.
2. Menggeser *MaterialSwitch* pada kartu "Dimsum Ayam Jamur" dari **Tersedia** ke **Habis**.
3. **Instan & Tanpa Reload:** Switch langsung berubah posisi seketika, badge berubah merah (*Stok Habis*).
4. Muncul bar notifikasi melayang di bawah (*Snackbar*) berdurasi 3 detik:
   * Teks: *"Status Dimsum Ayam Jamur diubah menjadi Habis."*
   * Tombol Aksi: **BATALKAN (UNDO)** untuk memulihkan jika salah pencet.

### 3.4 Alur UX 3: Catat Pengeluaran & Kamera Bukti Nota
1. Pemilik di pasar membuka tab **Pengeluaran** ➔ Menekan tombol bundar melayang **FAB (+)** di pojok kanan bawah.
2. Membuka form `ActivityAddExpense`. Pengguna mengetik *"Daging Ayam Fillet 5 Kg"*, memilih kategori *"Bahan Baku"*, dan mengetik nominal `175000`.
3. Menekan tombol kamera *"Ambil Foto Nota"*:
   * Membuka kamera bawaan HP tanpa jeda.
   * Menjepret nota fisik belanja ➔ Foto otomatis dikompresi ke format JPEG maksimal 1280px (< 400 KB).
   * Muncul preview foto kecil di form dengan tombol silang (x) jika ingin foto ulang.
4. Menekan tombol *"Simpan Pengeluaran"*:
   * Tombol bertransformasi menampilkan indikator loading melingkar (*ProgressBar*).
   * Selesai dalam < 1.5 detik, kembali ke list pengeluaran dengan suara/haptik sukses.

### 3.5 Penanganan Kondisi Khusus (Edge Cases & Offline)
* **Koneksi Internet Lemah / Terputus:**
  * Banner merah tipis muncul di bawah Action Bar: *"Koneksi terputus. Menampilkan data tersimpan terakhir."*
  * Pengguna tetap dapat melihat katalog produk dan riwayat transaksi yang tersimpan di cache lokal (tidak ada layar putih kosong).
* **Kondisi Data Masih Kosong (*Empty State*):**
  * Ditampilkan ikon vektor lembut dengan teks membumi:
    * *Dashboard:* "Belum ada transaksi kasir tercatat hari ini."
    * *Pengeluaran:* "Belum ada catatan pengeluaran belanja dapur."

---

## 4. Desain Animasi Native Android (Motion & Micro-Interactions)

Semua animasi menerapkan standar **Material Motion**: berdurasi pendek (150ms – 300ms), tidak menghambat aktivitas pengguna (*non-blocking*), dan menggunakan interpolator akselerasi natural (`FastOutSlowInInterpolator`).

### 4.1 Tabel Ringkasan Animasi Aplikasi

| No | Nama Animasi | Titik Kejadian (Trigger) | Jenis Animasi & Durasi | Tujuan UX |
| :---: | :--- | :--- | :--- | :--- |
| **1** | **Number Count-Up** | Saat kartu KPI Dashboard pertama kali muncul | `ValueAnimator` angka bergulir (0 ➔ Nilai Riil), durasi 600ms | Memberikan kesan aplikasi hidup & real-time |
| **2** | **Button Bounce Scale** | Saat tombol utama / kartu ditekan pengguna | Skala mengecil ke `0.96` lalu membal ke `1.0` (durasi 120ms) | Umpan balik sentuhan fisik yang memuaskan |
| **3** | **Staggered List Slide-Up** | Saat daftar menu atau pengeluaran dimuat | `layoutAnimation` geser ke atas 16dp + fade-in (delay 40ms/item) | Transisi data terasa mengalir dan tidak kaku |
| **4** | **FAB Expand & Morph** | Saat tombol (+) ditekan untuk tambah data | Rotasi ikon 45° + transisi ekspansi form (250ms) | Menjelaskan keterikatan aksi dengan form |
| **5** | **Skeleton Shimmer** | Saat data sedang diambil dari server Laravel | Sapuan gradien abu-abu krem lembut (kecepatan 1000ms loop) | Mengurangi persepsi waktu tunggu pengguna |
| **6** | **Page Fade-Through** | Saat berganti tab pada Bottom Navigation | Transisi fade out 90ms & fade in 150ms antar fragment | Perpindahan layar mulus tanpa jeda kedip layar |

---

### 4.2 Kode & Implementasi Animasi Nyata

#### A. Animasi Sentuhan Membal (*Button Bounce Scale*) di Kotlin:
```kotlin
fun View.applyButtonBounceEffect() {
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
            }
        }
        false
    }
}
```

#### B. Animasi Angka Bergulir (*Number Count-Up*) untuk Omset & Laba:
```kotlin
fun TextView.animateCurrencyCountUp(targetAmount: Double, durationMs: Long = 600L) {
    val animator = ValueAnimator.ofFloat(0f, targetAmount.toFloat())
    animator.duration = durationMs
    animator.interpolator = FastOutSlowInInterpolator()
    animator.addUpdateListener { animation ->
        val animatedValue = (animation.animatedValue as Float).toDouble()
        val formatted = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(animatedValue)
            .replace("Rp", "Rp ")
            .replace(",00", "")
        this.text = formatted
    }
    animator.start()
}
```

#### C. Animasi Muncul Bertahap pada List (`res/anim/item_animation_slide_up.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="250"
    android:interpolator="@android:anim/decelerate_interpolator">
    <translate
        android:fromYDelta="20%p"
        android:toYDelta="0" />
    <alpha
        android:fromAlpha="0.0"
        android:toAlpha="1.0" />
</set>
```
Diterapkan pada `RecyclerView` menggunakan `res/anim/layout_animation_staggered.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<layoutAnimation xmlns:android="http://schemas.android.com/apk/res/android"
    android:animation="@anim/item_animation_slide_up"
    android:animationOrder="normal"
    android:delay="12%" />
```

---

## 5. Rincian Desain Antarmuka Tiap Layar (Wireframe XML)

### 5.1 Layar 1: Dashboard Eksekutif (`fragment_dashboard.xml`)

```
┌────────────────────────────────────────────────────────┐
│ [Logo Icon] YAN'S FOOD               [ Halo, Owner ]  │
├────────────────────────────────────────────────────────┤
│ RINGKASAN HARI INI                                     │
│ ┌───────────────────────────┐ ┌──────────────────────┐ │
│ │ 🍔 OMSET KASIR POS        │ │ 🧾 TRANSAKSI SELESAI │ │
│ │ Rp 450.000 (Count-Up)     │ │ 18 Struk Selesai     │ │
│ └───────────────────────────┘ └──────────────────────┘ │
│ ┌───────────────────────────┐ ┌──────────────────────┐ │
│ │ 💸 PENGELUARAN DAPUR      │ │ 💰 LABA BERSIH HARI  │ │
│ │ Rp 120.000                │ │ Rp 330.000 (Surplus) │ │
│ └───────────────────────────┘ └──────────────────────┘ │
│                                                        │
│ ⚠️ PERINGATAN STOK MENIPIS                             │
│ ┌────────────────────────────────────────────────────┐ │
│ │ [!] Dimsum Ayam Jamur tersisa 4 porsi (Restock)    │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ FEED TRANSAKSI KASIR TERAKHIR                          │
│ ┌────────────────────────────────────────────────────┐ │
│ │ TR-20261026-0042  • 19:45 WIB  •  CASH             │ │
│ │ Rp 35.000 (2x Dimsum Ayam, 1x Es Teh)              │ │
│ └────────────────────────────────────────────────────┘ │
│ ┌────────────────────────────────────────────────────┐ │
│ │ TR-20261026-0041  • 19:12 WIB  •  QRIS             │ │
│ │ Rp 50.000 (4x Dimsum Mix Party)                    │ │
│ └────────────────────────────────────────────────────┘ │
├────────────────────────────────────────────────────────┤
│  [📊 Beranda]     [🍗 Menu]     [💸 Biaya]    [📈 Lap]  │
└────────────────────────────────────────────────────────┘
```

### 5.2 Layar 2: Manajemen Menu & Stok Cepat (`fragment_menu.xml`)

```
┌────────────────────────────────────────────────────────┐
│ KATALOG MENU & KONTROL STOK                            │
│ ┌────────────────────────────────────────────────────┐ │
│ │ 🔍 Cari nama makanan atau minuman...               │ │
│ └────────────────────────────────────────────────────┘ │
│ (Semua)  [Dimsum]  [Minuman]  [Dessert]                │
│                                                        │
│ ┌────────────────────────────────────────────────────┐ │
│ │ [FOTO 76dp] Dimsum Ayam Original                   │ │
│ │             Kategori: Dimsum                       │ │
│ │             Rp 15.000  •  [ 25 porsi ]             │ │
│ │             Status: [ Switch: TERSEDIA (Hijau) ]   │ │
│ └────────────────────────────────────────────────────┘ │
│ ┌────────────────────────────────────────────────────┐ │
│ │ [FOTO 76dp] Es Kul-Kul Pisang Coklat               │ │
│ │             Kategori: Dessert                      │ │
│ │             Rp 5.000   •  [ 3 porsi - TIPIS ]      │ │
│ │             Status: [ Switch: TERSEDIA (Hijau) ]   │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│                                              (+) FAB   │
├────────────────────────────────────────────────────────┤
│  [📊 Beranda]     [🍗 Menu]     [💸 Biaya]    [📈 Lap]  │
└────────────────────────────────────────────────────────┘
```

### 5.3 Layar 3: Catat Pengeluaran & Kamera Bukti (`activity_add_expense.xml`)

```
┌────────────────────────────────────────────────────────┐
│ [←] CATAT PENGELUARAN BELANJA DAPUR                    │
├────────────────────────────────────────────────────────┤
│ Nama Pengeluaran                                       │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Belanja Daging Ayam Fillet 5 Kg                    │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ Kategori Biaya                                         │
│ ┌────────────────────────────────────────────────────┐ │
│ │ [V] Bahan Baku                                     │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ Nominal Uang (Rp)                                      │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Rp 175.000                                         │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ Metode Pembayaran                                      │
│ (*) TUNAI (CASH)           ( ) TRANSFER BANK           │
│                                                        │
│ Bukti Nota Fisik Belanja (Kamera HP)                   │
│ ┌────────────────────────────────────────────────────┐ │
│ │ [ 📷 ] Ambil Foto Nota Belanja Langsung            │ │
│ │ [Thumbnail preview foto yang baru dijepret]        │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ ┌────────────────────────────────────────────────────┐ │
│ │         [ SIMPAN CATATAN PENGELUARAN ]             │ │
│ └────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

---

## 6. Panduan Implementasi XML Material 3 untuk Siswa

1. **Gunakan MaterialCardView Modern:**
   Hindari `CardView` lama dari support library. Gunakan `com.google.android.material.card.MaterialCardView` yang memiliki atribut `app:strokeColor` dan `app:strokeWidth="1dp"` untuk border yang elegan dan tajam di layar AMOLED maupun IPS.
2. **Gunakan MaterialButton:**
   Gunakan `com.google.android.material.button.MaterialButton` dengan `app:cornerRadius="12dp"`. Hindari tag `<Button>` standar lama.
3. **Optimasi Gambar dengan Coil:**
   Tampilkan gambar produk menggunakan library `Coil`:
   ```kotlin
   imageView.load(product.imageUrl) {
       crossfade(true)
       placeholder(R.drawable.placeholder_food)
       error(R.drawable.placeholder_food)
       transformations(RoundedCornersTransformation(12f))
   }
   ```
4. **Hindari Overdraw Hierarchy:**
   Batasi penumpukan layout maksimal 3 level menggunakan `ConstraintLayout` agar rendering frame tetap 60 FPS di semua perangkat Android.

---

## 8. Panduan Prompt & Spesifikasi Modul Login & Logout (Siap Copas)

Bagian ini berisi prompt instruksi mandiri terstandarisasi yang siap disalin (*copas*) untuk memulai eksekusi koding modul Login & Logout:

```markdown
Tolong buatkan implementasi lengkap Modul Autentikasi (Login & Logout) untuk aplikasi Android Native (Kotlin) "Yan's Food Mobile Admin Companion" menggunakan XML Layout + Material Design 3 + ViewBinding + MVVM + Retrofit.

Spesifikasi & Kebutuhan Implementasi:

1. DESAIN UI & TAMPILAN XML (activity_login.xml):
   - Warna Brand: Primary Terakota (#C1440E), Latar Belakang Warm Off-White (#F8F6F2), Kartu Putih (#FFFFFF).
   - Header: Logo icon Yan's Food (rounded card), Judul "YAN'S FOOD ADMIN", Subtitle "Masuk ke Panel Pengawasan Owner".
   - Input Field (Material TextInputLayout):
     * Email (InputType textEmailAddress, placeholder: admin@yansfood.id, ikon mail).
     * Password (InputType textPassword, toggle password visibility aktif, placeholder: ••••••••, ikon lock).
   - Tombol Masuk (MaterialButton):
     * Teks "Masuk ke Dashboard", warna Terakota (#C1440E), corner radius 12dp, efek sentuh membal (bounce scale).
   - Indikator Loading: ProgressBar melingkar halus saat tombol ditekan (tombol menjadi disable untuk mencegah double request).
   - Anti-AI Slop: Tampilan bersih, membumi, rapi, dan mudah dioperasikan.

2. LOGIKA & ALUR INTEGRASI API (Retrofit 2 + Coroutines):
   - Endpoint: POST https://yansfoods.my.id/api/login
   - Request Body: email & password.
   - Response Sukses: Mengembalikan API Token (Bearer), status success, dan objek user (id, name, email, role).
   - Response Gagal: Menampilkan pesan error bersahabat via Snackbar merah di atas layar (misal: "Email atau kata sandi salah!").

3. MANAJEMEN SESI LOKAL (SessionManager):
   - Simpan API Token, User Name, dan Email ke SharedPreferences / DataStore.
   - Auto-Login: Saat aplikasi dibuka pertama kali (Splash/Main), jika token sudah tersimpan, aplikasi otomatis langsung melompat ke MainActivity (Dashboard) tanpa memunculkan layar login lagi.

4. FITUR LOGOUT:
   - Tombol "Keluar dari Aplikasi" di tab Laporan/Profil.
   - Menampilkan MaterialAlertDialog konfirmasi: "Apakah Anda yakin ingin keluar dari akun Admin?".
   - Jika "Ya", hapus token sesi lokal dan arahkan kembali pengguna ke LoginActivity dengan menghapus backstack (FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK).
```
