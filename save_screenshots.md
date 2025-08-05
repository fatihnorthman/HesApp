# 📸 Ekran Görüntülerini Kaydetme

Bu dosya, HesApp uygulamasının ekran görüntülerini kaydetmek için talimatları içerir.

## 📱 Gerekli Ekran Görüntüleri

### 1. **Ana Sayfa (Dashboard)**
- **Dosya Adı**: `docs/screenshots/dashboard.png`
- **Açıklama**: Karanlık tema ile ana sayfa
- **Özellikler**:
  - Finansal özet kartları (Gelir: 15.750 ₺, Gider: 8.250 ₺, Net: 7.500 ₺)
  - İstatistik kartları (Toplam İşlem: 24, Toplam Kişi: 12)
  - Hızlı işlem butonları
  - Modern Material Design 3 arayüzü

### 2. **İşlemler Sayfası**
- **Dosya Adı**: `docs/screenshots/transactions.png`
- **Açıklama**: İşlemler listesi ve filtreleme
- **Özellikler**:
  - Arama çubuğu
  - Filtreleme chip'leri (Tümü, Gelir, Gider, Borç, Alacak)
  - Floating Action Button (+)
  - Boş durum (henüz işlem yok)

### 3. **Kişiler Sayfası**
- **Dosya Adı**: `docs/screenshots/contacts.png`
- **Açıklama**: Kişi yönetimi sayfası
- **Özellikler**:
  - Arama çubuğu
  - Filtreleme chip'leri (Tümü, Müşteri, Tedarikçi, Çalışan)
  - Floating Action Button (+)
  - Boş durum (henüz kişi yok)

### 4. **İşlem Ekleme Sayfası**
- **Dosya Adı**: `docs/screenshots/add-transaction.png`
- **Açıklama**: Yeni işlem ekleme formu
- **Özellikler**:
  - İşlem türü seçimi (Gelir, Gider, Borç, Alacak)
  - Form alanları (Açıklama, Kategori, Tutar, Tarih, Notlar)
  - Kaydet butonu (FAB)
  - Modern form tasarımı

## 📋 Kaydetme Talimatları

### **Android Studio ile:**
1. Uygulamayı çalıştırın
2. Her sayfaya gidin
3. `Ctrl + Shift + A` tuşlarına basın
4. "Screenshot" yazın ve seçin
5. Dosyayı `docs/screenshots/` klasörüne kaydedin

### **ADB ile:**
```bash
# Ana sayfa
adb shell screencap -p /sdcard/dashboard.png
adb pull /sdcard/dashboard.png docs/screenshots/

# İşlemler sayfası
adb shell screencap -p /sdcard/transactions.png
adb pull /sdcard/transactions.png docs/screenshots/

# Kişiler sayfası
adb shell screencap -p /sdcard/contacts.png
adb pull /sdcard/contacts.png docs/screenshots/

# İşlem ekleme sayfası
adb shell screencap -p /sdcard/add-transaction.png
adb pull /sdcard/add-transaction.png docs/screenshots/
```

### **Manuel Kaydetme:**
1. Uygulamayı açın
2. Her sayfaya gidin
3. Ekran görüntüsü alın (Power + Volume Down)
4. Dosyaları `docs/screenshots/` klasörüne kopyalayın
5. Dosya adlarını yukarıdaki gibi değiştirin

## 🎨 Görsel Kalite

### **Önerilen Ayarlar:**
- **Çözünürlük**: 1080x2400 (FHD+)
- **Format**: PNG
- **Kalite**: Yüksek
- **Boyut**: 150px genişlik (README için)

### **Tema Seçimi:**
- **Karanlık Tema** kullanın (daha modern görünüm)
- **Material Design 3** bileşenleri görünür olsun
- **Renkli kartlar** ve **chip'ler** net olsun

## 📝 Notlar

- Ekran görüntüleri **karanlık tema** ile alınmalı
- **Tüm özellikler** görünür olmalı
- **Modern tasarım** öne çıkmalı
- **Performans** ve **kullanıcı deneyimi** vurgulanmalı

---

**Not**: Bu ekran görüntüleri README.md dosyasında kullanılacaktır. 