
#ifndef __HASH_COMPARE__
#define __HASH_COMPARE__

#include <cstdint>

/**
 * 利用c++11特性，使得可以通过switch可以识别string
 * 使用方法：
 * switch (hash(str.c_str())) {
 *       case "str1"_hash:
 *           //do something
 *           break;
 *       case "str2"_hash:
 *           //do something
 *           break;
 *   }
 */

typedef std::uint64_t hash_t;

constexpr hash_t prime = 0x100000001B3ull;
constexpr hash_t basis = 0xCBF29CE484222325ull;

hash_t hash(char const *str);

//PS： debug得知，在h定义，在cpp实现的话，constexpr无效。

constexpr hash_t hash_compile_time(char const* str, hash_t last_value = basis)
{
    return *str ? hash_compile_time(str+1, (*str ^ last_value) * prime) : last_value;
}
constexpr unsigned long long operator "" _hash(char const* p, size_t)
{
    return hash_compile_time(p);
}

#endif
