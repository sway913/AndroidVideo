
#include <include/hash_compare.h>

hash_t hash(char const* str)
{
    hash_t ret{basis};
    while(*str){
        ret ^= *str;
        ret *= prime;
        str++;
    }
    return ret;
}