# react-native-upi-intentA React Native package for UPI integration using Intents on Android.

## Table of Contents

- [Installation](#installation)
- [Linking](#linking)
  - [Automatic Linking](#automatic-linking)
  - [Manual Linking](#manual-linking)
    - [Android](#android)
    - [iOS](#ios)
- [API](#api)
- [Contributing](#contributing)
- [License](#license)

## Installation

### Using npm

```sh
npm install react-native-upi-intent
```

### Using yarn

```sh
yarn add react-native-upi-intent
```

## Linking

### Automatic Linking

For React Native 0.60 and above, the package will be automatically linked. If you are using an older version, you need to link the package manually:

```sh
react-native link react-native-upi-intent
```

### Manual Linking

If you are using React Native 0.60 and above, the package should be linked automatically. For versions below 0.60 or if automatic linking does not work, you will need to link the package manually.

#### Android

1. **Add the package to your project settings:**

   **`android/settings.gradle`**

   ```gradle
   include ':react-native-upi-intent'
   project(':react-native-upi-intent').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-upi-intent/android')
   ```

2. **Add the package as a dependency in your app:**

   **`android/app/build.gradle`**

   ```gradle
   dependencies {
       implementation project(':react-native-upi-intent')
   }
   ```

3. **Add the package to your MainApplication:**

   **`android/app/src/main/java/.../MainApplication.java`**

   ```java
   import com.reactnativeupiintent.UPIPackage; // <-- Add this import

   public class MainApplication extends Application implements ReactApplication {
       // ...

       @Override
       protected List<ReactPackage> getPackages() {
           @SuppressWarnings(\"UnnecessaryLocalVariable\")
           List<ReactPackage> packages = new PackageList(this).getPackages();
           packages.add(new UPIPackage()); // <-- Add this line
           return packages;
       }

       // ...
   }
   ```

#### iOS

Currently, this package supports only Android. iOS support can be added in future updates.


## Contributing

Contributions are welcome! Please open an issue or submit a pull request on GitHub.

1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Submit a pull request.

## License

MIT License

