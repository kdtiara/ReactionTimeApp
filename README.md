# Reaction Time Tester (Java Swing)

Aplikasi uji refleks: layar merah → tunggu → berubah hijau → klik (atau tekan **SPASI**) secepat mungkin.
Waktu reaksi diukur dalam milidetik, ada deteksi "terlalu cepat", dan 10 skor terbaik disimpan selama aplikasi berjalan.

Dibuat sebagai latihan eksplorasi GUI.

- **NIM  :** F1D02510130
- **Nama :** Pande Kadek Tiara Lestari


## 5 Komponen Utama GUI yang Digunakan

### 1. Top-level Container Class
| Class | Fungsi |
|-------|--------|
| `JFrame` | Jendela utama aplikasi (`ReactionTimeApp extends JFrame`). |

### 2. Intermediate Container Class
| Class | Fungsi |
|-------|--------|
| `JPanel` | Menampung komponen: kartu menu, kartu game, kartu skor, area warna, panel tombol. |
| `JScrollPane` | Membungkus `JList` agar daftar skor bisa di-scroll. |

### 3. Atomic Component Class
| Class | Fungsi |
|-------|--------|
| `JLabel` | Judul, instruksi, status, teks di area warna. |
| `JButton` | Tombol Mulai, Skor Terbaik, Keluar, Kembali, Reset Skor. |
| `JList` (+ `DefaultListModel`) | Menampilkan 10 skor terbaik. |

### 4. Layout Classes
| Class | Dipakai di |
|-------|-----------|
| `CardLayout` | Berpindah antar layar: Menu / Game / Skor. |
| `BorderLayout` | Susunan utama tiap layar (judul atas, konten tengah, tombol bawah). |
| `GridLayout` | Judul + deskripsi di menu, dan tiga tombol menu. |
| `FlowLayout` | Baris tombol di bagian bawah layar game dan skor. |

### 5. Event Listener
| Class / Interface | Method yang di-override | Fungsi |
|-------------------|-------------------------|--------|
| `MouseAdapter` | `mousePressed()` | Klik pada area warna untuk mulai/merespons. |
| `KeyAdapter` | `keyPressed()` | Tombol **SPASI** sebagai alternatif klik. |
| `WindowAdapter` | `windowClosing()` | Dialog konfirmasi saat menutup jendela. |
| `ActionListener` (interface) | `actionPerformed()` (via lambda) | Semua tombol, dan `javax.swing.Timer` untuk jeda acak 1,5–4,5 detik. |

