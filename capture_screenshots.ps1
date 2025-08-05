# HesApp Screenshot Capture Script
# Bu script, uygulamanın ekran görüntülerini almak için kullanılır

Write-Host "📱 HesApp Ekran Görüntüleri Alınıyor..." -ForegroundColor Green

# ADB'nin çalışıp çalışmadığını kontrol et
try {
    $adbResult = adb devices
    Write-Host "✅ ADB bağlantısı başarılı" -ForegroundColor Green
} catch {
    Write-Host "❌ ADB bulunamadı. Android SDK'nın PATH'e eklendiğinden emin olun." -ForegroundColor Red
    exit 1
}

# Screenshots klasörünü oluştur
if (!(Test-Path "screenshots")) {
    New-Item -ItemType Directory -Path "screenshots"
    Write-Host "📁 Screenshots klasörü oluşturuldu" -ForegroundColor Yellow
}

# Ekran görüntüsü alma fonksiyonu
function Capture-Screenshot {
    param(
        [string]$Name,
        [string]$Description
    )
    
    Write-Host "📸 $Description alınıyor..." -ForegroundColor Cyan
    
    # Ekran görüntüsü al
    adb shell screencap -p /sdcard/$Name.png
    
    # Dosyayı bilgisayara kopyala
    adb pull /sdcard/$Name.png screenshots/$Name.png
    
    # Telefondan dosyayı sil
    adb shell rm /sdcard/$Name.png
    
    Write-Host "✅ $Description kaydedildi: screenshots/$Name.png" -ForegroundColor Green
}

# Uygulamayı başlat
Write-Host "🚀 HesApp başlatılıyor..." -ForegroundColor Yellow
adb shell am start -n com.ncorp.hesapp/.MainActivity

# Biraz bekle
Start-Sleep -Seconds 3

# Ekran görüntülerini al
Write-Host "📸 Ekran görüntüleri alınıyor..." -ForegroundColor Yellow

# Ana sayfa
Capture-Screenshot -Name "dashboard" -Description "Ana Sayfa (Dashboard)"

# İşlemler sayfasına git
adb shell input tap 300 1800  # Bottom navigation - İşlemler
Start-Sleep -Seconds 2
Capture-Screenshot -Name "transactions" -Description "İşlemler Sayfası"

# Kişiler sayfasına git
adb shell input tap 600 1800  # Bottom navigation - Kişiler
Start-Sleep -Seconds 2
Capture-Screenshot -Name "contacts" -Description "Kişiler Sayfası"

# Raporlar sayfasına git
adb shell input tap 900 1800  # Bottom navigation - Raporlar
Start-Sleep -Seconds 2
Capture-Screenshot -Name "reports" -Description "Raporlar Sayfası"

# Ayarlar sayfasına git
adb shell input tap 1200 1800  # Bottom navigation - Ayarlar
Start-Sleep -Seconds 2
Capture-Screenshot -Name "settings" -Description "Ayarlar Sayfası"

# Ana sayfaya geri dön
adb shell input tap 0 1800  # Bottom navigation - Ana Sayfa
Start-Sleep -Seconds 2

# Tema değiştir
adb shell input tap 1000 100  # Tema geçiş butonu
Start-Sleep -Seconds 1
Capture-Screenshot -Name "dark_theme" -Description "Karanlık Tema"

# Tekrar tema değiştir
adb shell input tap 1000 100  # Tema geçiş butonu
Start-Sleep -Seconds 1
Capture-Screenshot -Name "light_theme" -Description "Aydınlık Tema"

# İşlemler sayfasında örnek veri ekle
adb shell input tap 300 1800  # İşlemler sayfasına git
Start-Sleep -Seconds 2

# Empty state'e tıkla (örnek veri eklemek için)
adb shell input tap 400 800
Start-Sleep -Seconds 2
Capture-Screenshot -Name "transactions_with_data" -Description "İşlemler Sayfası (Veri ile)"

# Arama yap
adb shell input tap 200 200  # SearchView'a tıkla
adb shell input text "müşteri"
Start-Sleep -Seconds 1
Capture-Screenshot -Name "search_results" -Description "Arama Sonuçları"

# Filtreleme
adb shell input tap 100 300  # Gelir filtresi
Start-Sleep -Seconds 1
Capture-Screenshot -Name "filtered_results" -Description "Filtrelenmiş Sonuçlar"

Write-Host "🎉 Tüm ekran görüntüleri başarıyla alındı!" -ForegroundColor Green
Write-Host "📁 Dosyalar 'screenshots' klasöründe bulunabilir" -ForegroundColor Cyan

# Dosya listesini göster
Write-Host "📋 Alınan dosyalar:" -ForegroundColor Yellow
Get-ChildItem "screenshots" | ForEach-Object {
    Write-Host "  📸 $($_.Name)" -ForegroundColor White
} 