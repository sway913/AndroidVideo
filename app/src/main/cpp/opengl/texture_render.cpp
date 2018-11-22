

#include "include/texture_render.h"
#include "opengl_helper.h"
#include "tex_transform_util.h"



const static char *VERTER_SHADER  =
        "attribute vec4 aPosition;\n"
        "attribute vec4 aTextureCoord;\n"
        "varying vec2 vTextureCoord;\n"
        "void main() {\n"
        "   gl_Position = aPosition;\n"
        "   vTextureCoord = aTextureCoord.xy;\n"
        "}\n";

const static char *FRAGMENT_2D_SHADER =
        "precision mediump float;\n"
        "varying vec2 vTextureCoord;\n"
        "uniform sampler2D uTexrue;\n"
        "void main() {\n"
        "   gl_FragColor = texture2D(uTexrue, vTextureCoord);\n"
        "}\n";

const static char *FRAGMENT_OES_SHADER =
        "#extension GL_OES_EGL_image_external : require\n"
        "precision mediump float;\n"
        "varying vec2 vTextureCoord;\n"
        "uniform samplerExternalOES uTexrue;\n"
        "void main() {\n"
        "   gl_FragColor = texture2D(uTexrue, vTextureCoord);\n"
        "}\n";



TextureRender::TextureRender(GLenum target)
: _target(target)
, _texture_id(0)
, _program(0)
{
    _tex_coords = static_cast<float *>(malloc(get_tex_size()));
    _vertex_coords = static_cast<float *>(malloc(get_vertex_size()));
    get_tex_coords(_tex_coords);
    get_vertex_coords(_vertex_coords);
}

TextureRender::~TextureRender()
{
    free(_tex_coords);
    free(_vertex_coords);
}

int TextureRender::init()
{
    switch (_target) {
        case GL_TEXTURE_2D:
            _program = glhelp_create_program(VERTER_SHADER, FRAGMENT_2D_SHADER);
            break;
        case GL_TEXTURE_EXTERNAL_OES:
            _program = glhelp_create_program(VERTER_SHADER, FRAGMENT_OES_SHADER);
            break;
        default:
            goto error;
    }

    _aPosition_loc = glGetAttribLocation(_program, "aPosition");
    if (_aPosition_loc < 0)
        goto error;
    _aTextureCoord_loc = glGetAttribLocation(_program, "aTextureCoord");
    if (_aTextureCoord_loc < 0)
        goto error;
    _uTexrue_loc = glGetUniformLocation(_program, "uTexrue");
    if (_uTexrue_loc < 0)
        goto error;

    return 0;
error:
    return -1;
}

int TextureRender::draw(GLuint texture)
{
    glUseProgram(_program);
    CHECK_ERROR(error);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(_target, texture);
    CHECK_ERROR(error);

    glEnableVertexAttribArray(static_cast<GLuint>(_aPosition_loc));
    glVertexAttribPointer(static_cast<GLuint>(_aPosition_loc), 2, GL_FLOAT, GL_FALSE, 0, _vertex_coords);
    CHECK_ERROR(error);

    glEnableVertexAttribArray(static_cast<GLuint>(_aTextureCoord_loc));
    glVertexAttribPointer(static_cast<GLuint>(_aTextureCoord_loc), 2, GL_FLOAT, GL_FALSE, 0, _tex_coords);
    CHECK_ERROR(error);

    glUniform1i(_uTexrue_loc, 0);
    CHECK_ERROR(error);

    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    CHECK_ERROR(error);

    glBindTexture(_target, 0);
    glDisableVertexAttribArray(static_cast<GLuint>(_aPosition_loc));
    glDisableVertexAttribArray(static_cast<GLuint>(_aTextureCoord_loc));
    CHECK_ERROR(error);

error:
    return -1;
}


int TextureRender::createTexId()
{
    _texture_id = glhelp_create_texture_id(_target);
    return _texture_id;
}

void TextureRender::release()
{
    const GLuint ids[] = {_texture_id};
    glDeleteProgram(_program);
    glDeleteTextures(1, ids);
}