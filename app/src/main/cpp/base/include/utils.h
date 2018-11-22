
#ifndef __UTILS__
#define __UTILS__

#define CHECK_NULL_CODE(obj, code)         if (!obj)  return code;
#define CHECK_NULL(obj)                    CHECK_NULL_CODE(obj, -1)
#define CHECK_RESULT(result)               if (result < 0) return result;
#define CHECK_NULL_GOTO(obj, lebal)        if (!obj)  goto lebal;
#define CHECK_RESULT_GOTO(result, lebal)   if (result < 0) goto lebal;

#endif
