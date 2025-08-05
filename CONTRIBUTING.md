# 🤝 Katkıda Bulunma Rehberi

HesApp projesine katkıda bulunmak istediğiniz için teşekkür ederiz! Bu rehber, projeye nasıl katkıda bulunabileceğinizi açıklar.

## 📋 İçindekiler

- [Başlarken](#başlarken)
- [Geliştirme Ortamı](#geliştirme-ortamı)
- [Kod Yazma Kuralları](#kod-yazma-kuralları)
- [Commit Mesajları](#commit-mesajları)
- [Pull Request Süreci](#pull-request-süreci)
- [Raporlama](#raporlama)

## 🚀 Başlarken

### 1. Repository'yi Fork Edin
GitHub'da HesApp repository'sini fork edin.

### 2. Local Repository'yi Klonlayın
```bash
git clone https://github.com/fatihnorthman/hesapp.git
cd hesapp
```

### 3. Remote'u Ayarlayın
```bash
git remote add upstream https://github.com/original/hesapp.git
```

## 🛠️ Geliştirme Ortamı

### Gereksinimler
- Android Studio Arctic Fox veya üzeri
- Android SDK API 26+
- Kotlin 2.0.21+
- Gradle 8.12.0+

### Kurulum
1. Projeyi Android Studio'da açın
2. Gradle sync'i bekleyin
3. Uygulamayı çalıştırın: `./gradlew installDebug`

## 📝 Kod Yazma Kuralları

### Kotlin Stil Rehberi
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)'a uyun
- 4 space indentation kullanın
- UTF-8 encoding kullanın
- Line ending: LF (Unix)

### Dosya Organizasyonu
```
app/src/main/java/com/ncorp/hesapp/
├── data/           # Veri katmanı
├── ui/             # Kullanıcı arayüzü
├── di/             # Dependency injection
└── HesAppApplication.kt
```

### Naming Conventions
- **Sınıflar**: PascalCase (örn: `TransactionAdapter`)
- **Fonksiyonlar**: camelCase (örn: `getAllTransactions()`)
- **Değişkenler**: camelCase (örn: `transactionList`)
- **Sabitler**: UPPER_SNAKE_CASE (örn: `MAX_RETRY_COUNT`)

### Kod Kalitesi
- **Kotlin Lint** kurallarına uyun
- **SonarQube** analizini geçin
- **Unit test** yazın (en az %80 coverage)
- **Documentation** ekleyin

## 💬 Commit Mesajları

### Format
```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Types
- **feat**: Yeni özellik
- **fix**: Hata düzeltmesi
- **docs**: Dokümantasyon
- **style**: Kod formatı
- **refactor**: Kod refactoring
- **test**: Test ekleme/düzenleme
- **chore**: Build, config değişiklikleri

### Örnekler
```bash
feat(transactions): add search functionality
fix(ui): resolve theme toggle crash
docs(readme): update installation guide
refactor(data): optimize database queries
```

## 🔄 Pull Request Süreci

### 1. Branch Oluşturun
```bash
git checkout -b feature/amazing-feature
```

### 2. Değişiklikleri Yapın
- Kodunuzu yazın
- Testleri ekleyin
- Dokümantasyonu güncelleyin

### 3. Test Edin
```bash
./gradlew test
./gradlew connectedAndroidTest
./gradlew lint
```

### 4. Commit Edin
```bash
git add .
git commit -m "feat(module): add amazing feature"
```

### 5. Push Edin
```bash
git push origin feature/amazing-feature
```

### 6. Pull Request Oluşturun
- GitHub'da PR açın
- Template'i doldurun
- Review'ları bekleyin

## 📋 Pull Request Template

```markdown
## 📝 Açıklama
Bu PR ne yapıyor?

## 🎯 Değişiklik Türü
- [ ] Bug fix
- [ ] Yeni özellik
- [ ] Breaking change
- [ ] Dokümantasyon güncellemesi

## 🧪 Test
- [ ] Unit testler eklendi
- [ ] UI testler eklendi
- [ ] Manuel test yapıldı

## 📸 Screenshots (UI değişiklikleri için)
![Screenshot](url)

## ✅ Checklist
- [ ] Kod stil rehberine uyuldu
- [ ] Self-review yapıldı
- [ ] Testler eklendi
- [ ] Dokümantasyon güncellendi
- [ ] Breaking changes belgelendi

## 🔗 İlgili Issue
Closes #123
```

## 🐛 Raporlama

### Bug Report Template
```markdown
## 🐛 Bug Açıklaması
Kısa ve net açıklama

## 🔄 Adımlar
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## 📱 Beklenen Davranış
Ne olması gerekiyordu?

## 🚨 Gerçek Davranış
Ne oldu?

## 📸 Screenshots
Varsa ekran görüntüleri

## 📱 Cihaz Bilgileri
- OS: Android 11
- Device: Samsung Galaxy S21
- App Version: 1.0.0

## 📋 Ek Bilgiler
Ek bağlam veya loglar
```

### Feature Request Template
```markdown
## 💡 Özellik İsteği
Kısa açıklama

## 🎯 Problem
Bu özellik neden gerekli?

## 💭 Önerilen Çözüm
Nasıl çalışmalı?

## 🔄 Alternatifler
Düşündüğünüz diğer çözümler

## 📋 Ek Bilgiler
Ek bağlam, mockup'lar vs.
```

## 🏷️ Issue Labels

- **bug**: Hata raporları
- **enhancement**: Özellik istekleri
- **documentation**: Dokümantasyon
- **good first issue**: Yeni başlayanlar için
- **help wanted**: Yardım istenen konular
- **question**: Sorular
- **wontfix**: Düzeltilmeyecek sorunlar

## 📞 İletişim

- **GitHub Issues**: [Issues](https://github.com/fatihnorthman/hesapp/issues)
- **Email**: fatihnorthman@gmail.com

## 🙏 Teşekkürler

Katkıda bulunan herkese teşekkür ederiz! 🎉

---

**Not**: Bu rehber sürekli güncellenmektedir. Önerileriniz varsa lütfen issue açın. 