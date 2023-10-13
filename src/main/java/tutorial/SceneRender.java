package tutorial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.*;

public class SceneRender {

    private ShaderInjector shaderProgram;
    private UniformsMap uniformsMap;


    public SceneRender() {
        List<ShaderInjector.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderInjector.ShaderModuleData("shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderInjector.ShaderModuleData("shaders/scene.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderInjector(shaderModuleDataList);
        createUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public void render(Scene scene) {
        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());

        Collection<Model> models = scene.getModelMap().values();
        for (Model model : models) {
            model.getMeshList().stream().forEach(mesh -> {
                glBindVertexArray(mesh.getVaoId());
                List<Entity> entities = model.getEntitiesList();
                for (Entity entity : entities) {
                    uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                    glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                }
            });
        }

        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    private void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("modelMatrix");
    }
}
