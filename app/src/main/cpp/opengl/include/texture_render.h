

#ifndef __TEXTURE_RENDER__
#define __TEXTURE_RENDER__

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#define TEXTURE_2D GL_TEXTURE_2D
#define TEXTURE_OES GL_TEXTURE_EXTERNAL_OES

class TextureRender {
public:
    TextureRender(GLenum target);
    ~TextureRender();
    int init();
    int createTexId();
    int draw(GLuint texture);
    void release();
private:

    float *_vertex_coords;
    float *_tex_coords;

    GLenum _target;
    GLuint _program;
    GLuint _texture_id;

    GLint _aPosition_loc;
    GLint _aTextureCoord_loc;
    GLint _uTexrue_loc;



};



#endif
