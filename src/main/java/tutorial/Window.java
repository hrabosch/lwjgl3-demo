package tutorial;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

    private final long windowHandle;
    private int width;
    private int height;
    private Callable<Void> resizeFunction;

    public Window(String title, WindowOptions options, Callable<Void> resizeFunction) {
        this.resizeFunction = resizeFunction;

        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Cannot init GLFW!");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        if (options.compatibleProfile) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        }

        if (options.width > 0 && options.height > 0) {
            this.width = options.width;
            this.height = options.height;
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            width = vidMode.width();
            height = vidMode.height();
        }

        windowHandle = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Cannot create window instance");
        }

        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> resized(w, h));

        GLFW.glfwSetErrorCallback((int errorCode, long msgPtr) ->
                LOGGER.error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr))
        );

        GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            keyCallBack(key, action);
        });

        GLFW.glfwMakeContextCurrent(windowHandle);

        if (options.fps > 0) {
            GLFW.glfwSwapInterval(0);
        } else {
            GLFW.glfwSwapInterval(1);
        }

        GLFW.glfwShowWindow(windowHandle);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        GLFW.glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);
        width = arrWidth[0];
        height = arrHeight[0];
    }

    public void keyCallBack(int key, int action) {
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
            GLFW.glfwSetWindowShouldClose(windowHandle, true);
        }
    }

    public void cleanup() {
        Callbacks.glfwFreeCallbacks(windowHandle);
        GLFW.glfwDestroyWindow(windowHandle);
        GLFW.glfwTerminate();
        GLFWErrorCallback callback = GLFW.glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    protected void resized(int width, int height) {
        this.width = width;
        this.height = height;
        try {
            resizeFunction.call();
        } catch (Exception ex) {
            LOGGER.error("Error calling resize callback", ex);
        }
    }

    public void update() {
        GLFW.glfwSwapBuffers(windowHandle);
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    public static class WindowOptions {
        public boolean compatibleProfile;
        public int fps;
        public int height;
        public int ups = Engine.TARGET_UPS;
        public int width;
    }
}
