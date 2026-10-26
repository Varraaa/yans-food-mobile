# PRODUCT REQUIREMENT DOCUMENT (PRD)
# YAN'S FOOD MOBILE ADMIN COMPANION APP
**Aplikasi Android Monitoring & Pengendalian Operasional Kuliner Yan's Food**

---

| Dokumen | Versi | Status | Target Platform | Target Pengguna |
| :--- | :--- | :--- | :--- | :--- |
| **PRD Mobile Yan's Food** | `v1.0.0` | **Approved for Development** | Android (Kotlin, Min SDK 24, Target SDK 34) | Owner / Manajer Operasional Yan's Food |

---

## 1. Latar Belakang & Ringkasan Produk

### 1.1 Latar Belakang
Yan's Food telah memiliki sistem berbasis Web (Laravel) yang melayani landing page publik dan operasional kasir Point of Sale (POS) di gerai. Dalam operasional kuliner sehari-hari, pemilik usaha (*owner*) sering tidak berada di meja kasir karena harus berbelanja bahan baku ke pasar, mengawasi dapur, atau melakukan aktivitas luar gerai. 

Pemilik usaha membutuhkan alat kendali portabel di saku celana untuk:
1. Memantau omset kasir dan transaksi yang sedang berjalan secara *real-time* tanpa harus membuka laptop.
2. Mengubah status ketersediaan menu dan stok langsung dari smartphone ketika stok dapur habis mendadak.
3. Mencatat bukti belanja bahan baku (daging ayam, cabai, minyak, gas) seketika saat masih di pasar dengan mengambil foto nota belanja fisik menggunakan kamera ponsel.

### 1.2 Mengapa Aplikasi Mobile Ini Tanpa POS Kasir?
* **Pemisahan Peran yang Tegas (*Separation of Concerns*):** Kasir POS membutuhkan meja khusus, monitor/tablet statis, koneksi printer termal Bluetooth/USB, dan antarmuka checkout cepat dengan laci kas (*cash drawer*). Memasukkan alur transaksi kasir ke ponsel pemilik justru memperlambat operasional dan rawan salah pencet.
* **Fokus Mobile Sebagai "Executive Companion":** Aplikasi Android ini didedikasikan 100% untuk fungsi pengawasan, persetujuan biaya, dan analisis bisnis, bukan mesin penerima uang di kasir gerai.

### 1.3 Arsitektur Multi-Platform & Sinkronisasi CRUD Real-Time (Single Source of Truth)
Aplikasi mobile ini **bukan sistem yang terisolasi atau berdiri sendiri**, melainkan terhubung langsung ke **Basis Data MySQL Terpusat** di Server VPS Biznet GIO (`yans_food`) melalui protokol RESTful API Laravel (`https://yansfoods.my.id/api`).

Setiap aksi **CRUD (*Create, Read, Update, Delete*)** yang dieksekusi dari aplikasi Android akan **seketika dan otomatis terintegrasi langsung** dengan Website Publik maupun Website Admin Gerai:

```
                            ┌─────────────────────────────────────────┐
                            │      DATABASE TERPUSAT CLOUD VPS        │
                            │      (MySQL Server: yans_food)          │
                            └────────────────────┬────────────────────┘
                                                 │
                   ┌─────────────────────────────┼─────────────────────────────┐
                   │                             │                             │
                   ▼                             ▼                             ▼
       ┌───────────────────────┐     ┌───────────────────────┐     ┌───────────────────────┐
       │     WEB PUBLIK        │     │    WEB ADMIN KASIR    │     │   APLIKASI ANDROID    │
       │ (Katalog Konsumen)    │     │ (Operasional POS)     │     │  (Executive Owner)    │
       │ https://yansfoods...  │     │ https://yansfoods...  │     │   Retrofit REST API   │
       └───────────────────────┘     └───────────────────────┘     └───────────────────────┘
```

#### Matriks Dampak Aksi CRUD Lintas Platform:
| Operasi CRUD di HP | Aksi yang Dilakukan Owner di Android | Dampak Otomatis di Web Publik (`/menu`) | Dampak Otomatis di Web Admin POS (`/admin/pos`) |
| :--- | :--- | :--- | :--- |
| **CREATE (Tambah)** | Menambah menu baru (misal: *"Dimsum Mentai"* Rp 18.000 + foto). | Menu langsung tayang di katalog publik dan siap dipesan via WhatsApp. | Menu baru langsung muncul di tombol kasir POS gerai untuk dijual. |
| **READ (Membaca)** | Membuka Dashboard & Riwayat Transaksi di HP. | - | Menampilkan rekap seluruh transaksi yang baru saja dicatat oleh kasir gerai. |
| **UPDATE (Mengubah)** | Menggeser switch menu *"Dimsum Ayam"* menjadi **Habis** / mengubah stok. | Muncul label merah *"Stok Habis"*, mencegah pembeli kecewa. | Menu langsung terkunci di kasir POS dan tidak dapat ditransaksikan. |
| **DELETE (Menghapus)** | Menghapus catatan pengeluaran salah input di HP. | - | Data pengeluaran di web admin langsung terhapus dan laba bersih dihitung ulang. |

