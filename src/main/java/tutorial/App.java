package tutorial;

public class App {
    public static void main(String[] args) {
        GameCore gameCore = new CustomGameCore();
        Engine engine = new Engine("Not quite sure what this will be", new Window.WindowOptions(), gameCore);
        engine.start();
    }
}
