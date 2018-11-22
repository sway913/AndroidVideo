
#include <cstring>
#include "tex_transform_util.h"


static const float tex_coords[] = {
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
};

static const float vertex_coords[] = {
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
};


void get_vertex_coords(float *data) {
    memcpy(data, vertex_coords, sizeof(vertex_coords));
}

void get_tex_coords(float *data) {
    memcpy(data, tex_coords, sizeof(tex_coords));
}

size_t get_vertex_size() {
    return sizeof(vertex_coords);
}

size_t get_tex_size() {
    return sizeof(tex_coords);
}