---

## 2. Profil Pengguna (User Persona)

| Atribut | Detail Profil |
| :--- | :--- |
| **Nama Peran** | Pemilik Usaha / Admin Penguji (*Owner Persona*) |
| **Karakteristik** | Praktis, mobilitas tinggi, ingin melihat angka penting tanpa membuka banyak menu rumit. |
| **Perangkat Utama** | Smartphone Android (RAM 3GB - 6GB, layar 6.0" - 6.7"). |
| **Tujuan Utama** | Mengetahui apakah hari ini untung/rugi, berapa uang tunai vs QRIS di laci kasir, dan memastikan stok menu tetap aman. |

---

## 3. Ruang Lingkup Proyek (Scope of Work)

### 3.1 Didalam Cakupan (In-Scope)
1. **Autentikasi Aman:** Login menggunakan akun Owner (`admin@yansfood.id`) dan akun penguji (`dummy@yansfood.id`) terhubung ke backend Laravel via token API.
2. **Executive Dashboard:** Kartu omset hari ini, total struk, total pengeluaran hari ini, laba bersih, dan daftar peringatan stok menipis (≤ 10 porsi).
3. **Katalog Menu & Manajemen Stok:** Daftar 40 menu resmi, filter kategori, tombol instan ubah status (Tersedia/Habis), dan pengeditan cepat sisa stok.
4. **Pencatatan Biaya Dapur (Expense Tracker):** Form pencatatan belanja dapur dengan integrasi kamera native Android untuk mengambil foto fisik struk/nota.
5. **Feed Transaksi Kasir Live:** Memantau daftar struk yang baru saja diselesaikan oleh kasir di web secara kronologis.
6. **Laporan & Rekapitulasi:** Ringkasan bulanan omset vs beban dan perbandingan metode bayar CASH vs QRIS.

### 3.2 Diluar Cakupan (Out-of-Scope)
1. Fitur Keranjang Belanja & Checkout Kasir POS (tetap eksklusif di Web).
2. Cetak Struk Pelanggan lewat printer kasir (hanya ditangani di terminal kasir web gerai).
3. Keranjang belanja publik untuk konsumen umum (konsumen tetap memesan via WhatsApp web).

---

## 4. Kebutuhan Fungsional (Functional Requirements)

### FR-01: Modul Autentikasi & Sesi
* **FR-01.1:** Pengguna dapat masuk menggunakan email dan password admin yang terdaftar di basis data Yan's Food.
* **FR-01.2:** Aplikasi menyimpan sesi login secara lokal menggunakan `EncryptedSharedPreferences` / `DataStore` sehingga pengguna tidak perlu login berulang kali.
* **FR-01.3:** Pengguna dapat melakukan Logout dengan menghapus token sesi lokal dan dialihkan kembali ke layar login.

### FR-02: Modul Executive Dashboard (Beranda)
* **FR-02.1:** Menampilkan kartu ringkasan KPI hari ini:
  * Total Omset Kasir POS (Rp).
  * Total Transaksi Selesai (Struk).
  * Total Pengeluaran Dapur (Rp).
  * Estimasi Laba Bersih Operasional (Rp).
* **FR-02.2:** Menampilkan daftar *Stok Produk Menipis* jika terdapat produk dengan stok ≤ 10 porsi.
* **FR-02.3:** Menampilkan 3-5 riwayat transaksi kasir paling terakhir yang tercatat di web.
* **FR-02.4:** Menyediakan gestur *Swipe to Refresh* untuk memperbarui data KPI secara instan dari server.

### FR-03: Modul Katalog Menu & Kontrol Stok
* **FR-03.1:** Menampilkan 40 menu Yan's Food dalam daftar kartu rapi lengkap dengan foto, nama, kategori, harga, dan stok.
* **FR-03.2:** Menyediakan pencarian nama menu secara instan (*search as you type*).
* **FR-03.3:** Menyediakan filter chip berdasarkan kategori menu (Semua, Dimsum, Minuman, Dessert, dll.).
* **FR-03.4:** Menyediakan sakelar cepat (*toggle switch*) untuk mengubah status produk antara **Tersedia** (*Available*) dan **Habis** (*Unavailable*) tanpa membuka halaman form penuh.
* **FR-03.5:** Menyediakan dialog ubah stok cepat (+ / - jumlah porsi).

### FR-04: Modul Pencatatan Pengeluaran Dapur (Expense Tracker)
* **FR-04.1:** Menampilkan riwayat pengeluaran operasional per bulan yang dikelompokkan rapi.
* **FR-04.2:** Form input pengeluaran baru dengan kolom: Nama Biaya, Kategori (Bahan Baku, Gas, Listrik, Air, Kemasan, Operasional Lain), Nominal Rupiah, Tanggal, dan Metode Pembayaran (CASH/TRANSFER).
* **FR-04.3:** Integrasi Kamera & Galeri ponsel untuk mengunggah foto bukti fisik struk/nota belanja sebagai lampiran berkas multipart ke server.

