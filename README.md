# JavaFX Blur
This library provides methods to apply a blur effect to a JavaFX stage using JNI to call the native window manager functions.

<img src="/res/example.gif?raw=true" width="500px">

## Install
To install, simply add the jar file as a dependency on your favourite IDE (add jar to classpath) and add the library files (.dll) to the library path.

## Usage
The module load method should be called at the start of your program to ensure the native functions have been loaded before the external function calls.

```java
public static void main(String[] args){
    Blur.loadBlurLibrary();
    // Launch JavaFX application
}
```

Once your JavaFX stage is shown, you can call the blur method to apply the blur effect. The stage must be initialised as transparent (`StageStyle.TRANSPARENT`) or JavaFX will have an opaque window decoration background so the blur effect will not be visible.

You might also need to set the backgrounds of the panes and scenes as transparent/none.
```java
public void start(Stage primaryStage) {
    AnchorPane root = new AnchorPane();
    root.setBackground(Background.EMPTY);
    
    Scene scene = new Scene(root, 640.0, 480.0);
    scene.setFill(Color.TRANSPARENT);

    primaryStage.initStyle(StageStyle.TRANSPARENT);

    primaryStage.setScene(scene);
    primaryStage.show();

    // Blur effect must be called after the stage is visible
    Blur.applyBlur(primaryStage, Blur.ACRYLIC);
}
```

The second argument of `applyBlur(stage, blurType)` decides which method of blur should be used. The following blur types are available:

- TRANSPARENT
    - No blur, the window background is just transparent.
- BLUR_BEHIND
    - Slight blur behind window (see example image)
- ACRYLIC
    - Windows acrylic blur. Stronger blur than `BLUR_BEHIND`.
 
## Compatibility
So far, only the native functions for Windows 10 have been provided.