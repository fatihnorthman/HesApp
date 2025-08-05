# HesApp 📱

**Modern Android Hesap Takip Uygulaması**

[![Android](https://img.shields.io/badge/Android-API%2026+-green.svg)](https://developer.android.com/about/versions/android-8.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)
[![Material Design](https://img.shields.io/badge/Material%20Design-3.0-orange.svg)](https://material.io/design)
[![Room Database](https://img.shields.io/badge/Room-Database-red.svg)](https://developer.android.com/training/data-storage/room)
[![MVVM](https://img.shields.io/badge/Architecture-MVVM-purple.svg)](https://developer.android.com/jetpack/guide)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

<div align="center">
  <img src="https://via.placeholder.com/200x200/3B82F6/FFFFFF?text=HesApp" alt="HesApp Logo" width="200"/>
  
  *Modern, kullanıcı dostu hesap takip uygulaması*
</div>

## 📖 Hakkında

HesApp, kişisel ve iş hesap takibi için geliştirilmiş modern bir Android uygulamasıdır. Gelir, gider, borç ve alacak işlemlerinizi kolayca takip edebilir, detaylı raporlar alabilir ve kişilerinizle olan finansal ilişkilerinizi yönetebilirsiniz.

### ✨ Özellikler

- 💰 **Gelir/Gider Takibi** - Tüm finansal işlemlerinizi kategorize edin
- 🏦 **Borç/Alacak Yönetimi** - Kişilerle olan finansal ilişkilerinizi takip edin
- 📊 **Detaylı Raporlar** - Grafik ve istatistiklerle finansal durumunuzu analiz edin
- 👥 **Kişi Yönetimi** - Müşteri, tedarikçi ve çalışan bilgilerinizi saklayın
- 🔍 **Gelişmiş Arama** - İşlemlerinizi hızlıca bulun
- 🎨 **Modern Arayüz** - Material Design 3 ile güzel ve kullanıcı dostu arayüz
- 🌙 **Karanlık/Aydınlık Tema** - Gözünüzü yormayan tema seçenekleri
- 📱 **Responsive Tasarım** - Tüm cihazlarda mükemmel görünüm
- ⚡ **Yüksek Performans** - Optimize edilmiş kod yapısı
- 🔒 **Güvenli Veri** - Yerel SQLite veritabanı ile güvenli depolama

## 🖼️ Ekran Görüntüleri

<div align="center">
  <table>
    <tr>
      <td align="center">
        <strong>Ana Sayfa (Dashboard)</strong><br/>
        <img src="docs/screenshots/dashboard.png" width="150" alt="Dashboard"/>
      </td>
      <td align="center">
        <strong>İşlemler</strong><br/>
        <img src="docs/screenshots/transactions.png" width="150" alt="Transactions"/>
      </td>
      <td align="center">
        <strong>Kişiler</strong><br/>
        <img src="docs/screenshots/contacts.png" width="150" alt="Contacts"/>
      </td>
    </tr>
    <tr>
      <td align="center">
        <strong>İşlem Ekleme</strong><br/>
        <img src="docs/screenshots/add-transaction.png" width="150" alt="Add Transaction"/>
      </td>
      <td align="center">
        <strong>Karanlık Tema</strong><br/>
        <img src="docs/screenshots/dashboard.png" width="150" alt="Dark Theme"/>
      </td>
      <td align="center">
        <strong>Modern UI</strong><br/>
        <img src="docs/screenshots/transactions.png" width="150" alt="Modern UI"/>
      </td>
    </tr>
  </table>
</div>

### 📱 **Ana Sayfa (Dashboard)**
- **Finansal Özet Kartları** - Gelir, gider, borç, alacak toplamları
- **İstatistik Kartları** - Toplam işlem ve kişi sayıları
- **Hızlı İşlemler** - Kolay erişim butonları
- **Modern Tasarım** - Material Design 3 ile şık arayüz

### 💰 **İşlemler Sayfası**
- **Arama ve Filtreleme** - Gelişmiş arama ve chip filtreleri
- **İşlem Listesi** - Performanslı RecyclerView
- **Floating Action Button** - Yeni işlem ekleme
- **Swipe Refresh** - Yenileme özelliği

### 👥 **Kişiler Sayfası**
- **Kişi Yönetimi** - Müşteri, tedarikçi, çalışan kategorileri
- **Filtreleme Sistemi** - Kişi türüne göre filtreleme
- **Arama Fonksiyonu** - Hızlı kişi arama
- **Modern Chip Tasarımı** - Görsel filtreleme

### ➕ **İşlem Ekleme Sayfası**
- **İşlem Türü Seçimi** - Chip group ile görsel seçim
- **Form Validasyonu** - Kapsamlı veri kontrolü
- **Tarih Seçici** - Native DatePickerDialog
- **Dinamik Form** - İşlem türüne göre alan görünürlüğü

### 🌙 **Karanlık Tema**
- **Göz Dostu Tasarım** - Koyu arka plan ve açık metinler
- **Renk Uyumu** - Tutarlı renk paleti
- **Modern İkonlar** - Material Design ikonları
- **Responsive Layout** - Tüm cihazlarda uyumlu

## 🛠️ Teknolojiler

### Backend & Veritabanı
- **Room Database** - Yerel SQLite veritabanı
- **Kotlin Coroutines** - Asenkron işlemler
- **Flow** - Reaktif veri akışı
- **Hilt** - Dependency injection

### UI/UX
- **Material Design 3** - Modern tasarım sistemi
- **Navigation Component** - Fragment yönetimi
- **RecyclerView** - Performanslı liste görünümleri
- **ViewBinding** - Güvenli view erişimi
- **Animasyonlar** - Yumuşak geçişler ve efektler

### Mimari
- **MVVM** - Model-View-ViewModel mimarisi
- **Repository Pattern** - Veri erişim katmanı
- **Clean Architecture** - Temiz kod yapısı
- **SOLID Principles** - Yazılım geliştirme prensipleri

## 📱 Kurulum

### Gereksinimler
- Android Studio Arctic Fox veya üzeri
- Android SDK API 26+
- Kotlin 2.0.21+
- Gradle 8.12.0+

### Adımlar

1. **Repository'yi klonlayın**
```bash
git clone https://github.com/fatihnorthman/hesapp.git
cd hesapp
```

2. **Projeyi Android Studio'da açın**
```bash
android-studio hesapp
```

3. **Bağımlılıkları senkronize edin**
```bash
./gradlew build
```

4. **Uygulamayı çalıştırın**
```bash
./gradlew installDebug
```

## 🏗️ Proje Yapısı

```
app/
├── src/main/
│   ├── java/com/ncorp/hesapp/
│   │   ├── data/
│   │   │   ├── dao/           # Veritabanı erişim katmanı
│   │   │   ├── database/      # Room veritabanı
│   │   │   ├── model/         # Veri modelleri
│   │   │   ├── repository/    # Repository katmanı
│   │   │   └── converter/     # Tip dönüştürücüleri
│   │   ├── ui/
│   │   │   ├── fragment/      # Fragment'lar
│   │   │   ├── adapter/       # RecyclerView adapter'ları
│   │   │   └── viewmodel/     # ViewModel'ler
│   │   ├── di/                # Dependency injection
│   │   └── HesAppApplication.kt
│   └── res/
│       ├── layout/            # Layout dosyaları
│       ├── values/            # String, color, style
│       ├── drawable/          # İkonlar ve görseller
│       ├── anim/              # Animasyon dosyaları
│       └── navigation/        # Navigation graph
```

## 🎯 Özellik Detayları

### 📊 Dashboard
- **Özet Kartları** - Gelir, gider, borç, alacak toplamları
- **Grafikler** - Aylık/haftalık trend analizi
- **Son İşlemler** - En son yapılan işlemlerin listesi
- **Hızlı Erişim** - Sık kullanılan fonksiyonlar

### 💰 İşlemler
- **CRUD İşlemleri** - Ekleme, düzenleme, silme, görüntüleme
- **Filtreleme** - Tür, tarih, kategori bazında filtreleme
- **Arama** - Açıklama ve kategori araması
- **Kategoriler** - Gelir, Gider, Borç, Alacak
- **Tarih Takibi** - İşlem tarihleri ve geçmiş

### 👥 Kişiler
- **Kişi Yönetimi** - Müşteri, tedarikçi, çalışan kayıtları
- **İletişim Bilgileri** - Telefon, e-posta, adres
- **Finansal İlişki** - Kişiye özel işlem geçmişi
- **Kategoriler** - Kişi türüne göre sınıflandırma

### 📈 Raporlar
- **Gelir/Gider Analizi** - Aylık karşılaştırmalar
- **Kategori Raporları** - Harcama dağılımları
- **Trend Analizi** - Zaman bazlı grafikler
- **Export** - PDF/Excel rapor çıktısı

### ⚙️ Ayarlar
- **Tema Seçimi** - Aydınlık/Karanlık tema
- **Para Birimi** - TL, USD, EUR desteği
- **Yedekleme** - Veri yedekleme ve geri yükleme
- **Bildirimler** - Hatırlatma ve uyarılar

## 🎨 Tasarım Sistemi

### Renk Paleti
- **Primary**: #3B82F6 (Mavi)
- **Secondary**: #8B5CF6 (Mor)
- **Success**: #10B981 (Yeşil)
- **Warning**: #F59E0B (Turuncu)
- **Error**: #EF4444 (Kırmızı)

### Tipografi
- **Headline**: Roboto Bold
- **Body**: Roboto Regular
- **Caption**: Roboto Light

### İkonlar
- **Material Icons** - Google Material Design ikonları
- **Custom Gradients** - İşlem türüne göre gradient arka planlar

## 🚀 Performans

### Optimizasyonlar
- **Lazy Loading** - Büyük listeler için performanslı yükleme
- **DiffUtil** - RecyclerView için akıllı güncelleme
- **ViewBinding** - Null-safe view erişimi
- **Coroutines** - Asenkron işlemler için hafif thread'ler

### Bellek Yönetimi
- **Singleton Pattern** - Tek instance yönetimi
- **Weak References** - Memory leak önleme
- **Lifecycle Awareness** - Otomatik kaynak temizleme

## 🧪 Test

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
```bash
./gradlew jacocoTestReport
```

## 📦 Release

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### APK Konumları
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## 🤝 Katkıda Bulunma

1. **Fork edin** - Projeyi fork edin
2. **Branch oluşturun** - `git checkout -b feature/amazing-feature`
3. **Commit edin** - `git commit -m 'Add amazing feature'`
4. **Push edin** - `git push origin feature/amazing-feature`
5. **Pull Request açın** - GitHub'da pull request oluşturun

### Katkı Kuralları
- **Kod Stili** - Kotlin coding conventions'a uyun
- **Test Yazın** - Yeni özellikler için test yazın
- **Dokümantasyon** - Kodunuzu belgelendirin
- **Commit Mesajları** - Açıklayıcı commit mesajları yazın

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## 👨‍💻 Geliştirici

**Fatih Northman**


- 📱 GitHub: [@fatihnorthman](https://github.com/fatihnorthman)

## 🙏 Teşekkürler

- [Android Jetpack](https://developer.android.com/jetpack) - Modern Android geliştirme
- [Material Design](https://material.io/) - Tasarım sistemi
- [Room Database](https://developer.android.com/training/data-storage/room) - Veritabanı
- [Hilt](https://dagger.dev/hilt/) - Dependency injection
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Asenkron programlama

## 📈 Roadmap

### v1.1.0 (Yakında)
- [ ] Çoklu para birimi desteği
- [ ] Gelişmiş grafik raporları
- [ ] Veri yedekleme/geri yükleme
- [ ] Bildirim sistemi

### v1.2.0 (Planlanan)
- [ ] Widget desteği
- [ ] Wear OS entegrasyonu
- [ ] Cloud sync
- [ ] Çoklu kullanıcı desteği

### v2.0.0 (Gelecek)
- [ ] Web uygulaması
- [ ] API entegrasyonu
- [ ] AI destekli analiz
- [ ] Blockchain entegrasyonu

---

<div align="center">
  ⭐ **Bu projeyi beğendiyseniz yıldız vermeyi unutmayın!**
  
  [![GitHub stars](https://img.shields.io/github/stars/fatihnorthman/hesapp.svg?style=social&label=Star)](https://github.com/fatihnorthman/hesapp)
  [![GitHub forks](https://img.shields.io/github/forks/fatihnorthman/hesapp.svg?style=social&label=Fork)](https://github.com/fatihnorthman/hesapp)
  [![GitHub issues](https://img.shields.io/github/issues/fatihnorthman/hesapp.svg)](https://github.com/fatihnorthman/hesapp/issues)
  [![GitHub pull requests](https://img.shields.io/github/issues-pr/fatihnorthman/hesapp.svg)](https://github.com/fatihnorthman/hesapp/pulls)
</div> 