### FR-05: Modul Feed Transaksi & Laporan Finansial
* **FR-05.1:** Menampilkan riwayat transaksi penjualan kasir web lengkap dengan nomor transaksi unik (`TR-YYYYMMDD-XXXXX`), waktu transaksi WIB, nama kasir, nama pelanggan, metode bayar (CASH / QRIS), dan nominal total.
* **FR-05.2:** Menampilkan dialog/layar detail rincian menu yang dibeli di dalam suatu transaksi.
* **FR-05.3:** Menampilkan ringkasan bulanan: Total Pemasukan vs Total Pengeluaran beserta persentase perbandingan transaksi Cash vs QRIS.

---

## 5. Kebutuhan Non-Fungsional (Non-Functional Requirements)

1. **Kecepatan & Responsivitas (Performance):**
   * Pemuatan data layar utama harus selesai di bawah 2 detik pada koneksi 4G standar.
   * Render list menu dan riwayat transaksi menggunakan `RecyclerView` dengan pola `DiffUtil` agar scrolling mulus 60 FPS tanpa patah-patah (*zero jank*).
2. **Efisiensi Memori & Baterai:**
   * Pemuatan foto produk menggunakan library `Glide` atau `Coil` dengan *automatic memory caching* dan *downsampling* agar tidak menyebabkan error *Out of Memory (OOM)* pada ponsel spesifikasi pemula (*entry-level*).
3. **Ketahanan Koneksi (Reliability):**
   * Aplikasi menangani kondisi offline / gangguan sinyal dengan menampilkan *State Layout* yang bersahabat (misal: "Gagal memuat data, periksa koneksi internet Anda" disertai tombol Coba Lagi).
4. **Keamanan Data (Security):**
   * Komunikasi data wajib menggunakan protokol terenkripsi HTTPS ke domain `https://yansfoods.my.id`.
   * Token autentikasi disimpan di penyimpanan aman (*Secure Storage*).

---

## 6. Arsitektur Teknis Android & Integrasi API

### 6.1 Arsitektur Aplikasi (MVVM Pattern)
Aplikasi dibangun menggunakan pola standar Google **Model-View-ViewModel (MVVM)**:
* **View Layer (XML + Activity/Fragment):** Menangani antarmuka visual dan menerima interaksi pengguna. Menggunakan `ViewBinding` murni untuk menghindari *boilerplate* `findViewById`.
* **ViewModel Layer (`AndroidX ViewModel + LiveData / StateFlow`):** Mengelola status antarmuka (*UI state*) dan mempertahankan data saat terjadi perubahan orientasi layar (*configuration change*).
* **Repository Layer:** Mengabstraksi sumber data dari backend REST API.
* **Network Layer:** **Retrofit 2 + OkHttp3 Logging Interceptor** untuk melakukan serialisasi data JSON dari server Laravel.

### 6.2 Pemetaan Endpoint REST API Backend Laravel

| Endpoint API Laravel | Metode | Fungsi di Aplikasi Android |
| :--- | :---: | :--- |
| `/api/login` | `POST` | Validasi kredensial admin dan pengembalian API Token. |
| `/api/dashboard` | `GET` | Mengambil data KPI hari ini (omset, transaksi, biaya, laba, stok tipis). |
| `/api/products` | `GET` | Mengambil daftar seluruh menu dan status stok. |
| `/api/products/{id}/status` | `PATCH` | Mengubah status ketersediaan menu secara cepat (*toggle*). |
| `/api/products/{id}/stock` | `PATCH` | Memperbarui jumlah sisa stok produk. |
| `/api/expenses` | `GET` | Mengambil daftar riwayat catatan pengeluaran dapur. |
| `/api/expenses` | `POST` | Mengunggah catatan pengeluaran baru beserta foto bukti nota (Multipart). |
| `/api/transactions` | `GET` | Mengambil feed riwayat transaksi kasir dari web. |
| `/api/reports/summary` | `GET` | Mengambil data rekapitulasi laba rugi bulanan dan persentase Cash vs QRIS. |

---

## 7. Rencana Tahapan Eksekusi (Implementation Milestones)

* **Fase 1: Backend Endpoint Readiness** — Menyediakan endpoint API ringan pada file `routes/api.php` di Laravel yang sudah terpasang di VPS.
* **Fase 2: Project Setup & Design System** — Inisialisasi Android Studio, konfigurasi Gradle, `colors.xml`, `themes.xml`, `dimens.xml`, dan Bottom Navigation.
* **Fase 3: Autentikasi & Dashboard** — Implementasi LoginActivity, token session manager, dan halaman Beranda KPI.
* **Fase 4: Modul Menu & Stok** — Implementasi RecyclerView katalog menu, filter kategori, dan quick-update stok.
* **Fase 5: Modul Pengeluaran & Kamera** — Implementasi pencatatan pengeluaran, pemilihan kategori, dan pengambilan foto bukti nota.
* **Fase 6: Modul Transaksi & Laporan** — Implementasi live feed penjualan kasir web dan rekap grafik/persentase.
* **Fase 7: Testing & Packaging APK** — Pengujian menyeluruh di HP Android fisik, optimasi layout, dan build berkas `app-release.apk` siap uji di hadapan guru.
