package vehicleshop;

import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import vehicleshop.components.AnimatedBackground;

public class App extends Application {

    private static Scene scene;
    private static AnimatedBackground animatedBackground;
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().contains("linux");

    @Override
    public void start(Stage stage) throws Exception {
        // Apply AtlantaFX Dracula theme (dark, modern theme)
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());

        // Load cross-platform fonts
        loadCrossPlatformFonts();

        // Create animated background
        animatedBackground = new AnimatedBackground();

        // Load the main FXML view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
        Parent root = loader.load();

        // Create layered layout with background behind UI
        StackPane layeredRoot = new StackPane();
        layeredRoot.getChildren().addAll(animatedBackground, root);

        // Apply our custom CSS on top of AtlantaFX
        scene = new Scene(layeredRoot, 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        // Cross-platform window settings
        stage.setTitle("⚡ Vehicle Shop Management System");
        stage.setScene(scene);

        // Platform-specific minimum sizes
        if (IS_WINDOWS) {
            stage.setMinWidth(1050);
            stage.setMinHeight(700);
        } else {
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
        }

        // Stop animation on close
        stage.setOnCloseRequest(e -> {
            animatedBackground.stopAnimation();
            Platform.exit();
        });

        stage.show();
    }

    /**
     * Load cross-platform compatible fonts
     */
    private void loadCrossPlatformFonts() {
        // Try to load fonts that are commonly available
        String[] preferredFonts = { "JetBrains Mono", "Fira Code", "Cascadia Code", "Consolas", "Monospace",
                "DejaVu Sans Mono", "Liberation Mono" };

        for (String fontName : preferredFonts) {
            Font testFont = Font.font(fontName, 12);
            if (testFont.getName().toLowerCase().contains(fontName.toLowerCase().split(" ")[0])) {
                System.out.println("Using font: " + fontName);
                break;
            }
        }
    }

    public static void main(String[] args) {
        // Enable hardware acceleration hints for better performance
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        // Platform-specific optimizations
        if (IS_LINUX) {
            // Better font rendering on Linux
            System.setProperty("prism.text", "t2k");
        } else if (IS_WINDOWS) {
            // DirectX rendering on Windows
            System.setProperty("prism.order", "d3d,sw");
        }

        launch(args);
    }

    public static Scene getScene() {
        return scene;
    }

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean isLinux() {
        return IS_LINUX;
    }
}
