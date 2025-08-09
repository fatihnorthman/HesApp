plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
	alias(libs.plugins.navigation.safeargs.kotlin)
	alias(libs.plugins.hilt.android)
}

android {
	namespace = "com.ncorp.hesapp"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.ncorp.hesapp"
		minSdk = 26
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	buildFeatures {
		viewBinding = true
	}
	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			isDebuggable = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
		debug {
			isMinifyEnabled = false
			isShrinkResources = false
			isDebuggable = true
			applicationIdSuffix = ".debug"
			versionNameSuffix = "-debug"
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
		freeCompilerArgs += listOf(
			"-opt-in=kotlin.RequiresOptIn",
			"-Xjvm-default=all"
		)
	}
	
	// Performance optimizations
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
	}
	
	// Packaging options
	packagingOptions {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
			excludes += "META-INF/gradle/incremental.annotation.processors"
		}
	}
}

dependencies {
	// AndroidX Core
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.androidx.fragment.ktx)

	// ViewModel, LiveData, Lifecycle
	implementation(libs.androidx.lifecycle.livedata.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)

	// Room DB
	implementation(libs.androidx.room.runtime)
	ksp(libs.androidx.room.compiler)
	implementation(libs.androidx.room.ktx)

	// Navigation Component
	implementation(libs.androidx.navigation.fragment.ktx)
	implementation(libs.androidx.navigation.ui.ktx)

	// RecyclerView
	implementation(libs.androidx.recyclerview)

	// SwipeRefreshLayout
	implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

	// Hilt
	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)
	
	// Performance monitoring
	implementation("androidx.profileinstaller:profileinstaller:1.3.1")
	
	// Core library desugaring
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
	implementation("com.google.guava:guava:32.1.2-android")

	// SplashScreen API
	implementation("androidx.core:core-splashscreen:1.0.1")
	
	// Test dependencies
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}