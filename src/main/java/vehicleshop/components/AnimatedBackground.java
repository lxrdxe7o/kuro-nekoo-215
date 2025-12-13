package vehicleshop.components;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated geometric background with floating shapes and particles
 */
public class AnimatedBackground extends Pane {

    private Canvas canvas;
    private AnimationTimer animationTimer;
    private List<GeometricShape> shapes;
    private List<Particle> particles;
    private Random random;
    private long lastTime;

    // Colors
    private static final Color BG_COLOR = Color.web("#0a0a0f");
    private static final Color ACCENT_1 = Color.web("#00ff88", 0.3);
    private static final Color ACCENT_2 = Color.web("#00d4ff", 0.2);
    private static final Color ACCENT_3 = Color.web("#ff0080", 0.15);

    public AnimatedBackground() {
        random = new Random();
        shapes = new ArrayList<>();
        particles = new ArrayList<>();

        canvas = new Canvas();
        getChildren().add(canvas);

        // Bind canvas size to pane size
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        // Initialize shapes when size is available
        widthProperty().addListener((obs, oldVal, newVal) -> initializeShapes());
        heightProperty().addListener((obs, oldVal, newVal) -> initializeShapes());

        // Start animation
        startAnimation();
    }

    private void initializeShapes() {
        if (getWidth() <= 0 || getHeight() <= 0)
            return;

        shapes.clear();
        particles.clear();

        // Create floating geometric shapes
        for (int i = 0; i < 8; i++) {
            shapes.add(new GeometricShape(random.nextDouble() * getWidth(), random.nextDouble() * getHeight(),
                    30 + random.nextDouble() * 80, random.nextInt(4), // 0=triangle, 1=square, 2=hexagon, 3=circle
                    getRandomAccentColor(), 0.3 + random.nextDouble() * 0.7, random.nextDouble() * 360));
        }

        // Create particles
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle(random.nextDouble() * getWidth(), random.nextDouble() * getHeight(),
                    1 + random.nextDouble() * 3, getRandomAccentColor()));
        }
    }

    private Color getRandomAccentColor() {
        double r = random.nextDouble();
        if (r < 0.4)
            return ACCENT_1;
        else if (r < 0.7)
            return ACCENT_2;
        else
            return ACCENT_3;
    }

    private void startAnimation() {
        lastTime = System.nanoTime();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                update(deltaTime);
                render();
            }
        };
        animationTimer.start();
    }

    private void update(double deltaTime) {
        double w = getWidth();
        double h = getHeight();

        if (w <= 0 || h <= 0)
            return;

        // Update shapes
        for (GeometricShape shape : shapes) {
            shape.update(deltaTime, w, h);
        }

        // Update particles
        for (Particle particle : particles) {
            particle.update(deltaTime, w, h);
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        if (w <= 0 || h <= 0)
            return;

        // Clear with background color
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, w, h);

        // Draw gradient overlay
        RadialGradient gradient = new RadialGradient(0, 0, w * 0.3, h * 0.3, Math.max(w, h) * 0.8, false,
                CycleMethod.NO_CYCLE, new Stop(0, Color.web("#1a1a28", 0.3)), new Stop(1, Color.TRANSPARENT));
        gc.setFill(gradient);
        gc.fillRect(0, 0, w, h);

        // Draw grid lines
        gc.setStroke(Color.web("#1a1a28", 0.5));
        gc.setLineWidth(1);
        double gridSize = 50;
        for (double x = 0; x < w; x += gridSize) {
            gc.strokeLine(x, 0, x, h);
        }
        for (double y = 0; y < h; y += gridSize) {
            gc.strokeLine(0, y, w, y);
        }

        // Draw shapes
        for (GeometricShape shape : shapes) {
            shape.render(gc);
        }

        // Draw particles
        for (Particle particle : particles) {
            particle.render(gc);
        }
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    // Inner class for geometric shapes
    private class GeometricShape {
        double x, y, size, rotation, rotationSpeed;
        int shapeType;
        Color color;
        double speed;
        double vx, vy;

        GeometricShape(double x, double y, double size, int shapeType, Color color, double speed, double rotation) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.shapeType = shapeType;
            this.color = color;
            this.speed = speed;
            this.rotation = rotation;
            this.rotationSpeed = (random.nextDouble() - 0.5) * 30;
            this.vx = (random.nextDouble() - 0.5) * speed * 20;
            this.vy = (random.nextDouble() - 0.5) * speed * 20;
        }

        void update(double deltaTime, double w, double h) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            rotation += rotationSpeed * deltaTime;

            // Wrap around screen
            if (x < -size)
                x = w + size;
            if (x > w + size)
                x = -size;
            if (y < -size)
                y = h + size;
            if (y > h + size)
                y = -size;
        }

        void render(GraphicsContext gc) {
            gc.save();
            gc.translate(x, y);
            gc.rotate(rotation);
            gc.setStroke(color);
            gc.setLineWidth(2);

            double halfSize = size / 2;

            switch (shapeType) {
            case 0: // Triangle
                double[] xPoints = { 0, halfSize, -halfSize };
                double[] yPoints = { -halfSize, halfSize, halfSize };
                gc.strokePolygon(xPoints, yPoints, 3);
                break;
            case 1: // Square
                gc.strokeRect(-halfSize, -halfSize, size, size);
                break;
            case 2: // Hexagon
                double[] hxPoints = new double[6];
                double[] hyPoints = new double[6];
                for (int i = 0; i < 6; i++) {
                    double angle = Math.PI / 3 * i - Math.PI / 2;
                    hxPoints[i] = Math.cos(angle) * halfSize;
                    hyPoints[i] = Math.sin(angle) * halfSize;
                }
                gc.strokePolygon(hxPoints, hyPoints, 6);
                break;
            case 3: // Circle
                gc.strokeOval(-halfSize, -halfSize, size, size);
                break;
            }

            gc.restore();
        }
    }

    // Inner class for particles
    private class Particle {
        double x, y, size;
        Color color;
        double vx, vy;
        double twinkle;
        double twinkleSpeed;

        Particle(double x, double y, double size, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.vx = (random.nextDouble() - 0.5) * 15;
            this.vy = (random.nextDouble() - 0.5) * 15;
            this.twinkle = random.nextDouble() * Math.PI * 2;
            this.twinkleSpeed = 1 + random.nextDouble() * 2;
        }

        void update(double deltaTime, double w, double h) {
            x += vx * deltaTime;
            y += vy * deltaTime;
            twinkle += twinkleSpeed * deltaTime;

            // Wrap around
            if (x < 0)
                x = w;
            if (x > w)
                x = 0;
            if (y < 0)
                y = h;
            if (y > h)
                y = 0;
        }

        void render(GraphicsContext gc) {
            double opacity = 0.3 + 0.7 * (0.5 + 0.5 * Math.sin(twinkle));
            gc.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity() * opacity));
            gc.fillOval(x - size / 2, y - size / 2, size, size);
        }
    }
}
