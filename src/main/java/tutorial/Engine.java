package tutorial;

public class Engine {

    public static final int TARGET_UPS = 30;
    private final GameCore gameCore;
    private final Window window;
    private Render renderer;
    private boolean running;
    private Scene scene;
    private int targetFps;
    private int targetUps;

    public Engine(String windowTitle, Window.WindowOptions options, GameCore gameCore) {
        this.window = new Window(windowTitle, options, () -> {
            resize();
            return null;
        });
        this.targetFps = options.fps;
        this.targetUps = options.ups;
        this.gameCore = gameCore;
        this.renderer = new Render();
        this.scene = new Scene();
        gameCore.init(window, scene, renderer);
        running = true;
    }

    private void cleanup() {
        gameCore.cleanup();
        renderer.cleanup();
        scene.cleanup();
        window.cleanup();
    }

    private void resize() {
    }

    private void run() {
        long initialTime = System.currentTimeMillis();
        float timeU = 1000.0f / targetUps;
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
        float deltaUpdate = 0;
        float deltaFps = 0;

        long updateTime = initialTime;
        while (running && !window.windowShouldClose()) {
            window.pollEvents();

            long now = System.currentTimeMillis();
            deltaUpdate += (now - initialTime) / timeU;
            deltaFps += (now - initialTime) / timeR;

            if (targetFps <= 0 || deltaFps >= 1) {
                gameCore.input(window, scene, now - initialTime);
            }

            if (deltaUpdate >= 1) {
                long diffTimeMillis = now - updateTime;
                gameCore.update(window, scene, diffTimeMillis);
                updateTime = now;
                deltaUpdate--;
            }

            if (targetFps <= 0 || deltaFps >= 1) {
                renderer.render(window, scene);
                deltaFps--;
                window.update();
            }
            initialTime = now;
        }

        cleanup();
    }

    public void start() {
        running = true;
        run();
    }

    public void stop() {
        running = false;
    }

}
