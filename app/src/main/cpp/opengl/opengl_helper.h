
#ifndef __OPENGL_HELPER__
#define __OPENGL_HELPER__

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

GLuint glhelp_create_texture_id(GLenum target);
GLuint glhelp_create_program(const GLchar *vertex_shader, const GLchar *fragment_shader);

#define CHECK_ERROR(label) if (glGetError() != GL_NO_ERROR) goto label;
#define CHECK_RESULT(result, label) if (result == 0) goto label;

#endif
