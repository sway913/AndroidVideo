
#include "opengl_helper.h"


GLuint glhelp_create_texture_id(GLenum target)
{
    GLuint id;
    glGenTextures(1, &id);
    CHECK_ERROR(error);

    glBindTexture(target, id);
    CHECK_ERROR(error);
    glTexParameterf(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameterf(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    CHECK_ERROR(error);

    glBindTexture(target, 0);

    return id;
error:
    return 0;
}


GLuint glhelp_load_shader(GLenum type, const GLchar *shader_src)
{
    GLint state;
    GLuint shader = glCreateShader(type);
    CHECK_ERROR(error);
    glShaderSource(shader, 1, &shader_src, NULL);
    glCompileShader(shader);
    glGetShaderiv(shader, GL_COMPILE_STATUS, &state);
    CHECK_RESULT(state, error);
    return shader;
error:
    glDeleteShader(shader);
    return 0;
}


GLuint glhelp_create_program(const GLchar *vertex_shader, const GLchar *fragment_shader)
{
    GLint state;
    GLuint vertex, fragment;
    GLuint program = glCreateProgram();
    CHECK_ERROR(error);
    vertex = glhelp_load_shader(GL_VERTEX_SHADER, vertex_shader);
    CHECK_RESULT(vertex, error);
    fragment = glhelp_load_shader(GL_FRAGMENT_SHADER, fragment_shader);
    CHECK_RESULT(fragment, error);

    glAttachShader(program, vertex);
    CHECK_ERROR(error);
    glAttachShader(program, fragment);
    CHECK_ERROR(error);
    glLinkProgram(program);
    glGetProgramiv(program, GL_LINK_STATUS, &state);
    CHECK_RESULT(state, error);

    glDeleteShader(vertex);
    glDeleteShader(fragment);
    CHECK_ERROR(error);

    return program;
error:
    glDeleteProgram(program);
    return 0;
}