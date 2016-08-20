package pl.marchuck.ninjaworlds;

import android.content.Context;
import android.opengl.GLSurfaceView;


/**
 * @author Lukasz Marczak
 * @since 09.08.16.
 */
public interface OpenGLProxy {
    Context getBaseContext();

    GLSurfaceView getSurfaceView();

}
